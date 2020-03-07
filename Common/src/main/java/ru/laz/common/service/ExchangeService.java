package ru.laz.common.service;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Service
//TODO сделать наблюдателя и подписчиков
public class ExchangeService {
    private static BlockingQueue blockingQueue = new LinkedBlockingDeque();
    private List<Consumer> consumers = new LinkedList<>();

    public <T> void addNotification(T notif) {
        blockingQueue.add(notif);
    }
}
