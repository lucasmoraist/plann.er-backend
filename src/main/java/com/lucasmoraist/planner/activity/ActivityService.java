package com.lucasmoraist.planner.activity;

import com.lucasmoraist.planner.exception.DatesInconsistency;
import com.lucasmoraist.planner.exception.ResourceNotFound;
import com.lucasmoraist.planner.trip.Trip;
import com.lucasmoraist.planner.trip.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    @Autowired
    private TripRepository tripRepository;

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

    public List<ActivityDataResponse> getAllActivities(UUID tripId){
        Trip trip = this.tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFound("Trip not found"));

        LocalDate startAt = trip.getStartAt().toLocalDate();
        LocalDate endAt = trip.getEndAt().toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(startAt, endAt);

        List<Activity> allActivities = this.repository.findByTripId(tripId);

        List<ActivityDataResponse> response = new ArrayList<>();

        for (long i = 0; i <= daysBetween; i++) {
            LocalDate date = startAt.plusDays(i);
            List<ActivityData> activitiesForDate = allActivities
                    .stream()
                    .filter(activity -> activity.getOccursAt().toLocalDate().isEqual(date))
                    .map(activity -> new ActivityData(activity.getId(), activity.getTitle(), activity.getOccursAt()))
                    .collect(Collectors.toList());

            response.add(new ActivityDataResponse(date, activitiesForDate));
        }
        return response;
    }

}
