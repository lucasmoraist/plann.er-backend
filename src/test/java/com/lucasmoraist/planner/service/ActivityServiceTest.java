package com.lucasmoraist.planner.service;

import com.lucasmoraist.planner.activity.*;
import com.lucasmoraist.planner.exception.DatesInconsistency;
import com.lucasmoraist.planner.exception.ResourceNotFound;
import com.lucasmoraist.planner.link.Link;
import com.lucasmoraist.planner.trip.Trip;
import com.lucasmoraist.planner.trip.TripRepository;
import com.lucasmoraist.planner.trip.TripRequestPayload;
import com.lucasmoraist.planner.trip.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private ActivityService activityService;

    private Trip trip;
    private UUID tripId;
    private UUID activityId;
    private String title;
    private LocalDateTime occursAt;
    private Activity activity;
    private ActivityRequestPayload activityPayload;
    private ActivityResponse activityResponse;
    private List<Activity> activities;
    private List<ActivityDataResponse> activityDataResponses;
    private List<ActivityData> activityData;

    @BeforeEach
    public void setup() {
        trip = new Trip();
        tripId = UUID.randomUUID();
        trip.setId(tripId);

        LocalDateTime startAt = LocalDateTime.now().plusDays(1);
        LocalDateTime endAt = startAt.plusDays(5);
        trip.setStartAt(startAt);
        trip.setEndAt(endAt);

        activityId = UUID.randomUUID();
        title = "Sample Activity";
        occursAt = startAt.plusDays(1);

        activityPayload = new ActivityRequestPayload(title, occursAt.format(DateTimeFormatter.ISO_DATE_TIME));

        activity = Activity.builder().title(title).occursAt(occursAt).trip(trip).build();
        activity.setId(activityId);

        activityResponse = new ActivityResponse(activityId);

        activities = List.of(activity);

        activityData = List.of(
                new ActivityData(activityId, title, occursAt)
        );
    }

    @Test
    @DisplayName("RegisterActivity: Should register an activity successfully")
    public void case01() {
        when(activityRepository.save(any(Activity.class))).thenAnswer(invocation -> {
            Activity activity = invocation.getArgument(0);
            activity.setId(activityId);
            return activity;
        });

        ActivityResponse response = activityService.registerActivity(activityPayload, trip);

        assertEquals(activityResponse, response);
        ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
        verify(activityRepository).save(captor.capture());

        Activity capturedActivity = captor.getValue();
        assertEquals(title, capturedActivity.getTitle());
        assertEquals(occursAt, capturedActivity.getOccursAt());
        assertEquals(trip, capturedActivity.getTrip());
    }

    @Test
    @DisplayName("RegisterActivity: Should throw DatesInconsistency when occursAt is before trip start date")
    public void case02() {
        LocalDateTime invalidOccursAt = trip.getStartAt().minusDays(1);
        activityPayload = new ActivityRequestPayload(title, invalidOccursAt.format(DateTimeFormatter.ISO_DATE_TIME));

        DatesInconsistency exception = assertThrows(DatesInconsistency.class, () -> {
            activityService.registerActivity(activityPayload, trip);
        });

        assertEquals("This event will occur before the travel date", exception.getMessage());
    }

    @Test
    @DisplayName("RegisterActivity: Should throw DatesInconsistency when occursAt is after trip end date")
    public void case03() {
        LocalDateTime invalidOccursAt = trip.getEndAt().plusDays(2);
        activityPayload = new ActivityRequestPayload(title, invalidOccursAt.format(DateTimeFormatter.ISO_DATE_TIME));

        DatesInconsistency exception = assertThrows(DatesInconsistency.class, () -> {
            activityService.registerActivity(activityPayload, trip);
        });

        assertEquals("This event will occur after the travel date", exception.getMessage());
    }

    @Test
    @DisplayName("GetAllActivities: Should throw ResourceNotFound when trip is not found")
    public void case04() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
            activityService.getAllActivities(tripId);
        });

        assertEquals("Trip not found", exception.getMessage());
    }

    @Test
    @DisplayName("GetAllActivities: Should return list of activities successfully")
    public void case05() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(activityRepository.findByTripId(tripId)).thenReturn(activities);

        List<ActivityDataResponse> expectedActivityDataResponses = new ArrayList<>();
        LocalDate startAt = trip.getStartAt().toLocalDate();
        LocalDate endAt = trip.getEndAt().toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(startAt, endAt);

        for (long i = 0; i <= daysBetween; i++) {
            LocalDate date = startAt.plusDays(i);
            List<ActivityData> activitiesForDate = activities.stream()
                    .filter(activity -> activity.getOccursAt().toLocalDate().isEqual(date))
                    .map(activity -> new ActivityData(activity.getId(), activity.getTitle(), activity.getOccursAt()))
                    .collect(Collectors.toList());
            expectedActivityDataResponses.add(new ActivityDataResponse(date, activitiesForDate));
        }

        List<ActivityDataResponse> returnedActivities = activityService.getAllActivities(tripId);

        assertEquals(expectedActivityDataResponses, returnedActivities);
    }

}
