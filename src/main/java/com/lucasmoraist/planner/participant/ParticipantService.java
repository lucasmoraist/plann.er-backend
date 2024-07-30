package com.lucasmoraist.planner.participant;

import com.lucasmoraist.planner.exception.ResourceNotFound;
import com.lucasmoraist.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public void registerParticipantsToEvent(List<String> participantsToInvite, Trip trip) {
        List<Participant> participants = participantsToInvite
                .stream()
                .map(email -> new Participant(email, trip))
                .toList();
        this.participantRepository.saveAll(participants);
    }

    public ParticipantCreateResponse registerParticipantToEvent(String email, Trip trip) {
        Participant participant = new Participant(email, trip);
        this.participantRepository.save(participant);
        return new ParticipantCreateResponse(participant.getId());
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId) {
    }

    public void triggerConfirmationEmailToParticipant(String email) {
    }

    public List<ParticipantData> getAllParticipants(UUID tripId) {
        return this.participantRepository.findByTripId(tripId)
                .stream()
                .map(participant -> new ParticipantData(participant.getId(), participant.getName(), participant.getEmail(), participant.isConfirmed()))
                .toList();
    }

    public Participant confirmedParticipants(UUID id, ParticipantRequestPayload payload) {
        Optional<Participant> participants = this.participantRepository.findById(id);
        if (participants.isEmpty()) throw new ResourceNotFound("Participant not found");
        Participant participant = participants.get();
        participant.setConfirmed(true);
        participant.setName(payload.name());
        this.participantRepository.save(participant);
        return participant;
    }

}
