package ru.laz.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaders;
import org.asynchttpclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.laz.common.models.NewsBlockDTO;
import ru.laz.sender.Sender;
import ru.laz.sender.SenderBase;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
public class TelegramSender extends SenderBase implements Sender {


    private static final String NAME = "TelegramSender";
    @Value("${telegram.bot.protocol}")
    private String botProtocol;
    @Value("${telegram.bot.url}")
    private String botUrl;
    @Value("${telegram.bot.token}")
    private String botToken;
    private final String SEND_METHOD = "/sendMessage";
    @Value("${async.http.connect.timeout}")
    private int httpConnectionTimeout;

    @Value("${telegram.bot.chat_id}")
    int botChatId;


    @Autowired
    private ObjectMapper objectMapper;

    private final static Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config().setConnectTimeout(httpConnectionTimeout);
    private AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder);

    @PostConstruct
    public void init() {
        log.info(new File("").getAbsolutePath());
        log.info(botProtocol + "://" + botUrl + botToken);
    }



    private  String castDtoToTelegramJson(NewsBlockDTO newsBlockDTO) {
        int id = newsBlockDTO.getId();
        TelegramDTO telegramDTO = new TelegramDTO(botChatId, newsBlockDTO.getUrl() + " " + newsBlockDTO.getTitle());
        String jsonTelegramDTO = "";
        try {
            jsonTelegramDTO = objectMapper.writeValueAsString(telegramDTO);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert to JSON ", e);
        }
        return jsonTelegramDTO;
    }


    private void sendToChannel(NewsBlockDTO newsBlockDTO) {
        log.info("Mark in DB: " + newsBlockDTO.getId());
        setProcessing(newsBlockDTO.getId(), 1);
        String fullUrl = botProtocol + "://" + botUrl + botToken + SEND_METHOD;
        String jsonTelegramDTO = castDtoToTelegramJson(newsBlockDTO);
        BoundRequestBuilder request = client.preparePost(fullUrl)
                .setBody(jsonTelegramDTO)
                .setHeader("Content-Type", "application/json");
        log.info("Start send: " + newsBlockDTO.getId());
        request.execute(new AsyncHandler<Object>() {
            @Override
            public State onStatusReceived(HttpResponseStatus response) {
                if (response.getStatusCode() == 200) {
                    log.info("Sent :" + jsonTelegramDTO);
                    setSent(newsBlockDTO.getId());
                } else {
                    setProcessing(newsBlockDTO.getId(), 0);
                    log.error("Failed to send :" + ", "
                            + jsonTelegramDTO + ", "
                            + fullUrl + ", "
                            + response.getStatusCode() + ", "
                            + response.getStatusText());
                }
                return null;
            }

            @Override
            public State onHeadersReceived(HttpHeaders httpHeaders) {
                return null;
            }

            @Override
            public State onBodyPartReceived(HttpResponseBodyPart httpResponseBodyPart) {
                return null;
            }

            @Override
            public void onThrowable(Throwable throwable) {
                setProcessing(newsBlockDTO.getId(), 0);
                log.error("Failed to send " + jsonTelegramDTO + " " + throwable.getMessage());
            }

            @Override
            public Object onCompleted() {
                return null;
            }
        });
    }



    @Override
    public void send() {
            findUnsent().forEach(nb -> sendToChannel(nb));
    }

    public String getName() {return NAME;}

}


