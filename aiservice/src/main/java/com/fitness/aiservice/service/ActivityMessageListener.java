package com.fitness.aiservice.service;


import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.model.activity;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAiService activityAiService ;
    private final RecommendationRepository recommendationRepository ;

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "activity-processor-group")
    public void processActivity(activity activity){
        log.info("Received activity: {}", activity);
        Recommendation recommendation = activityAiService.generateRecommendation(activity);
        recommendationRepository.save(recommendation) ;

    }

}
