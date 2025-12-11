package com.example.activityservice;

import com.example.activityservice.model.activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends MongoRepository<activity,String> {
}
