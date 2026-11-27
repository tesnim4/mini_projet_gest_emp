package tn.iset.m2glnt.client.model;

import tn.iset.m2glnt.client.service.dao.CalendarServiceDAO;
import tn.iset.m2glnt.client.service.dao.exceptions.ConnexionException;
import tn.iset.m2glnt.client.service.dto.SlotDTO;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCalendar implements Calendar {
    private final CalendarServiceDAO calendarDAO;
    private final SlotDTOConverter slotDTOConverter = new SimpleSlotDTOConverter();
    private final Map<Period, List<Slot>> cache = new ConcurrentHashMap<>();

    private record Period(LocalDate start, LocalDate end) {}

    public SimpleCalendar(CalendarServiceDAO calendarDAO) {
        this.calendarDAO = calendarDAO;
    }

    @Override
    public Collection<Slot> getAllSlotsBetween(LocalDate startDate, LocalDate endDate) {
        Period period = new Period(startDate, endDate);
        if (cache.containsKey(period)) {
            return cache.get(period);
        }
        try {
            Collection<SlotDTO> dtos = calendarDAO.getAllSlotsIn(startDate, endDate);
            List<Slot> slots = dtos.stream().map(slotDTOConverter::fromDTO).toList();
            cache.put(period, slots);
            return slots;
        } catch (ConnexionException e) {
            return List.of();
        }
    }

    @Override
    public int create(Slot slot) {
        try {
            int newId = calendarDAO.create(slotDTOConverter.toDTO(slot));
            invalidate();
            return newId;
        } catch (ConnexionException e) {
            return -1;
        }
    }

    @Override
    public int update(Slot slot) {
        try {
            int newVersion = calendarDAO.update(slotDTOConverter.toDTO(slot));
            invalidate();
            return newVersion;
        } catch (Exception e) {
            invalidate();
            return -1;
        }
    }

    @Override
    public boolean delete(Slot slot) {
        try {
            calendarDAO.delete(slotDTOConverter.toDTO(slot));
            invalidate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void invalidate() {
        cache.clear();
    }
}