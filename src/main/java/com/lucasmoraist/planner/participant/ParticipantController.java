package com.lucasmoraist.planner.participant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/participants")
@Slf4j
public class ParticipantController {

    @Autowired
    private ParticipantService service;

    @PostMapping("{id}/confirm")
    public ResponseEntity<Participant> confirmedParticipants(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        Participant participant = this.service.confirmedParticipants(id, payload);

        log.info("Participant confirmed");

        return ResponseEntity.ok().body(participant);
    }

}
