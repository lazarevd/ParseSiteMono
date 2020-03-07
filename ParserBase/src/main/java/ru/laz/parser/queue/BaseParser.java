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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import ru.laz.common.models.NewsBlockDTO;
import ru.laz.common.models.NewsBlockEntity;
import ru.laz.common.models.NewsBlockSendStatusDTO;
import ru.laz.db.repository.NewsBlockRepo;
import ru.laz.sender.SenderListener;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseParser {
    protected Logger log = LoggerFactory.getLogger(getClass());

    AsyncHttpClient client;

    protected List<SenderListener> senders = new ArrayList<>();

    protected String newsUrl = "";

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;


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

    public void sendToSenders(NewsBlockDTO newsBlockDTO) {
        for (SenderListener se : senders) {
            se.convertAndSend(newsBlockDTO);
        }
    }

    protected List<NewsBlockDTO> convertToDtos(List<NewsBlockEntity> input) {
        List<NewsBlockDTO> returnList = new ArrayList<>();
        input.forEach(nb -> returnList.add(modelMapper.map(nb, NewsBlockDTO.class)));
        return returnList;
    }


    //schedule it
    public void getContentFromHttp() {
        BoundRequestBuilder request = client.prepareGet(newsUrl);
        request.execute(new AsyncHandler<Object>() {
            int status;
            StringBuilder sb = new StringBuilder();
            @Override
            public State onStatusReceived(HttpResponseStatus responseStatus)
                    throws Exception {
                log.debug(responseStatus.toString());
                status = responseStatus.getStatusCode();
                if (status == 200) {
                    return State.CONTINUE;
                } else {
                    return State.ABORT;
                }
            }

            @Override
            public State onHeadersReceived(HttpHeaders headers)
                    throws Exception {
                return State.CONTINUE;
            }

            @Override
            public State onBodyPartReceived(HttpResponseBodyPart bodyPart)
                    throws Exception {
                sb.append(new String( bodyPart.getBodyPartBytes(), StandardCharsets.UTF_8));
                return State.CONTINUE;
            }

            @Override
            public void onThrowable(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public Object onCompleted() {
                if (status == 200) {
                    List<NewsBlockEntity> news = parseHtml(sb.toString());
                    log.debug("Fetched: " + news.size());
                    for (NewsBlockEntity nb : news) {
                        //newsBlockRepo.insertF(nb);
                    }
                }
                return null;
            }
        });
    }

    public abstract List<NewsBlockEntity> parseHtml(String html);


}
