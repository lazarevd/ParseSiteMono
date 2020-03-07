package ru.laz.parser.grabber;

import io.netty.handler.codec.http.HttpHeaders;
import org.asynchttpclient.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.laz.common.models.NewsBlockDTO;
import ru.laz.common.models.NewsBlockEntity;
import ru.laz.db.repository.NewsBlockRepo;
import ru.laz.parser.queue.BaseParser;
import ru.laz.sender.SenderListener;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Service
public class MosfarrParser extends BaseParser {


    @Autowired
    NewsBlockRepo newsBlockRepo;

    @Autowired
    SenderListener telegramSender;

    @PostConstruct
    public void initParser() {
        init();
        senders.add(telegramSender);
        newsUrl = "http://mosfarr.ru/category/новости/";
    }


    public List<NewsBlockEntity> parseHtml(String html) {
        Document doc = Jsoup.parseBodyFragment(html);
        Element body = doc.body();
        Elements elems = body.getElementsByTag("main")
                .first().getElementsByClass("item-block item-news");
        List<NewsBlockEntity> retList = new ArrayList<>();

        log.debug("elems " + elems.size());
        for (Element el : elems) {
            Element nElementHref = el.getElementsByTag("a").first();
            String date = el.getElementsByClass("date").text();
            String url = nElementHref.attr("href");
            String title = nElementHref.getElementsByTag("h2").first().text();
            NewsBlockEntity nb = new NewsBlockEntity();
            nb.setTitle(title);
            nb.setUrl(url);
            nb.setDate(date);
            retList.add(nb);
        }
        return retList;
    }


    @Scheduled(fixedDelayString = "${news.block.refresh}")
    public void getPageContent() {
        getContentFromHttp();
    }
}
