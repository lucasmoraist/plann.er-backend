package com.lucasmoraist.planner.trips;

import com.lucasmoraist.planner.exceptions.ResourceNotFound;
import com.lucasmoraist.planner.participant.ParticipationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
@Slf4j
@RequiredArgsConstructor
public class TripsController {

    private final TripsRepository repository;
    private final ParticipationService participantService;

    @PostMapping
    public ResponseEntity<TripCreateResponse> create(@RequestBody TripRequestPayload payload) {
        log.info("Creating a new trip: {}", payload);
        Trips trips = new Trips(payload);
        this.repository.save(trips);
        this.participantService.registerParticipantToEvent(payload.emailsToInvite(), trips.getId());
        return ResponseEntity.ok().body(new TripCreateResponse(trips.getId()));
    }

    @GetMapping("{id}")
    public ResponseEntity<Trips> getTripDetails(@PathVariable UUID id){
        log.info("Getting trip details: {}", id);
        Optional<Trips> trips = this.repository.findById(id);
        log.info("Getting trip details: {}", trips);
        
        return trips.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFound("Trip not found"));
    }

}
