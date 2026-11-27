package tn.iset.m2glnt.server.storage;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;

import static org.junit.jupiter.api.Assertions.*;

class CalendarMemoryDAOTest {

    @org.junit.jupiter.api.Test
    void get() {
        CalendarMemoryDAO dao = new CalendarMemoryDAO();
        assertEquals(Optional.empty(), dao.get(0));

        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        // ✅ Ajout du 4ème paramètre : 0
        CalendarSlotDTO cal = new CalendarSlotDTO(0, "CM GLA", new TimeInterval(startCM, endCM), 0);
        dao.create(cal);
        assertEquals(Optional.of(cal), dao.get(0));
    }

    @org.junit.jupiter.api.Test
    void getAll() {
        CalendarMemoryDAO dao = new CalendarMemoryDAO();
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        // ✅ Ajout du 4ème paramètre : 0
        CalendarSlotDTO cal = new CalendarSlotDTO(0, "CM GLA", new TimeInterval(startCM, endCM), 0);
        dao.create(cal);
        List<CalendarSlotDTO> slots = dao.getAll();
        assertEquals(1, slots.size());
        assertEquals(cal, slots.getFirst());
    }

    @org.junit.jupiter.api.Test
    void create() {
        CalendarMemoryDAO dao = new CalendarMemoryDAO();
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        // ✅ Ajout du 4ème paramètre : 0
        CalendarSlotDTO cal = new CalendarSlotDTO(0, "CM GLA", new TimeInterval(startCM, endCM), 0);
        int id = dao.create(cal);
        assertEquals(Optional.of(cal), dao.get(0));
        assertEquals(0, id);
    }

    @org.junit.jupiter.api.Test
    void update() {

    }

    @org.junit.jupiter.api.Test
    void delete() {

    }
}