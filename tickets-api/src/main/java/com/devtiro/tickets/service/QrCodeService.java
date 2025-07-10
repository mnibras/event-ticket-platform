package com.devtiro.tickets.service;

import com.devtiro.tickets.domain.entity.QrCode;
import com.devtiro.tickets.domain.entity.Ticket;

import java.util.UUID;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);

    byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId);
}
