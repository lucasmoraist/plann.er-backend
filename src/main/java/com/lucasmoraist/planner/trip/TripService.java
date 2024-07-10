package com.lucasmoraist.planner.trip;

import com.lucasmoraist.planner.activity.ActivityData;
import com.lucasmoraist.planner.activity.ActivityRequestPayload;
import com.lucasmoraist.planner.activity.ActivityResponse;
import com.lucasmoraist.planner.activity.ActivityService;
import com.lucasmoraist.planner.exception.DatesInconsistency;
import com.lucasmoraist.planner.exception.ResourceNotFound;
import com.lucasmoraist.planner.link.LinkData;
import com.lucasmoraist.planner.link.LinkRequestPayload;
import com.lucasmoraist.planner.link.LinkResponse;
import com.lucasmoraist.planner.link.LinkService;
import com.lucasmoraist.planner.participant.ParticipantCreateResponse;
import com.lucasmoraist.planner.participant.ParticipantData;
import com.lucasmoraist.planner.participant.ParticipantRequestPayload;
import com.lucasmoraist.planner.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final ParticipantService participantService;
    private final ActivityService activityService;
    private final LinkService linkService;

    // Trip

    public TripCreateResponse createTrip(TripRequestPayload payload) {
        Trip trip = new Trip(payload);

        LocalDateTime startAt = trip.getStartAt();
        LocalDateTime endAt = trip.getEndAt();

        if(startAt.isAfter(endAt)) throw new DatesInconsistency();

        this.tripRepository.save(trip);
        this.participantService.registerParticipantsToEvent(payload.emailsToInvite(), trip);
        return new TripCreateResponse(trip.getId());
    }

    public Trip getTripDetails(UUID id) {
        return this.tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Trip not found"));
    }

    public Trip updateTrip(UUID id, TripRequestPayload payload) {
        Trip trip = this.getTripDetails(id);
        trip.setDestination(payload.destination());
        trip.setStartAt(LocalDateTime.parse(payload.startAt(), DateTimeFormatter.ISO_DATE_TIME));
        trip.setEndAt(LocalDateTime.parse(payload.endAt(), DateTimeFormatter.ISO_DATE_TIME));
        this.tripRepository.save(trip);
        return trip;
    }

    public Trip confirmTrip(UUID id) {
        Trip trip = this.getTripDetails(id);
        trip.setConfirmed(true);
        this.participantService.triggerConfirmationEmailToParticipants(id);
        this.tripRepository.save(trip);
        return trip;
    }

    // Participant

    public ParticipantCreateResponse inviteParticipant(UUID id, ParticipantRequestPayload payload) {
        Trip trip = this.getTripDetails(id);
        ParticipantCreateResponse response = this.participantService.registerParticipantToEvent(payload.email(), trip);
        if (trip.isConfirmed()) this.participantService.triggerConfirmationEmailToParticipant(payload.email());
        return response;

    }

    public List<ParticipantData> getAllParticipants(UUID idTrip) {
        return this.participantService.getAllParticipants(idTrip);
    }

    // Activity

    public ActivityResponse registerActivity(UUID id, ActivityRequestPayload payload) {
        Trip trip = this.getTripDetails(id);
        return this.activityService.registerActivity(payload, trip);
    }

    public List<ActivityData> getAllActivities(UUID idTrip) {
        return this.activityService.getAllActivities(idTrip);
    }

    // Link
    public LinkResponse registerLink(UUID id, LinkRequestPayload payload) {
        Trip trip = this.getTripDetails(id);
        return this.linkService.registerLink(payload, trip);
    }

    public List<LinkData> getAllLinks(UUID idTrip) {
        return this.linkService.getAllLinksFromTrip(idTrip);
    }
}
