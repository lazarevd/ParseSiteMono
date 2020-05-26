package ru.laz.sender;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.laz.common.models.NewsBlockDTO;
import ru.laz.common.models.NewsBlockEntity;
import ru.laz.db.repository.NewsBlockRepo;

import java.util.ArrayList;
import java.util.List;


public class SenderBase {

    @Autowired
    protected NewsBlockRepo newsBlockRepo;
    @Autowired
    ModelMapper modelMapper;

    private final static Logger log = LoggerFactory.getLogger(SenderBase.class);

    @Transactional(isolation= Isolation.SERIALIZABLE)
    public void setProcessing(int id, int status) {
        NewsBlockEntity newsBlockEntity = newsBlockRepo.findById(id).get();
        newsBlockEntity.setProcessing(status);
        newsBlockRepo.save(newsBlockEntity);
    }

    @Transactional(isolation= Isolation.SERIALIZABLE)
    public void setSent(int id) {
        NewsBlockEntity newsBlockEntity = newsBlockRepo.findById(id).get();
        newsBlockEntity.setProcessing(0);
        newsBlockEntity.setSent(1);
        newsBlockRepo.save(newsBlockEntity);
    }

    @Transactional(isolation= Isolation.SERIALIZABLE)
    protected List<NewsBlockDTO> findUnsent() {
        return convertToDtos(newsBlockRepo.findBySentAndProcessing(0, 0));
    }

    private List<NewsBlockDTO> convertToDtos(List<NewsBlockEntity> input) {
        List<NewsBlockDTO> returnList = new ArrayList<>();
        input.forEach(nb -> {
            log.info("Fetched unsent: " + nb.getId());
            returnList.add(modelMapper.map(nb, NewsBlockDTO.class));
        });
        return returnList;
    }

}
