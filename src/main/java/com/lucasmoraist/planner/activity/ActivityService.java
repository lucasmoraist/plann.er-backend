package com.lucasmoraist.planner.activity;

import com.lucasmoraist.planner.exception.DatesInconsistency;
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

        LocalDateTime startAt = activity.getTrip().getStartAt();
        LocalDateTime endAt = activity.getTrip().getEndAt();

        // isBefore -> ele faz a função de verificar se a data é anterior a outra
        // isAfter -> ele faz a função de verificar se a data é posterior a outra
        if(activity.getOccursAt().isBefore(startAt)){
            throw new DatesInconsistency("This event will occur before the travel date");
        }else if(activity.getOccursAt().isAfter(endAt)){
            throw new DatesInconsistency("This event will occur after the travel date");
        }

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
