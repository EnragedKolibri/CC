package com.example.copicatkurilshika.controller;

import com.example.copicatkurilshika.httpServices.AsyncRequestExecutionService;
import com.example.copicatkurilshika.viberEntitys.ViberRequest;
import com.example.copicatkurilshika.viberEntitys.ViberResponse;
import com.example.copicatkurilshika.viberEntitys.ViberStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@RestController
public class ApiController {

    private Random random = new Random();

    @Value("${test-responce}")
    private String testText;
    @Value("${custom-responce}")
    private String customResponce;

    @Autowired
    private AsyncRequestExecutionService asyncRequestExecutionService;

    @GetMapping(value = "/test",produces = {"application/json"})
    public @ResponseBody String hello()
    {
        return testText;
    }

    @PostMapping(value = "/customResponce", produces = {"application/json"}, consumes = {"application/json"})
    public  String custom(@RequestBody String request)
    {
        return customResponce;
    }

    @PostMapping(value = "/viber")
    public  ResponseEntity<ViberResponse> post(@RequestBody ViberRequest viberRequest) throws UnsupportedEncodingException {
        String token = generateToken();
        asyncRequestExecutionService.startFutureRequestExecutionService(token);
        return ResponseEntity.ok(ViberResponse.builder().status(ViberStatus.SRVC_SUCCESS).messageToken(token).build());
    }

   private String generateToken() {
        String token;
        token = String.valueOf(System.nanoTime())+ String.valueOf(random.nextInt(999));
        return token;
    }


}
