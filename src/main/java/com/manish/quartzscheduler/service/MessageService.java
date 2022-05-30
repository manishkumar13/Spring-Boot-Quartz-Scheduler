package com.manish.quartzscheduler.service;

import org.springframework.stereotype.Service;

@Service
public class MessageService {

    public void printMessage(String message) {
        System.out.println("Scheduled message is " + message);
    }
}
