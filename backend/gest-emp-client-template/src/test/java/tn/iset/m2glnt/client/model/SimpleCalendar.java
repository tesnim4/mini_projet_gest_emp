package tn.iset.m2glnt.client.model;

import tn.iset.m2glnt.client.service.dao.CalendarServiceDAO;
import tn.iset.m2glnt.client.service.dao.exceptions.ConnexionException;
import tn.iset.m2glnt.client.service.dto.SlotDTO;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCalendar implements Calendar {
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);

    private final CalendarServiceDAO calendarDAO;
    private final SlotDTOConverter slotDTOConverter = new SimpleSlotDTOConverter();
    private final Duration ttl;
    private final Clock clock;
    private final Map<Period, CacheEntry> cache = new ConcurrentHashMap<>();

    private record Period(LocalDate start, LocalDate end) {}

    private record CacheEntry(List<Slot> slots, Instant loadedAt) {
        boolean isExpired(Duration ttl, Clock clock) {
            return clock.instant().isAfter(loadedAt.plus(ttl));
        }
    }

    public SimpleCalendar(CalendarServiceDAO calendarDAO) {
        this(calendarDAO, DEFAULT_TTL, Clock.systemDefaultZone());
    }

    // ✅ Ce constructeur est utilisé par vos tests unitaires
    public SimpleCalendar(CalendarServiceDAO calendarDAO, Duration ttl, Clock clock) {
        this.calendarDAO = calendarDAO;
        this.ttl = ttl;
        this.clock = clock;
    }

    @Override
    public Collection<Slot> getAllSlotsBetween(LocalDate startDate, LocalDate endDate) {
        Period period = new Period(startDate, endDate);
        CacheEntry entry = cache.get(period);

        if (entry != null && !entry.isExpired(ttl, clock)) {
            return entry.slots();
        }

        try {
            Collection<SlotDTO> dtos = calendarDAO.getAllSlotsIn(startDate, endDate);
            List<Slot> slots = dtos.stream().map(slotDTOConverter::fromDTO).toList();
            if (!slots.isEmpty()) {
                cache.put(period, new CacheEntry(slots, clock.instant()));
            }
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