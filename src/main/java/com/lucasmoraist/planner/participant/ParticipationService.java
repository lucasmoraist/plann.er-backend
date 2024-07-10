package com.lucasmoraist.planner.participant;

import com.lucasmoraist.planner.trips.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipationService {

    @Autowired
    private ParticipantRepository participantRepository;

    public void registerParticipantsToEvent(List<String> participantsToInvite, Trip trip){
        List<Participant> participants = participantsToInvite
                .stream()
                .map((email -> new Participant(email, trip)))
                .toList();

        this.participantRepository.saveAll(participants);

        System.out.println(participants.get(0).getId());

    }

    public ParticipationCreateResponse registerParticipantToEvent(String email, Trip trip){
        Participant participant = new Participant(email, trip);
        this.participantRepository.save(participant);
        return new ParticipationCreateResponse(participant.getId());
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId){

    }

    public void triggerConfirmationEmailToParticipant(String email){

    }

    public List<ParticipantsData> getAllParticipants(UUID tripId){
        return this.participantRepository.findByTripId(tripId)
                .stream()
                .map(participant -> new ParticipantsData(participant.getId(), participant.getName(), participant.getEmail(), participant.isConfirmed()))
                .toList();
    }

}
