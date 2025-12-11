package com.example.activityservice.controller;


import com.example.activityservice.ActivityserviceApplication;
import com.example.activityservice.dto.activityRequest;
import com.example.activityservice.dto.activityResponse;
import com.example.activityservice.service.activityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {
    private activityService activityService ;

    @PostMapping
    public ResponseEntity<activityResponse> trackActivity(@RequestBody activityRequest request){
        return ResponseEntity.ok(activityService.trackActivity(request)) ;
    }
}
