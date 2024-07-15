package com.lucasmoraist.planner.trip;

import com.lucasmoraist.planner.activity.ActivityData;
import com.lucasmoraist.planner.activity.ActivityDataResponse;
import com.lucasmoraist.planner.activity.ActivityRequestPayload;
import com.lucasmoraist.planner.activity.ActivityResponse;
import com.lucasmoraist.planner.link.LinkData;
import com.lucasmoraist.planner.link.LinkRequestPayload;
import com.lucasmoraist.planner.link.LinkResponse;
import com.lucasmoraist.planner.participant.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/plann.er-frontend/trips")
@Slf4j
public class TripController {

    @Autowired
    private TripService service;

    // Trip

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
        log.info("Creating a new trip: {}", payload);
        TripCreateResponse trip = this.service.createTrip(payload);
        return ResponseEntity.ok().body(trip);
    }

    @GetMapping("{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {
        log.info("Getting trip details: {}", id);
        Trip trip = this.service.getTripDetails(id);
        log.info("Getting trip details: {}", trip);
        return ResponseEntity.ok().body(trip);
    }

    @PutMapping("{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {
        log.info("Updating trip: {}", id);
        Trip trip = this.service.updateTrip(id, payload);
        log.info("Updated trip: {}", trip);
        return ResponseEntity.ok().body(trip);
    }

    @GetMapping("{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
        log.info("Confirming trip: {}", id);
        Trip trip = this.service.confirmTrip(id);
        return ResponseEntity.ok().body(trip);
    }

    // Participant

    @PostMapping("{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {
        log.info("Searching trip: {}", id);
        ParticipantCreateResponse response = this.service.inviteParticipant(id, payload);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("{idTrip}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID idTrip) {
        log.info("Getting all participants from trip: {}", idTrip);
        List<ParticipantData> participantList = this.service.getAllParticipants(idTrip);
        return ResponseEntity.ok().body(participantList);
    }

    // Activity

    @GetMapping("{idTrip}/activities")
    public ResponseEntity<List<ActivityDataResponse>> getAllActivities(@PathVariable UUID idTrip) {
        log.info("Getting all participants from trip: {}", idTrip);
        List<ActivityDataResponse> activityDataList = this.service.getAllActivities(idTrip);
        return ResponseEntity.ok().body(activityDataList);
    }

    @PostMapping("{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload) {
        log.info("Searching trip: {}", id);
        ActivityResponse response = this.service.registerActivity(id, payload);
        log.info("Activity registered: {}", response);
        return ResponseEntity.ok().body(response);

    }

    // Link
    @PostMapping("{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload) {
        log.info("Searching trip: {}", id);
        LinkResponse response = this.service.registerLink(id, payload);
        log.info("Link registered: {}", response);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("{idTrip}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID idTrip) {
        log.info("Getting all links from trip: {}", idTrip);
        List<LinkData> linkDataList = this.service.getAllLinks(idTrip);
        return ResponseEntity.ok().body(linkDataList);
    }

}
