package com.example.copicatkurilshika.httpServices;
import com.example.copicatkurilshika.viberEntitys.ViberResponse;
import com.example.copicatkurilshika.viberEntitys.ViberStatus;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

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

        StringEntity deliveredEntity = new StringEntity("{\"message_token\":\""+token+"\",\"status\":0}");
        StringEntity seenEntity = new StringEntity("{\"message_token\":\""+token+"\",\"status\":1}");

        HttpPost deliveredRequest = new HttpPost(drURL);
        deliveredRequest.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        deliveredRequest.setEntity(deliveredEntity);

        HttpPost seenRequest = new HttpPost(drURL);
        seenRequest.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        seenRequest.setEntity(seenEntity);

        delivered.execute(deliveredRequest, null, r->r);
        seen.execute(seenRequest, null, r->r);

    }


}
