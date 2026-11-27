package tn.iset.m2glnt.server.model.technical;

import tn.iset.m2gln.server.model.Calendar;
import tn.iset.m2gln.server.model.CalendarSlot;
import tn.iset.m2glnt.server.dto.CalendarDTO;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ConvertorTest {

    @Test
    void fromCalendarSlotDTO() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        CalendarSlot calendarSlot = new CalendarSlot(startCM, endCM, "CM GLA");
        CalendarSlotDTO calendarSlotDTO = Convertor.toCalendarSlotDTO(calendarSlot);
        assertEquals(calendarSlot.getId(), calendarSlotDTO.id());
        assertEquals(calendarSlot.getDescription(), calendarSlotDTO.description());
        assertEquals(calendarSlot.getTime_begin(), calendarSlotDTO.timeInterval().start());
        assertEquals(calendarSlot.getTime_end(), calendarSlotDTO.timeInterval().end());
    }

    @Test
    void toCalendarSlotDTO() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        // ✅ Ajout du 4ème paramètre : 0
        CalendarSlotDTO calendarSlotDTO = new CalendarSlotDTO(-1, "CM GLA", new TimeInterval(startCM, endCM), 0);
        CalendarSlot calendarSlot = Convertor.fromCalendarSlotDTO(calendarSlotDTO);
        assertEquals(calendarSlotDTO.id(), calendarSlot.getId());
        assertEquals(calendarSlotDTO.description(), calendarSlot.getDescription());
        assertEquals(calendarSlotDTO.timeInterval().start(), calendarSlot.getTime_begin());
        assertEquals(calendarSlotDTO.timeInterval().end(), calendarSlot.getTime_end());
    }

    @Test
    void fromCalendarDTO() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        CalendarSlot calendarSlot = new CalendarSlot(startCM, endCM, "CM GLA");
        CalendarSlotDTO calendarSlotDTO = Convertor.toCalendarSlotDTO(calendarSlot);
        ArrayList<CalendarSlotDTO> slotsDTO = new ArrayList<>();
        slotsDTO.add(calendarSlotDTO);
        CalendarDTO calendarDTO = new CalendarDTO(slotsDTO);
        Calendar calendar = Convertor.fromCalendarDTO(calendarDTO);
        assertEquals(1, calendar.getSlots().size());
        assertEquals(startCM, calendar.getSlots().getFirst().getTime_begin());
        assertEquals(endCM, calendar.getSlots().getFirst().getTime_end());
    }

    @Test
    void toCalendarDTO() {
        LocalDateTime startCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        LocalDateTime endCM = LocalDateTime.of(2025, Month.JANUARY, 8, 8, 30, 0);
        CalendarSlot calendarSlot = new CalendarSlot(startCM, endCM, "CM GLA");
        Calendar calendar = new Calendar();
        calendar.addSlot(calendarSlot);
        CalendarDTO calendarDTO = Convertor.toCalendarDTO(calendar);
        assertEquals(1, calendarDTO.calendarSlots().size());
        assertEquals(startCM, calendarDTO.calendarSlots().getFirst().timeInterval().start());
        assertEquals(endCM, calendarDTO.calendarSlots().getFirst().timeInterval().end());
    }
}