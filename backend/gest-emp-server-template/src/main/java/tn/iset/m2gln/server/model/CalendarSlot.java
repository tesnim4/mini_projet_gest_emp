package tn.iset.m2gln.server.model;

import java.time.LocalDateTime;

/**
 * A calendar item
 */
public class CalendarSlot {

    /**
     * The identifier
     */
    private int id;

    /**
     * The beginning time
     */
    private LocalDateTime time_begin;

    /**
     * The ending time
     */
    private LocalDateTime time_end;

    /**
     * The description of the element
     */
    private String description;

    /**
     * The constructor
     * @param id the identifier
     * @param time_begin the beginning time
     * @param time_end the ending time
     * @param description the description
     */
    public CalendarSlot(int id, LocalDateTime time_begin, LocalDateTime time_end, String description) {
        this.id = id;
        this.time_begin = time_begin;
        this.time_end = time_end;
        this.description = description;
    }

    /**
     * The constructor
     * @param time_begin the beginning time
     * @param time_end the ending time
     * @param description the description
     */
    public CalendarSlot(LocalDateTime time_begin, LocalDateTime time_end, String description) {
        this(-1, time_begin, time_end, description);
    }

    /**
     * Getter for the identifier
     * @return the identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the beginning time
     * @return the time
     */
    public LocalDateTime getTime_begin() {
        return time_begin;
    }

    /**
     * Getter for the ending time
     * @return the time
     */
    public LocalDateTime getTime_end() {
        return time_end;
    }

    /**
     * Getter for the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the identifier
     * @param id the identifier
     */
    public void setId(int id) {
        this.id = id;
    }
}
