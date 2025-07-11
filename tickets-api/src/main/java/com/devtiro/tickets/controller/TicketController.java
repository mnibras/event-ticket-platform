package com.devtiro.tickets.controller;

import com.devtiro.tickets.domain.dto.response.GetTicketResponseDto;
import com.devtiro.tickets.domain.dto.response.ListTicketResponseDto;
import com.devtiro.tickets.mapper.TicketMapper;
import com.devtiro.tickets.service.QrCodeService;
import com.devtiro.tickets.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.devtiro.tickets.util.JwtUtil.getUserIdFromJwt;

@RestController
@RequestMapping(path = "/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final QrCodeService qrCodeService;

    @GetMapping
    public Page<ListTicketResponseDto> listTickets(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        return ticketService
                .listTicketsForUser(getUserIdFromJwt(jwt), pageable)
                .map(ticketMapper::toListTicketResponseDto);
    }

    @GetMapping(path = "/{ticketId}")
    public ResponseEntity<GetTicketResponseDto> getTicket(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID ticketId) {
        return ticketService
                .getTicketForUser(getUserIdFromJwt(jwt), ticketId)
                .map(ticketMapper::toGetTicketResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/{ticketId}/qr-codes")
    public ResponseEntity<byte[]> getTicketQrCode(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID ticketId) {
        byte[] qrCodeImage = qrCodeService.getQrCodeImageForUserAndTicket(getUserIdFromJwt(jwt), ticketId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrCodeImage.length);

        return ResponseEntity.ok().headers(headers).body(qrCodeImage);
    }

}
