package ru.laz.senderbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.laz.common.models.NewsBlockEntity;
import ru.laz.db.repository.NewsBlockRepo;

import java.util.Optional;

public class BaseSender {

    //@Autowired
    //NewsBlockRepo newsBlockRepo;

    Logger log = LoggerFactory.getLogger(BaseSender.class);

    @Transactional
    protected void processSent(int id) {
        /*
        log.info("Status received " + id);
        Optional<NewsBlockEntity> nbOptional = newsBlockRepo.findById(id);
        nbOptional.ifPresent(nb -> {
            nb.setSent(1);
            nb.setProcessing(0);
            newsBlockRepo.save(nb);
        });
        */

    }
}
