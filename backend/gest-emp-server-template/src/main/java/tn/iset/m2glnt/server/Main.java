package tn.iset.m2glnt.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import tn.iset.m2glnt.server.controler.CalendarController;
import tn.iset.m2glnt.server.dto.CalendarDTO;
import tn.iset.m2glnt.server.dto.TimeInterval;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {
        int port = resolvePort();
        CalendarController calendarController = new CalendarController();

        // ✅ Configuration Jackson pour supporter LocalDateTime
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Javalin.create(javalinConfig -> {
            javalinConfig.bundledPlugins.enableDevLogging();
            javalinConfig.jsonMapper(new JavalinJackson(mapper, false)); 
        })
                // Route existante
                .get("/timeslots/{dateInterval}",
                        ctx -> {
                            String[] parts = ctx.pathParam("dateInterval").split("&");
                            LocalDate startDate = LocalDate.parse(parts[0]);
                            LocalDate endDate = LocalDate.parse(parts[1]);
                            TimeInterval timeInterval = new TimeInterval(
                                    LocalDateTime.of(startDate, LocalTime.MIN),
                                    LocalDateTime.of(endDate, LocalTime.MAX));
                            CalendarDTO calendarSlotsIn = calendarController.getCalendarSlotsIn(timeInterval);
                            System.out.println(calendarSlotsIn);
                            ctx.json(calendarSlotsIn);
                        })

                .get("/api/slots/{id}", calendarController::getSlotById)

                .post("/api/slots", calendarController::createSlot)
                
                .delete("/api/slots/{id}", calendarController::deleteSlot)
                
                .put("/api/slots/{id}", calendarController::updateSlot)
                .start(port);
    }       

    private static int resolvePort() {
        String env = System.getenv("PORT");
        if (env != null && !env.isBlank()) {
            try { return Integer.parseInt(env); } catch (NumberFormatException ignored) {}
        }
        return Integer.getInteger("server.port", 9090);
    }
}