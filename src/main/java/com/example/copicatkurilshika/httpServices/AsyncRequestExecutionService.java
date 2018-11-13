package com.example.copicatkurilshika.httpServices;

import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

import static com.example.copicatkurilshika.constants.Common.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@Service
public class AsyncRequestExecutionService {

    @Value("${drURL}")
    private String drURL;

    @Autowired
    @Qualifier("deliveredFutureRequestExecutionService")
    private FutureRequestExecutionService delivered;

    @Autowired
    @Qualifier("seenFutureRequestExecutionService")
    private FutureRequestExecutionService seen;

    @Async("viberStatusSenderTaskExecutor")
    public void startFutureRequestExecutionService(String token) throws UnsupportedEncodingException {
        String del = "{" +
                "\"message_token\":\"" + token + "\"," +
                "\"message_status\":0," +
                "\"message_time\":" + System.currentTimeMillis() + "," +
                "\"phone_number\": \"380504490154\"," +
                "\"service_id\": 3100" +
                "}";
        StringEntity deliveredEntity = new StringEntity(del);

        String see = "{" +
                "\"message_token\":\"" + token + "\"," +
                "\"message_status\":1," +
                "\"message_time\":" + System.currentTimeMillis() + "," +
                "\"phone_number\": \"380504490154\"," +
                "\"service_id\": 3100" +
                "}";
        StringEntity seenEntity = new StringEntity(see);

        HttpPost deliveredRequest = new HttpPost(drURL);
        deliveredRequest.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        deliveredRequest.setEntity(deliveredEntity);

        HttpPost seenRequest = new HttpPost(drURL);
        seenRequest.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        seenRequest.setEntity(seenEntity);

        delivered.execute(deliveredRequest, null, r -> r);
        log.info(ANSI_PURPLE + token + ANSI_RESET + " delivered will sent: " + ANSI_GREEN + del + ANSI_RESET);
        seen.execute(seenRequest, null, r -> r);
        log.info(ANSI_PURPLE + token + ANSI_RESET + " seen will sent: " + ANSI_GREEN + see + ANSI_RESET);
    }

}
