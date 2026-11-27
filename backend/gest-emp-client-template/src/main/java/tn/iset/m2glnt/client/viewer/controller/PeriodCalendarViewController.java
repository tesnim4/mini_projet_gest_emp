package tn.iset.m2glnt.client.viewer.controller;

import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import tn.iset.m2glnt.client.model.Calendar;
import tn.iset.m2glnt.client.model.SimpleCalendar;
import tn.iset.m2glnt.client.model.SimpleSlot;
import tn.iset.m2glnt.client.model.Slot;
import tn.iset.m2glnt.client.util.DateInterval;
import tn.iset.m2glnt.client.util.DayGenerator;
import tn.iset.m2glnt.client.viewer.presenter.CalendarPresenter;
import tn.iset.m2glnt.client.viewer.presenter.PeriodCalendarPresenter;
import tn.iset.m2glnt.client.viewer.presenter.dialog.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeriodCalendarViewController implements CalendarViewController {
    private final CalendarPresenter calendarPresenter;
    private final CalendarViewConfiguration config;
    private DayGenerator dayGenerator;
    private final Calendar calendar;
    private final SlotFormFactory slotFormFactory;
    private final Map<Integer, Slot> slotsById = new HashMap<>();

    public PeriodCalendarViewController(Calendar calendar, CalendarViewConfiguration config) {
        this.config = config;
        this.calendar = calendar;
        
        // Initialisation de la date de début (lundi de la semaine en cours)
        setStartDate(config.getPeriodStartDateContaining(LocalDate.now()));
        
        List<LocalTime> times = config.getTimeIntervalGenerator().getStartTimesOfIntervals();
        times.add(config.getTimeIntervalGenerator().getEndTime());
        this.slotFormFactory = new SimpleSlotFormFactory(times, config.getPossibleDurations().getDurations());
        this.calendarPresenter = new PeriodCalendarPresenter(this, dayGenerator, config.getTimeIntervalGenerator());
        
        refreshSlots();
    }

    @Override 
    public void handleNext() { 
        updateStartDate(dayGenerator.getStartDate().plus(config.getTotalPeriod())); 
        refreshSlots(); 
    }

    @Override 
    public void handlePrevious() { 
        updateStartDate(dayGenerator.getStartDate().minus(config.getTotalPeriod())); 
        refreshSlots(); 
    }

    private void updateStartDate(LocalDate newStart) { 
        setStartDate(newStart); 
        calendarPresenter.updateDays(dayGenerator); 
    }

    private void setStartDate(LocalDate start) { 
        dayGenerator = new DayGenerator(new DateInterval(start, start.plus(config.getPrintablePeriod()))); 
    }

    @Override
    public void handleSlotEdition(int id) {
        Slot slot = slotsById.get(id);
        if (slot == null) return;
        
        Dialog<SlotFormResult> dialog = slotFormFactory.createCalendarEventDialog(slot);
        dialog.showAndWait().ifPresent(r -> {
            if (r.slotFormAction() == SlotFormAction.DELETE) {
                deleteSlot(id);
            } else if (r.slotFormAction() == SlotFormAction.CONFIRM && r.slotFormContent() != null) {
                var f = r.slotFormContent();
                Slot edited = new SimpleSlot(
                        f.description(), 
                        LocalDateTime.of(f.startDate(), f.startTime()), 
                        f.duration(), 
                        id, 
                        slot.versionNumber() + 1
                );
                
                // ✅ CORRECTION : update renvoie un int (version). On vérifie si > 0
                if (calendar.update(edited) > 0) {
                    refreshSlots();
                }
            }
        });
    }

    private void deleteSlot(int id) {
        if (calendar.delete(slotsById.get(id))) {
            refreshSlots();
        }
    }

       @Override
    public void handleSlotCreation() {
        Dialog<SlotFormResult> dialog = slotFormFactory.createCalendarEventDialog(LocalDateTime.now(), config.getDefaultDurationIndex());
        dialog.showAndWait().ifPresent(r -> {
            if (r.slotFormAction() == SlotFormAction.CONFIRM && r.slotFormContent() != null) {
                var f = r.slotFormContent();
                Slot s = new SimpleSlot(f.description(), LocalDateTime.of(f.startDate(), f.startTime()), f.duration(), -1, 0);
                
                int newId = calendar.create(s);
                if (newId > 0) {
                    // ✅ ON FORCE L'AFFICHAGE AVANT LE RE-REQUÊTAGE
                    Slot nouveauCreneau = new SimpleSlot(s.description(), s.startDateTime(), s.duration(), newId, 0);
                    
                    // On l'ajoute directement au visuel
                    slotsById.put(newId, nouveauCreneau);
                    calendarPresenter.addSlotView(nouveauCreneau, config.colorOfSlots(), this::handleSlotEdition);
                    
                    // Puis on rafraîchit en arrière-plan
                    refreshSlots(); 
                }
            }
        });
    }

    @Override
    public Scene getScene() {
        calendarPresenter.updateDays(dayGenerator);
        calendarPresenter.updateTimeIntervals(config.getTimeIntervalGenerator());
        refreshSlots();
        return calendarPresenter.getScene();
    }

    private void refreshSlots() {
        slotsById.clear();
        calendarPresenter.clearSlotViews();
        
        // ✅ On vide le cache pour forcer la récupération depuis le serveur
        if (calendar instanceof SimpleCalendar) {
            ((SimpleCalendar) calendar).invalidate();
        }
        
        for (Slot slot : calendar.getAllSlotsBetween(dayGenerator.getStartDate(), dayGenerator.getEndDate().minusDays(1))) {
            if (dayGenerator.getDayIndex(slot.startDateTime().toLocalDate()) != -1) {
                slotsById.put(slot.id(), slot);
                calendarPresenter.addSlotView(slot, config.colorOfSlots(), this::handleSlotEdition);
            }
        }
    }
}