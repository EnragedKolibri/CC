package com.example.copicatkurilshika.httpServices;

import com.example.copicatkurilshika.entities.HttpAnswer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.FutureRequestExecutionMetrics;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.apache.http.impl.client.HttpRequestFutureTask;
import org.apache.logging.log4j.core.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.example.copicatkurilshika.constants.Common.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@Service
public class AsyncRequestExecutionService {

    @Value("${drURL}")
    private String drURL;

    private static AtomicLong request = new AtomicLong();
    private static AtomicLong delivSend = new AtomicLong();

    @Autowired
    @Qualifier("deliveredFutureRequestExecutionService")
    private FutureRequestExecutionService delivered;

    @Autowired
    @Qualifier("seenFutureRequestExecutionService")
    private FutureRequestExecutionService seen;

    @Async("viberStatusSenderTaskExecutor")
    public void startFutureRequestExecutionService(String token) throws UnsupportedEncodingException {
        request.incrementAndGet();
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

        long startTime = System.currentTimeMillis();
        delivered.execute(deliveredRequest, null, r -> handleHttpResponse(r, startTime), new Callback(token));
//        log.info(ANSI_PURPLE + token + ANSI_RESET + " delivered will sent: " + ANSI_YELLOW + del + ANSI_RESET);
        HttpRequestFutureTask<HttpAnswer> execute = seen.execute(seenRequest, null, r -> handleHttpResponse(r, startTime));
//        log.info(ANSI_PURPLE + token + ANSI_RESET + " seen will sent: " + ANSI_CYAN + see + ANSI_RESET);
    }


    public static HttpAnswer handleHttpResponse(HttpResponse response, long startTime) {
        long finish = System.currentTimeMillis();
        HttpAnswer result = new HttpAnswer();
        result.setCode(response.getStatusLine().getStatusCode());
        result.setDuration(finish - startTime);
        try (Reader reader = new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8)) {
            result.setText(IOUtils.toString(reader));
        } catch (IOException e) {
            result.setText(e.getMessage());
        }
        return result;
    }

    @AllArgsConstructor
    class Callback implements FutureCallback<HttpAnswer> {

        private final String token;

        @Override
        public void completed(HttpAnswer answer) {
            delivSend.incrementAndGet();
            log.info(ANSI_PURPLE + token + ANSI_RESET + " delivered. Dur " + ANSI_YELLOW + answer.durationInSec() + ANSI_RESET);
        }

        @Override
        public void failed(Exception e) {
            log.error("Send dr failed " + e.getMessage());
        }

        @Override
        public void cancelled() {
        }
    }


    @PostConstruct
    public void counter() {
        ThroughputService t = new ThroughputService(delivered);

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(t, 0, 1, TimeUnit.MINUTES);
    }

    @AllArgsConstructor
    static class ThroughputService implements Runnable {

        private FutureRequestExecutionService delivered;

        @Override
        public void run() {
            long res = request.getAndSet(0);
            long deliv = delivSend.getAndSet(0);
            log.info("req per min: " + ANSI_PURPLE + res + ANSI_RESET + " deliv per min: " + ANSI_CYAN + deliv + ANSI_RESET);
            FutureRequestExecutionMetrics metrics = delivered.metrics();
            log.info("delivery metrics: " + ANSI_YELLOW + metrics + ANSI_RESET);
        }

    }
}
