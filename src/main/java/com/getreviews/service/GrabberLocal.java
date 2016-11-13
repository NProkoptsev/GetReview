package com.getreviews.service;

import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.getreviews.web.rest.util.HeaderUtil;

@RestController
@RequestMapping("/grabber")
public class GrabberLocal {
    
    @RequestMapping(value = "/local",
            method = RequestMethod.GET)
    @Timed
    public ResponseEntity<String> test() {
        /*new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.println("!!!!!!test!!!!!! - " + i);
                    try {
                        Thread.sleep(1000);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.run();*/
        

        printitout();
        
        return ResponseEntity.ok()
                .body("The process is started");
    }
    
    @Async
    public void printitout() {
        for (int i = 0; i < 10; i++) {
            System.out.println("!!!!!!test!!!!!! - " + i);
            try {
                Thread.sleep(1000);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
