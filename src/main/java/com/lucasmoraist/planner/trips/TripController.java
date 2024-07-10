package com.lucasmoraist.planner.trips;

import com.lucasmoraist.planner.exceptions.ResourceNotFound;
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
    private final ParticipationService participantService;

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

    @PostMapping("{id}/invite")
    public ResponseEntity<ParticipationCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {

        log.info("Searching trip: {}", id);

        Optional<Trip> trips = this.tripRepository.findById(id);
        if (trips.isEmpty()) throw new ResourceNotFound("Trip not found");
        Trip trip = trips.get();

        ParticipationCreateResponse response = this.participantService.registerParticipantToEvent(payload.email(), trip);

        if (trip.isConfirmed()) this.participantService.triggerConfirmationEmailToParticipant(payload.email());

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("{idTrip}/participants")
    public ResponseEntity<List<ParticipantsData>> getAllParticipants(@PathVariable UUID idTrip) {
        log.info("Getting all participants from trip: {}", idTrip);

        List<ParticipantsData> participantList = this.participantService.getAllParticipants(idTrip);

        return ResponseEntity.ok().body(participantList);
    }
}
