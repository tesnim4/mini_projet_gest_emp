package tn.iset.m2glnt.server.controler;

import tn.iset.m2glnt.server.controler.CalendarController;
import tn.iset.m2glnt.server.dto.CalendarDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class CalendarControllerTest {

    @Test
    void getCalendarSlotsIn() {
        CalendarController calendarController = new CalendarController();

        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 7, 40, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 10, 40, 0);
        TimeInterval timeInterval = new TimeInterval(startCM, endCM);

        CalendarDTO calendarDTO = calendarController.getCalendarSlotsIn(timeInterval);
        assertNotNull(calendarDTO);
        assertEquals(1, calendarDTO.calendarSlots().size());

        LocalDateTime start2 = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 40, 0);
        LocalDateTime end2 = LocalDateTime.of(2025, Month.JANUARY, 8, 9, 40, 0);
        TimeInterval timeInterval2 = new TimeInterval(start2, end2);

        CalendarDTO calendarDTO2 = calendarController.getCalendarSlotsIn(timeInterval2);
        assertNotNull(calendarDTO2);
        assertEquals(0, calendarDTO2.calendarSlots().size());
    }
}