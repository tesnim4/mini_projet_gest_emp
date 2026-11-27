package tn.iset.m2glnt.client.model;

import java.time.LocalDate;
import java.util.Collection;

public interface Calendar {
    Collection<Slot> getAllSlotsBetween(LocalDate startDate, LocalDate endDate);
    
    int create(Slot slot); // ✅ Renvoie l'ID
    
    int update(Slot slot); // ✅ Renvoie la nouvelle version (au lieu de boolean)
    
    boolean delete(Slot slot);
}