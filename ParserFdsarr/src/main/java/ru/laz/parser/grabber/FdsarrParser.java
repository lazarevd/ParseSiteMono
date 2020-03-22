package ru.laz.parser.grabber;

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
import ru.laz.common.models.NewsBlockEntity;
import ru.laz.db.repository.NewsBlockRepo;
import ru.laz.parser.queue.BaseParser;
import ru.laz.sender.Sender;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Service
public class FdsarrParser extends BaseParser {

    //@Autowired
    //private NewsBlockRepo newsBlockRepo;

    private final static Logger logger = LoggerFactory.getLogger(FdsarrParser.class);

    AsyncHttpClient client;

    private final String BASE_URL ="https://fdsarr.ru";

    @Autowired
    NewsBlockRepo newsBlockRepo;

    @Autowired
    Sender telegramSender;

    @PostConstruct
    public void initBean() {
        newsUrl = BASE_URL+"/arr/news/";
        senders.add(telegramSender);
        init();
    }


    public List<NewsBlockEntity> parseHtml(String html) {
        Document doc = Jsoup.parseBodyFragment(html);
        Element body = doc.body();
        Elements elems = body.getElementsByTag("section").first()
                .getElementById("newswrap")
                .getElementsByClass("content-wrap").first()
                .getElementsByClass("news-list div-with-shadow").first()
                .getElementsByTag("ul").first()
                .getElementsByTag("li");

        Element elemTop = body.getElementsByTag("section").first()
                .getElementById("newswrap")
                .getElementsByClass("content-wrap").first()
                .getElementsByClass("news-list div-with-shadow").first()
                .getElementsByClass("top-news").first();

        String urlTop = BASE_URL + elemTop.getElementsByTag("a").first().attr("href");
        String dateTop = elemTop.getElementsByClass("arr-news").first().text();
        String titleTop = elemTop.getElementsByTag("div").first().getElementsByTag("h3").first().text();
        NewsBlockEntity nbt = new NewsBlockEntity();
        nbt.setTitle(titleTop);
        nbt.setUrl(urlTop);
        nbt.setDate(dateTop);
        List<NewsBlockEntity> retList = new ArrayList<>();
        retList.add(nbt);
        logger.debug("elems " + elems.size());
        for (Element el : elems) {
            Element li = el.getElementsByTag("li").first();
            String url = BASE_URL + li.getElementsByTag("a").first().attr("href");
            String date = li.getElementsByClass("arr-news").first().text();
            String title = li.getElementsByTag("h3").first().text();

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
