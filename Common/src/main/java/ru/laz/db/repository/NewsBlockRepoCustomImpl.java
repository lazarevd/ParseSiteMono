package ru.laz.db.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.laz.common.models.NewsBlockEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class NewsBlockRepoCustomImpl implements NewsBlockRepoCustom {

    private static final Logger logger = LoggerFactory.getLogger(NewsBlockRepoCustomImpl.class);
    @PersistenceContext
    EntityManager em;
    @Override
    @Transactional(isolation= Isolation.SERIALIZABLE)
    public void insertF(NewsBlockEntity nb) {
        NewsBlockEntity nbl = em.find(NewsBlockEntity.class, nb.getId());
        //em.detach(nbl);
        if (null == nbl) {
            logger.debug("Not Exist! " + nb.getId());
            em.persist(nb);
        } else {
            logger.debug("Exist " + nb.getId());
        }

    }
}
