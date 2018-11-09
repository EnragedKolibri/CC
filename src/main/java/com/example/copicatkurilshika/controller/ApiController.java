package com.example.copicatkurilshika.controller;

import com.example.copicatkurilshika.viberEntitys.ViberRequest;
import com.example.copicatkurilshika.viberEntitys.ViberResponse;
import com.example.copicatkurilshika.viberEntitys.ViberStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
public class ApiController {

    private Random random = new Random();

    @Value("${email}")
    private String testText;
    @Value("${custom-responce-viber}")
    private String customResponceViber;
    @Value("${custom-responce}")
    private String customResponce;

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
    public  ResponseEntity<ViberResponse> post(@RequestBody ViberRequest viberRequest)
    {
        return ResponseEntity.ok(ViberResponse.builder().status(ViberStatus.SRVC_SUCCESS).messageToken(generateToken()).build());
    }

    @PostMapping(value = "/viber/customResponce", produces = {"application/json"})
    public  String customForViber(@RequestBody ViberRequest viberRequest)
    {
        return customResponceViber;
    }


   private String generateToken() {
        String token;
        token = String.valueOf(System.nanoTime())+ String.valueOf(random.nextInt(999));
        return token;
    }


}
