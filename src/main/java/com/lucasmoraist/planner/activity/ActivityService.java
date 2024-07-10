package com.lucasmoraist.planner.activity;

import com.lucasmoraist.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    public ActivityResponse registerActivity(ActivityRequestPayload payload, Trip trip){
        Activity activity = Activity.builder()
                .title(payload.title())
                .occursAt(LocalDateTime.parse(payload.occursAt(), DateTimeFormatter.ISO_DATE_TIME))
                .trip(trip)
                .build();

        this.repository.save(activity);

        return new ActivityResponse(activity.getId());
    }

    public List<ActivityData> getAllActivities(UUID tripId){
        return this.repository.findByTripId(tripId)
                .stream()
                .map(activity -> new ActivityData(activity.getId(), activity.getTitle(), activity.getOccursAt()))
                .toList();
    }

}
