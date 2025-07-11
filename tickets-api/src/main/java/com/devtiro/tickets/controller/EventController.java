package com.devtiro.tickets.controller;

import com.devtiro.tickets.domain.dto.request.*;
import com.devtiro.tickets.domain.dto.response.GetEventDetailsResponseDto;
import com.devtiro.tickets.domain.dto.response.ListEventResponseDto;
import com.devtiro.tickets.domain.dto.response.UpdateEventResponseDto;
import com.devtiro.tickets.domain.entity.Event;
import com.devtiro.tickets.mapper.EventMapper;
import com.devtiro.tickets.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.devtiro.tickets.util.JwtUtil.getUserIdFromJwt;

@RestController
@RequestMapping(path = "/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventMapper eventMapper;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<CreateEventResponseDto> createEvent(@AuthenticationPrincipal Jwt jwt,
                                                              @Valid @RequestBody CreateEventRequestDto createEventRequestDto) {
        CreateEventRequest createEventRequest = eventMapper.fromDto(createEventRequestDto);
        UUID userId = getUserIdFromJwt(jwt);
        Event createdEvent = eventService.createEvent(userId, createEventRequest);
        CreateEventResponseDto createEventResponseDto = eventMapper.toDto(createdEvent);
        return new ResponseEntity<>(createEventResponseDto, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{eventId}")
    public ResponseEntity<UpdateEventResponseDto> updateEvent(@AuthenticationPrincipal Jwt jwt,
                                                              @PathVariable UUID eventId,
                                                              @Valid @RequestBody UpdateEventRequestDto updateEventRequestDto) {
        UpdateEventRequest updateEventRequest = eventMapper.fromDto(updateEventRequestDto);
        UUID userId = getUserIdFromJwt(jwt);

        Event updatedEvent = eventService.updateEventForOrganizer(userId, eventId, updateEventRequest);

        UpdateEventResponseDto updateEventResponseDto = eventMapper.toUpdateEventResponseDto(
                updatedEvent);

        return ResponseEntity.ok(updateEventResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<ListEventResponseDto>> listEvents(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        UUID userId = getUserIdFromJwt(jwt);
        Page<Event> events = eventService.listEventsForOrganizer(userId, pageable);
        return ResponseEntity.ok(events.map(eventMapper::toListEventResponseDto));
    }

    @GetMapping(path = "/{eventId}")
    public ResponseEntity<GetEventDetailsResponseDto> getEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID eventId
    ) {
        UUID userId = getUserIdFromJwt(jwt);
        return eventService.getEventForOrganizer(userId, eventId)
                .map(eventMapper::toGetEventDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/{eventId}")
    public ResponseEntity<Void> deleteEvent(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID eventId) {
        UUID userId = getUserIdFromJwt(jwt);
        eventService.deleteEventForOrganizer(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}
