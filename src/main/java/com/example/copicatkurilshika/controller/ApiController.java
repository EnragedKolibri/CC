package com.example.copicatkurilshika.controller;

import com.example.copicatkurilshika.entities.ViberRequest;
import com.example.copicatkurilshika.entities.ViberResponse;
import com.example.copicatkurilshika.entities.ViberStatus;
import com.example.copicatkurilshika.httpServices.AsyncRequestExecutionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Log4j2
@RestController
@RequestMapping("api/viber")
public class ApiController {

    private Random random = new Random();

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
        String token = generateToken();
        ViberResponse viberResponse = ViberResponse.builder().status(ViberStatus.SRVC_SUCCESS.getStatus()).messageToken(token).build();
        ResponseEntity<ViberResponse> resp = ResponseEntity.ok(viberResponse);
        log.info("Request: " + viberRequest + "\t Response: " + viberResponse);
        asyncRequestExecutionService.startFutureRequestExecutionService(token);
        return resp;
    }

    private String generateToken() {
        String token;
        token = String.valueOf(System.nanoTime()) + String.valueOf(random.nextInt(999));
        return token;
    }


}
