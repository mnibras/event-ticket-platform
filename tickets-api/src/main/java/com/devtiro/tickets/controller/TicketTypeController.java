package com.devtiro.tickets.controller;

import com.devtiro.tickets.service.TicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.devtiro.tickets.util.JwtUtil.getUserIdFromJwt;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/events/{eventId}/ticket-types")
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    @PostMapping(path = "/{ticketTypeId}/tickets")
    public ResponseEntity<Void> purchaseTicket(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID ticketTypeId) {
        ticketTypeService.purchaseTicket(getUserIdFromJwt(jwt), ticketTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
