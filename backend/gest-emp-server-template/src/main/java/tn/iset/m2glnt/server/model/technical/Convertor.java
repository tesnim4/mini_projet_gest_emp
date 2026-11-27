package tn.iset.m2glnt.server.model.technical;

import tn.iset.m2gln.server.model.Calendar;
import tn.iset.m2gln.server.model.CalendarSlot;
import tn.iset.m2glnt.server.dto.CalendarDTO;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;

import java.util.ArrayList;

public class Convertor {

    public static CalendarSlot fromCalendarSlotDTO(CalendarSlotDTO calendarSlotDTO) {
        // On ignore la version car le modèle serveur (CalendarSlot) ne la gère pas
        return new CalendarSlot(calendarSlotDTO.id(),
                calendarSlotDTO.timeInterval().start(),
                calendarSlotDTO.timeInterval().end(),
                calendarSlotDTO.description());
    }

    public static CalendarSlotDTO toCalendarSlotDTO(CalendarSlot calendarSlot) {
        // ✅ On ajoute '0' comme version par défaut pour le DTO
        return new CalendarSlotDTO(calendarSlot.getId(),
                calendarSlot.getDescription(),
                new TimeInterval(calendarSlot.getTime_begin(), calendarSlot.getTime_end()),
                0 
        );
    }

    public static Calendar fromCalendarDTO(CalendarDTO calendarDTO) {
        Calendar calendar = new Calendar();
        for (CalendarSlotDTO calendarSlotDTO : calendarDTO.calendarSlots()) {
            calendar.addSlot(fromCalendarSlotDTO(calendarSlotDTO));
        }
        return calendar;
    }

    public static CalendarDTO toCalendarDTO(Calendar calendar) {
        ArrayList<CalendarSlotDTO> calendarSlotDTOs = new ArrayList<>();
        for (CalendarSlot slot : calendar.getSlots()) {
            calendarSlotDTOs.add(toCalendarSlotDTO(slot));
        }
        return new CalendarDTO(calendarSlotDTOs);
    }
}