package com.example.activityservice.controller;


import com.example.activityservice.ActivityserviceApplication;
import com.example.activityservice.dto.activityRequest;
import com.example.activityservice.dto.activityResponse;
import com.example.activityservice.service.activityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {
    private activityService activityService ;

    @PostMapping
    public ResponseEntity<activityResponse> trackActivity(@RequestBody activityRequest request , @RequestHeader("X-User-ID") String userId){
        request.setUserId(userId);
        return ResponseEntity.ok(activityService.trackActivity(request)) ;
    }
    @GetMapping
    public ResponseEntity<List<activityResponse>> getUserActivities(@RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }
}
