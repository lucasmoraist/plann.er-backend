package com.lucasmoraist.planner.participant;

import com.lucasmoraist.planner.exceptions.ResourceNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired
    private ParticipantRepository participantRepository;

    @PostMapping("{id}/confirm")
    public ResponseEntity<Participant> confirmedParticipants(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        Optional<Participant> participants = this.participantRepository.findById(id);

        if(participants.isEmpty()) throw new ResourceNotFound("Participant not found");

        Participant participant = participants.get();
        participant.setConfirmed(true);
        participant.setName(payload.name());

        this.participantRepository.save(participant);

        return ResponseEntity.ok().body(participant);
    }

}
