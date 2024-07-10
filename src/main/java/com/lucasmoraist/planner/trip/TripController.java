package com.lucasmoraist.planner.trip;

import com.lucasmoraist.planner.activity.ActivityData;
import com.lucasmoraist.planner.activity.ActivityRequestPayload;
import com.lucasmoraist.planner.activity.ActivityResponse;
import com.lucasmoraist.planner.activity.ActivityService;
import com.lucasmoraist.planner.exception.ResourceNotFound;
import com.lucasmoraist.planner.link.LinkData;
import com.lucasmoraist.planner.link.LinkRequestPayload;
import com.lucasmoraist.planner.link.LinkResponse;
import com.lucasmoraist.planner.link.LinkService;
import com.lucasmoraist.planner.participant.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
@Slf4j
@RequiredArgsConstructor
public class TripController {

    private final TripRepository tripRepository;
    private final ParticipantService participantService;
    private final ActivityService activityService;
    private final LinkService linkService;

    // Trip

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
        log.info("Creating a new trip: {}", payload);
        Trip trip = new Trip(payload);
        this.tripRepository.save(trip);
        this.participantService.registerParticipantsToEvent(payload.emailsToInvite(), trip);
        return ResponseEntity.ok().body(new TripCreateResponse(trip.getId()));
    }

    @GetMapping("{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {
        log.info("Getting trip details: {}", id);
        Optional<Trip> trips = this.tripRepository.findById(id);
        log.info("Getting trip details: {}", trips);

        return trips.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFound("Trip not found"));
    }

    @PutMapping("{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {
        log.info("Updating trip: {}", id);

        Optional<Trip> trips = this.tripRepository.findById(id);
        if (trips.isEmpty()) throw new ResourceNotFound("Trip not found");

        Trip trip = trips.get();
        trip.setDestination(payload.destination());
        trip.setStartAt(LocalDateTime.parse(payload.startAt(), DateTimeFormatter.ISO_DATE_TIME));
        trip.setEndAt(LocalDateTime.parse(payload.endAt(), DateTimeFormatter.ISO_DATE_TIME));

        this.tripRepository.save(trip);
        return ResponseEntity.ok().body(trip);
    }

    @GetMapping("{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
        log.info("Confirming trip: {}", id);

        Optional<Trip> trips = this.tripRepository.findById(id);
        if (trips.isEmpty()) throw new ResourceNotFound("Trip not found");

        Trip trip = trips.get();
        trip.setConfirmed(true);

        this.participantService.triggerConfirmationEmailToParticipants(id);
        this.tripRepository.save(trip);

        return ResponseEntity.ok().body(trip);
    }

    // Participant

    @PostMapping("{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {

        log.info("Searching trip: {}", id);

        Optional<Trip> trips = this.tripRepository.findById(id);
        if (trips.isEmpty()) throw new ResourceNotFound("Trip not found");
        Trip trip = trips.get();

        ParticipantCreateResponse response = this.participantService.registerParticipantToEvent(payload.email(), trip);

        if (trip.isConfirmed()) this.participantService.triggerConfirmationEmailToParticipant(payload.email());

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("{idTrip}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID idTrip) {
        log.info("Getting all participants from trip: {}", idTrip);

        List<ParticipantData> participantList = this.participantService.getAllParticipants(idTrip);

        return ResponseEntity.ok().body(participantList);
    }

    // Activity

    @GetMapping("{idTrip}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID idTrip) {
        log.info("Getting all participants from trip: {}", idTrip);

        List<ActivityData> activityDataList = this.activityService.getAllActivities(idTrip);

        return ResponseEntity.ok().body(activityDataList);
    }

    @PostMapping("{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id ,@RequestBody ActivityRequestPayload payload) {

        log.info("Searching trip: {}", id);

        Optional<Trip> trips = this.tripRepository.findById(id);
        if (trips.isEmpty()) throw new ResourceNotFound("Trip not found");
        Trip trip = trips.get();

        ActivityResponse response = this.activityService.registerActivity(payload, trip);

        return ResponseEntity.ok().body(response);

    }

    // Link
    @PostMapping("{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id , @RequestBody LinkRequestPayload payload) {

        log.info("Searching trip: {}", id);

        Optional<Trip> trips = this.tripRepository.findById(id);
        if (trips.isEmpty()) throw new ResourceNotFound("Trip not found");
        Trip trip = trips.get();

        LinkResponse response = this.linkService.registerLink(payload, trip);

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("{idTrip}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID idTrip) {
        log.info("Getting all links from trip: {}", idTrip);

        List<LinkData> linkDataList = this.linkService.getAllLinksFromTrip(idTrip);

        return ResponseEntity.ok().body(linkDataList);
    }

}
