package ru.laz.parser.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.laz.common.models.NewsBlockEntity;
import ru.laz.db.repository.NewsBlockRepo;
import ru.laz.parser.queue.BaseParser;
import ru.laz.sender.Sender;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Service
public class MosfarrParser extends BaseParser {


    @Autowired
    NewsBlockRepo newsBlockRepo;

    @Autowired
    Sender telegramSender;

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
        getContentFromHttpAndSend();
    }
}
