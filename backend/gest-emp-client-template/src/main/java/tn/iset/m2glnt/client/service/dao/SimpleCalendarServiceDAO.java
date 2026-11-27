package tn.iset.m2glnt.client.service.dao;

import tn.iset.m2glnt.client.service.dao.exceptions.UnknownElementException;
import tn.iset.m2glnt.client.service.dao.exceptions.WrongVersionException;
import tn.iset.m2glnt.client.service.dao.json.JsonBodyHandler;
import tn.iset.m2glnt.client.service.dto.CalendarDTO;
import tn.iset.m2glnt.client.service.dto.SlotDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class SimpleCalendarServiceDAO implements CalendarServiceDAO {
    private static final String URL_SERVER = "http://localhost:9090/timeslots";
    private static final String URL_API_SLOTS = "http://localhost:9090/api/slots";
    
    private static final Logger logger = LogManager.getLogger(SimpleCalendarServiceDAO.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private CalendarDTO getCalendarSlotBetween(LocalDate startDate, LocalDate endDate) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL_SERVER + "/" + startDate + "&" + endDate))
                    .GET()
                    .build();
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<CalendarDTO> response = client.send(request, new JsonBodyHandler<>(CalendarDTO.class));
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("Exception thrown", e);
        }
        return null;
    }

    @Override
    public Optional<SlotDTO> get(int key) throws UnknownElementException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL_API_SLOTS + "/" + key))
                    .GET()
                    .build();
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<SlotDTO> response = client.send(request, new JsonBodyHandler<>(SlotDTO.class));
            if (response.statusCode() == 200) return Optional.of(response.body());
            else if (response.statusCode() == 404) throw new UnknownElementException("Introuvable : " + key);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("Exception thrown", e);
        }
        return Optional.empty();
    }

    @Override
    public Collection<SlotDTO> getAllSlotsIn(LocalDate startDate, LocalDate endDate) {
        CalendarDTO slotDTO = getCalendarSlotBetween(startDate, endDate);
        if (slotDTO == null) return List.of();
        return slotDTO.calendarSlots();
    }

    @Override
    public int create(SlotDTO element) {
        logger.info("Creating calendar slot {}", element);
        try {
            String jsonBody = objectMapper.writeValueAsString(element);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL_API_SLOTS))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                // ✅ LECTURE DU VRAI ID DANS LE JSON (C'était le bug ici)
                SlotDTO created = objectMapper.readValue(response.body(), SlotDTO.class);
                return created.id(); 
            }
        } catch (Exception e) {
            logger.error("Exception thrown", e);
        }
        return -1;
    }

    @Override
    public int update(SlotDTO element) throws UnknownElementException, WrongVersionException {
        logger.info("Updating calendar slot {}", element);
        try {
            String jsonBody = objectMapper.writeValueAsString(element);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL_API_SLOTS + "/" + element.id()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return element.version() + 1; 
            } else if (response.statusCode() == 404) {
                throw new UnknownElementException("Créneau introuvable pour la mise à jour");
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("Exception thrown", e);
        }
        return -1;
    }

    @Override
    public void delete(SlotDTO element) throws UnknownElementException, WrongVersionException {
        logger.info("Deleting calendar slot {}", element);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL_API_SLOTS + "/" + element.id()))
                    .DELETE()
                    .build();
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) throw new UnknownElementException("Créneau introuvable pour la suppression");
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("Exception thrown", e);
        }
    }
}