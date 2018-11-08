package com.example.copicatkurilshika.controller;

import com.example.copicatkurilshika.viberEntitys.ViberRequest;
import com.example.copicatkurilshika.viberEntitys.ViberResponse;
import com.example.copicatkurilshika.viberEntitys.ViberStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
public class ApiController {

    private Random random = new Random();

    @GetMapping("/test")
    public @ResponseBody String hello()
    {
        return "it's works";
    }

    @PostMapping(value = "/viber")
    public  ResponseEntity<ViberResponse> post(@RequestBody ViberRequest viberRequest)
    {
        return ResponseEntity.ok(ViberResponse.builder().status(ViberStatus.SRVC_SUCCESS).messageToken(generateToken()).build());
    }

   private String generateToken() {
        String token;
        token = String.valueOf(System.nanoTime())+ String.valueOf(random.nextInt(999));
        return token;
    }


}
