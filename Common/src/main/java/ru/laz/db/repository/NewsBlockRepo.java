package ru.laz.db.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.laz.common.models.NewsBlockEntity;

import java.util.List;

@Repository
public interface NewsBlockRepo extends CrudRepository<NewsBlockEntity,Integer>, NewsBlockRepoCustom {

    List<NewsBlockEntity> findBySentAndProcessing(int sent, int processing);

    List<NewsBlockEntity> findBySent(int sent);

    List<NewsBlockEntity> findByProcessing(int proc);

    List<NewsBlockEntity> findByIdIn(List<Integer> ids);

    Iterable<NewsBlockEntity> findAll();
}
