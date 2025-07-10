package com.devtiro.tickets.controller;

import com.devtiro.tickets.domain.dto.request.TicketValidationRequestDto;
import com.devtiro.tickets.domain.dto.response.TicketValidationResponseDto;
import com.devtiro.tickets.domain.entity.TicketValidation;
import com.devtiro.tickets.domain.entity.TicketValidationMethod;
import com.devtiro.tickets.mapper.TicketValidationMapper;
import com.devtiro.tickets.service.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/ticket-validations")
@RequiredArgsConstructor
public class TicketValidationController {

    private final TicketValidationService ticketValidationService;
    private final TicketValidationMapper ticketValidationMapper;

    @PostMapping
    public ResponseEntity<TicketValidationResponseDto> validateTicket(@RequestBody TicketValidationRequestDto ticketValidationRequestDto) {
        TicketValidationMethod method = ticketValidationRequestDto.getMethod();
        TicketValidation ticketValidation;
        if (TicketValidationMethod.MANUAL.equals(method)) {
            ticketValidation = ticketValidationService.validateTicketManually(ticketValidationRequestDto.getId());
        } else {
            ticketValidation = ticketValidationService.validateTicketByQrCode(ticketValidationRequestDto.getId());
        }
        return ResponseEntity.ok(
                ticketValidationMapper.toTicketValidationResponseDto(ticketValidation)
        );
    }

}
