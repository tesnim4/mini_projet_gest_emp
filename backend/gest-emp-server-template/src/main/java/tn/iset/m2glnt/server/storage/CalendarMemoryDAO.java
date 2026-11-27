package tn.iset.m2glnt.server.storage;

import tn.iset.m2gln.server.model.DAO;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CalendarMemoryDAO implements DAO<Integer, CalendarSlotDTO> {
    private final ArrayList<CalendarSlotDTO> elements;
    private int id_sequence;

    public CalendarMemoryDAO() {
        this(false);
    }

    public CalendarMemoryDAO(boolean initialize) {
        this.elements = new ArrayList<>();
        this.id_sequence = 0;
        if (initialize) {
            initialize();
        }
    }

    private void initialize() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 10, 0, 0);
        LocalDateTime startTD = LocalDateTime.of(2025, Month.JANUARY, 8, 10, 15, 0);
        LocalDateTime endTD = LocalDateTime.of(2025, Month.JANUARY, 8, 11, 45, 0);
        LocalDateTime startTP = LocalDateTime.of(2025, Month.JANUARY, 8, 14, 0, 0);
        LocalDateTime endTP = LocalDateTime.of(2025, Month.JANUARY, 8, 15, 30, 0);
        for (int i = 0; i < 6; i++) {
            // ✅ Ajout du 4ème argument (version = 0)
            CalendarSlotDTO cmSlot = new CalendarSlotDTO(-1, "CM GLA", new TimeInterval(startCM, endCM), 0);
            CalendarSlotDTO tdSlot = new CalendarSlotDTO(-1, "TD GLA", new TimeInterval(startTD, endTD), 0);
            CalendarSlotDTO tpSlot = new CalendarSlotDTO(-1, "TP GLA", new TimeInterval(startTP, endTP), 0);
            startCM = startCM.plusWeeks(1); endCM = endCM.plusWeeks(1);
            startTD = startTD.plusWeeks(1); endTD = endTD.plusWeeks(1);
            startTP = startTP.plusWeeks(1); endTP = endTP.plusWeeks(1);
            create(cmSlot);
            create(tdSlot);
            create(tpSlot);
        }
    }

    @Override
    public Optional<CalendarSlotDTO> get(Integer key) {
        return elements.stream().filter(slot -> slot.id() == key).findFirst();
    }

    @Override
    public List<CalendarSlotDTO> getAll() {
        return Arrays.asList(elements.toArray(new CalendarSlotDTO[0]));
    }

    @Override
    public Integer create(CalendarSlotDTO element) {
        // ✅ Ajout du 4ème argument (version = 0)
        CalendarSlotDTO new_element = new CalendarSlotDTO(id_sequence,
                element.description(),
                element.timeInterval(),
                0); 
        elements.add(new_element);
        return id_sequence++;
    }

    @Override
    public int update(CalendarSlotDTO element) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).id() == element.id()) {
                elements.set(i, element);
                return element.id();
            }
        }
        return -1;
    }

    @Override
    public void delete(CalendarSlotDTO element) {
        elements.remove(element);
    }
}