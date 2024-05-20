package luan.passin.services;

import lombok.RequiredArgsConstructor;
import luan.passin.domain.attendee.Attendee;
import luan.passin.domain.event.Event;
import luan.passin.domain.event.exceptions.EventNotFoundException;
import luan.passin.dto.event.EventIdDTO;
import luan.passin.dto.event.EventRequestDTO;
import luan.passin.dto.event.EventResponseDTO;
import luan.passin.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor // Constroi constructors quando for preciso
public class EventService {
    private final EventRepository eventRepository;
    private final AttendeeService attendeeService;

    public EventResponseDTO getEventDetail(String eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event not found with ID:" + eventId));
        List<Attendee> attendeeList = this.attendeeService.getAllAttendeesFromEvent(eventId);
        return new EventResponseDTO(event, attendeeList.size());
    }

    public EventIdDTO createEvent(EventRequestDTO eventDTO){
        Event newEvent = new Event();
        newEvent.setTitle(eventDTO.title());
        newEvent.setDetails(eventDTO.details());
        newEvent.setMaximumAttendees(eventDTO.maximumAttendees());
        newEvent.setSlug(this.createSlug(eventDTO.title()));

        this.eventRepository.save(newEvent);

        return new EventIdDTO(newEvent.getId());
    }

    public String createSlug(String text){
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{InCOMBINING_DIACRITICAL_MARKS}]","")
                .replaceAll("[^\\w\\s)]", "")
                .replaceAll("[\\s+]", "")
                .toLowerCase();
    }
}
