package com.devtiro.tickets.service.impl;

import com.devtiro.tickets.domain.entity.Ticket;
import com.devtiro.tickets.domain.entity.TicketStatusEnum;
import com.devtiro.tickets.domain.entity.TicketType;
import com.devtiro.tickets.domain.entity.User;
import com.devtiro.tickets.exception.TicketTypeNotFoundException;
import com.devtiro.tickets.exception.TicketsSoldOutException;
import com.devtiro.tickets.exception.UserNotFoundException;
import com.devtiro.tickets.repository.TicketRepository;
import com.devtiro.tickets.repository.TicketTypeRepository;
import com.devtiro.tickets.repository.UserRepository;
import com.devtiro.tickets.service.QrCodeService;
import com.devtiro.tickets.service.TicketTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;

    @Override
    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID ticketTypeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User with ID %s was not found", userId)
        ));

        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticketTypeId)
                .orElseThrow(() -> new TicketTypeNotFoundException(
                        String.format("Ticket type with ID %s was not found", ticketTypeId)
                ));

        int purchasedTickets = ticketRepository.countByTicketTypeId(ticketType.getId());
        Integer totalAvailable = ticketType.getTotalAvailable();

        if (purchasedTickets + 1 > totalAvailable) {
            throw new TicketsSoldOutException();
        }

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setTicketType(ticketType);
        ticket.setPurchaser(user);

        Ticket savedTicket = ticketRepository.save(ticket);
        qrCodeService.generateQrCode(savedTicket);

        return ticketRepository.save(savedTicket);
    }
}
