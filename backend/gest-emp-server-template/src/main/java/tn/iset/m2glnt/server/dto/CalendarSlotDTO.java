package tn.iset.m2glnt.server.dto;

public record CalendarSlotDTO(int id, String description, TimeInterval timeInterval, int version) {
}