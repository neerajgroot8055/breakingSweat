package com.example.activityservice.service;

import com.example.activityservice.ActivityRepository;
import com.example.activityservice.dto.activityRequest;
import com.example.activityservice.dto.activityResponse;
import com.example.activityservice.model.activity;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class activityService {
    private final ActivityRepository activityRepository ;
    private final UserValidationService userValidationService ;


    public activityResponse trackActivity(activityRequest request) {

       boolean isValid = userValidationService.validateUser(request.getUserId());
       if(!isValid){
           throw new RuntimeException("Invalid User");
       }
        activity activity = com.example.activityservice.model.activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned((request.getCaloriesBurned()))
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build() ;

        activity savedActivity = activityRepository.save(activity) ;
        return mapToResponse(savedActivity) ;
    }

    private activityResponse mapToResponse(activity savedActivity) {
        activityResponse response = new activityResponse() ;
        response.setId(savedActivity.getId());
        response.setUserId(savedActivity.getUserId());
        response.setType(savedActivity.getType());
        response.setDuration(savedActivity.getDuration());
        response.setCaloriesBurned(savedActivity.getCaloriesBurned());
        response.setStartTime(savedActivity.getStartTime());
        response.setAdditionalMetrics(savedActivity.getAdditionalMetrics());
        response.setCreatedAt(savedActivity.getCreatedAt());
        response.setUpdatedAt(savedActivity.getUpdatedAt());

        return response ;
    }
}
