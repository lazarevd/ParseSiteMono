package ru.laz.parser.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.asynchttpclient.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.laz.common.models.NewsBlockDTO;
import ru.laz.common.models.NewsBlockEntity;
import ru.laz.db.repository.NewsBlockRepo;
import ru.laz.sender.Sender;

import javax.net.ssl.SSLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseParser {
    protected Logger log = LoggerFactory.getLogger(getClass());

    AsyncHttpClient client;

    protected List<Sender> senders = new ArrayList<>();

    protected String newsUrl = "";

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NewsBlockRepo newsBlockRepo;


    public void init() {
        SslContext sslContext = null;

        {
            try {
                sslContext = SslContextBuilder
                        .forClient()
                        .sslProvider(SslProvider.JDK)
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build();
            } catch (SSLException e) {
                e.printStackTrace();
            }
        }

        DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config()
                .setConnectTimeout(3500)
                .setSslContext(sslContext);//ignore wrong certs
        client = Dsl.asyncHttpClient(clientBuilder);
    }

    public void sendToSenders() {
        for (Sender se : senders) {
            log.info("Start send by " + se.getName());
            se.send();
        }
    }




    public void getContentFromHttpAndSend() {
        getContentFromHttp();
    }

    //schedule it in extended class
    public void getContentFromHttp() {
        BoundRequestBuilder request = client.prepareGet(newsUrl);
        request.execute(new AsyncHandler<Object>() {
            int status;
            StringBuilder sb = new StringBuilder();
            @Override
            public State onStatusReceived(HttpResponseStatus responseStatus) {
                log.debug(responseStatus.toString());
                status = responseStatus.getStatusCode();
                if (status == 200) {
                    return State.CONTINUE;
                } else {
                    return State.ABORT;
                }
            }

            @Override
            public State onHeadersReceived(HttpHeaders headers) {
                return State.CONTINUE;
            }

            @Override
            public State onBodyPartReceived(HttpResponseBodyPart bodyPart) {
                sb.append(new String( bodyPart.getBodyPartBytes(), StandardCharsets.UTF_8));
                return State.CONTINUE;
            }

            @Override
            public void onThrowable(Throwable t) {
                log.error(t.getMessage());
                sendToSenders();//send others unsent
            }

            @Override
            public Object onCompleted() {
                if (status == 200) {
                    List<NewsBlockEntity> news = parseHtml(sb.toString());
                    log.debug("Fetched: " + news.size());
                    for (NewsBlockEntity nb : news) {
                        newsBlockRepo.insertF(nb);
                    }
                    sendToSenders();
                }
                return null;
            }
        });
    }

    public abstract List<NewsBlockEntity> parseHtml(String html);


}
