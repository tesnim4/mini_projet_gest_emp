package tn.iset.m2gln.server.model;

import java.util.ArrayList;
import java.util.List;


/**
 * The calendar
 */
public class Calendar {

    /**
     * The slot composing the calendar
     */
    private ArrayList<CalendarSlot> slots;

    /**
     * Constructor
     */
    public Calendar() {
        this.slots = new ArrayList<>();
    }

    /**
     * Adding slot to the calendar
     * 
     * @param slot the new slot
     */
    public void addSlot(CalendarSlot slot) {
        this.slots.add(slot);
    }

    /**
     * Get the slots
     * 
     * @return the list of slots
     */
    public List<CalendarSlot> getSlots() {
        return slots;
    }

    /**
 * Récupère un créneau par son identifiant.
 *
 * @param id identifiant du créneau
 * @return le créneau correspondant, ou null si non trouvé
 */
public CalendarSlot getSlotById(int id) {
    return this.slots.stream()
            .filter(slot -> slot.getId() == id)
            .findFirst()
            .orElse(null);
}

/**
 * Ajoute un nouveau créneau avec un identifiant auto-généré.
 *
 * @param slot le créneau à ajouter
 * @return l'identifiant du créneau créé
 */
public int addNewSlot(CalendarSlot slot) {
    int newId = this.slots.stream()
            .mapToInt(CalendarSlot::getId)
            .max()
            .orElse(-1) + 1;
    slot.setId(newId);
    this.slots.add(slot);
    return newId;
}
/**
 * Met à jour un créneau existant.
 *
 * @param slot le créneau avec les nouvelles données
 * @return true si la mise à jour a réussi, false sinon
 */
public boolean updateSlot(CalendarSlot slot) {
    for (int i = 0; i < this.slots.size(); i++) {
        if (this.slots.get(i).getId() == slot.getId()) {
            this.slots.set(i, slot);
            return true;
        }
    }
    return false;
}

/**
 * Supprime un créneau par son identifiant.
 *
 * @param id l'identifiant du créneau à supprimer
 * @return true si la suppression a réussi, false sinon
 */
public boolean deleteSlotById(int id) {
    return this.slots.removeIf(slot -> slot.getId() == id);
}
}
