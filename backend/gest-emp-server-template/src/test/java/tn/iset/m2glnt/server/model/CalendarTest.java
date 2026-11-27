package tn.iset.m2glnt.server.model;

import org.junit.jupiter.api.Test;

import tn.iset.m2gln.server.model.Calendar;
import tn.iset.m2gln.server.model.CalendarSlot;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class CalendarTest {

    @Test
    void addSlot() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);

        CalendarSlot calendarSlot = new CalendarSlot(startCM, endCM, "CM GLA");
        Calendar calendar = new Calendar();
        calendar.addSlot(calendarSlot);

        assertEquals(calendarSlot, calendar.getSlots().getFirst());
    }

    @Test
    void getSlots() {
        Calendar calendar = new Calendar();
        assertTrue(calendar.getSlots().isEmpty());
    }
}