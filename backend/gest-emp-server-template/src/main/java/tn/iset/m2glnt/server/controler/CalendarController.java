package tn.iset.m2glnt.server.controler;

import tn.iset.m2gln.server.model.Calendar;
import tn.iset.m2gln.server.model.CalendarSlot;
import tn.iset.m2gln.server.model.DAO;
import tn.iset.m2glnt.server.dto.CalendarDTO;
import tn.iset.m2glnt.server.dto.CalendarSlotDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;
import tn.iset.m2glnt.server.model.technical.Convertor;
import tn.iset.m2glnt.server.storage.CalendarMemoryDAO;
import io.javalin.http.Context;
import java.util.Map;
import java.util.List;

public class CalendarController {

    /**
     * The calendar (model)
     */
    private Calendar calendar;

    /**
     * The DAO accessor
     */
    private DAO<Integer, CalendarSlotDTO> dao;

    /**
     * Constructor
     */
    public CalendarController() {
        this.calendar = new Calendar();
        this.dao = new CalendarMemoryDAO(true);
        loadAll();
    }

    /**
     * Create all into the DAO and get id
     */
    private void loadAll () {
        List<CalendarSlotDTO> calendarSlots = dao.getAll();
        for (CalendarSlotDTO calendarSlotDTO : calendarSlots) {
            calendar.addSlot(Convertor.fromCalendarSlotDTO(calendarSlotDTO));
        }
    }

    /**
     * Get the current slot from the model
     * @param timeInterval time intervals
     * @return the corresponding slots
     */
    public CalendarDTO getCalendarSlotsIn(TimeInterval timeInterval) {
        List<CalendarSlot> timeslots = this.calendar.getSlots()
                .stream()
                .filter(calendarSlot -> calendarSlot.getTime_begin().isAfter(timeInterval.start()) &&
                        calendarSlot.getTime_end().isBefore(timeInterval.end()))
                .toList();
        List<CalendarSlotDTO> calendarSlots = timeslots.stream().map(Convertor::toCalendarSlotDTO).toList();
        return new CalendarDTO(calendarSlots);
    }

    public void getSlotById(Context ctx) {
    try {
        int id = Integer.parseInt(ctx.pathParam("id"));
        CalendarSlot slot = calendar.getSlotById(id);

        if (slot == null) {
            ctx.status(404).json(Map.of("error", "Slot " + id + " non trouvé"));
            return;
        }

        CalendarSlotDTO dto = Convertor.toCalendarSlotDTO(slot);
        ctx.status(200).json(dto);
    } catch (NumberFormatException e) {
        ctx.status(400).json(Map.of("error", "Identifiant invalide"));
    }
}

public void createSlot(Context ctx) {
    try {
        CalendarSlotDTO dto = ctx.bodyAsClass(CalendarSlotDTO.class);

        CalendarSlot slot = Convertor.fromCalendarSlotDTO(dto);

        int newId = calendar.addNewSlot(slot);

        dao.create(dto);

        CalendarSlot created = calendar.getSlotById(newId);
        CalendarSlotDTO createdDTO = Convertor.toCalendarSlotDTO(created);

        ctx.status(201).json(createdDTO);
    } catch (Exception e) {
        ctx.status(400).json(Map.of("error", e.getMessage()));
    }
}
// ═══════════════════════════════════════════════════════════════════
// ✅ PUT /api/slots/{id}  →  mettre à jour un créneau
// ═══════════════════════════════════════════════════════════════════
public void updateSlot(Context ctx) {
    try {
        int id = Integer.parseInt(ctx.pathParam("id"));
        
        // 1. Vérifier si le slot existe
        CalendarSlot existing = calendar.getSlotById(id);
        if (existing == null) {
            ctx.status(404).json(Map.of("error", "Slot " + id + " non trouvé"));
            return;
        }

        // 2. Récupérer les nouvelles données du body
        CalendarSlotDTO dto = ctx.bodyAsClass(CalendarSlotDTO.class);
        
        // 3. Forcer l'ID de l'URL (sécurité)
        CalendarSlot updatedSlot = Convertor.fromCalendarSlotDTO(dto);
        updatedSlot.setId(id);

        // 4. Mettre à jour dans le modèle et le DAO
        calendar.updateSlot(updatedSlot);
        
        CalendarSlotDTO dtoForDao = Convertor.toCalendarSlotDTO(updatedSlot);
        dao.update(dtoForDao);

        // 5. Renvoyer le slot mis à jour
        ctx.status(200).json(dtoForDao);
    } catch (NumberFormatException e) {
        ctx.status(400).json(Map.of("error", "Identifiant invalide"));
    } catch (Exception e) {
        ctx.status(400).json(Map.of("error", e.getMessage()));
    }
}

// ═══════════════════════════════════════════════════════════════════
// ✅ DELETE /api/slots/{id}  →  supprimer un créneau
// ═══════════════════════════════════════════════════════════════════
public void deleteSlot(Context ctx) {
    try {
        int id = Integer.parseInt(ctx.pathParam("id"));

        // 1. Vérifier si le slot existe
        CalendarSlot existing = calendar.getSlotById(id);
        if (existing == null) {
            ctx.status(404).json(Map.of("error", "Slot " + id + " non trouvé"));
            return;
        }

        // 2. Supprimer du modèle
        calendar.deleteSlotById(id);

        // 3. Supprimer du DAO
        CalendarSlotDTO dto = Convertor.toCalendarSlotDTO(existing);
        dao.delete(dto);

        // 4. Renvoyer un succès (204 No Content = succès sans body)
        ctx.status(204);
    } catch (NumberFormatException e) {
        ctx.status(400).json(Map.of("error", "Identifiant invalide"));
    } catch (Exception e) {
        ctx.status(400).json(Map.of("error", e.getMessage()));
    }
}
}
