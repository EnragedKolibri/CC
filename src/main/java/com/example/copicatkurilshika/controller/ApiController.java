package com.example.copicatkurilshika.controller;

import com.example.copicatkurilshika.entities.ViberRequest;
import com.example.copicatkurilshika.entities.ViberResponse;
import com.example.copicatkurilshika.entities.ViberStatus;
import com.example.copicatkurilshika.httpServices.AsyncRequestExecutionService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.impl.client.FutureRequestExecutionMetrics;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.example.copicatkurilshika.constants.Common.*;
import static com.example.copicatkurilshika.constants.Common.ANSI_RESET;

@Log4j2
@RestController
@RequestMapping("api/viber")
public class ApiController {

    private Random random = new Random();

    private static AtomicLong request = new AtomicLong();

    @Value("${test-response}")
    private String testText;
    @Value("${custom-response}")
    private String customResponse;

    @Autowired
    private AsyncRequestExecutionService asyncRequestExecutionService;

    @GetMapping(value = "/test", produces = {"application/json"})
    public String hello() {
        return testText;
    }

    @PostMapping(value = "/customResponse", produces = {"application/json"}, consumes = {"application/json"})
    public String custom(@RequestBody String request) {
        return customResponse;
    }

    @PostMapping(value = "vibersrvc/1/send_message")
    public ResponseEntity<ViberResponse> post(@RequestBody ViberRequest viberRequest) throws UnsupportedEncodingException {
        request.incrementAndGet();
        String token = generateToken();
        ViberResponse viberResponse = ViberResponse.builder().status(ViberStatus.SRVC_SUCCESS.getStatus()).messageToken(token).build();
        ResponseEntity<ViberResponse> resp = ResponseEntity.ok(viberResponse);
//        log.info("Request: " + viberRequest + "\t Response: " + viberResponse);
        asyncRequestExecutionService.startFutureRequestExecutionService(token);
        return resp;
    }

    private String generateToken() {
        String token;
        token = String.valueOf(System.nanoTime()) + String.valueOf(random.nextInt(999));
        return token;
    }

    @PostConstruct
    public void counter() {
        ThroughputService t = new ThroughputService();

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(t, 0, 1, TimeUnit.MINUTES);
    }

    @AllArgsConstructor
    static class ThroughputService implements Runnable {



        @Override
        public void run() {
            long res = request.getAndSet(0);
            log.info("controller req per min: " + ANSI_PURPLE + res + ANSI_RESET);
        }

    }

}
