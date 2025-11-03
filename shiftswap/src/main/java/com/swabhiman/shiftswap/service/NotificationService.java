package com.swabhiman.shiftswap.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void notify(String message) {
        // Placeholder for emails/push/etc.
        log.info("NOTIFICATION: {}", message);
    }
}


