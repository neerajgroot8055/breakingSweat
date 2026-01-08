package com.example.activityservice;

import com.example.activityservice.model.activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends MongoRepository<activity,String> {
    List<activity> findByUserId(String userId);
}
