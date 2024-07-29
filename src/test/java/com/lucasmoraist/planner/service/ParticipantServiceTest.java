package com.lucasmoraist.planner.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import com.lucasmoraist.planner.exception.ResourceNotFound;
import com.lucasmoraist.planner.participant.*;
import com.lucasmoraist.planner.trip.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    private Trip trip;
    private UUID tripId;
    private UUID participantId;
    private String email;
    private ParticipantRequestPayload participantPayload;
    private Participant participant;
    private ParticipantCreateResponse participantCreateResponse;
    private List<Participant> participants;
    private List<ParticipantData> participantDataList;

    @BeforeEach
    public void setup() {
        trip = new Trip();
        tripId = UUID.randomUUID();
        trip.setId(tripId);

        email = "participant@example.com";

        participantPayload = new ParticipantRequestPayload("John Doe", email);

        participant = new Participant(email, trip);
        participantId = UUID.randomUUID();
        participant.setId(participantId);
        participant.setName("John Doe");
        participant.setConfirmed(true);

        participantCreateResponse = new ParticipantCreateResponse(participant.getId());

        participants = Collections.singletonList(participant);

        participantDataList = List.of(new ParticipantData(participant.getId(), "John Doe", email, true));
    }

    @Test
    @DisplayName("RegisterParticipantsToEvent: Should save all participants successfully")
    public void case01() {
        ArgumentCaptor<List<Participant>> captor = ArgumentCaptor.forClass(List.class);

        participantService.registerParticipantsToEvent(Collections.singletonList(email), trip);

        verify(participantRepository).saveAll(captor.capture());

        List<Participant> capturedParticipants = captor.getValue();

        assertEquals(1, capturedParticipants.size());
        Participant capturedParticipant = capturedParticipants.get(0);
        assertEquals(email, capturedParticipant.getEmail());
        assertEquals(trip, capturedParticipant.getTrip());
    }

    @Test
    @DisplayName("RegisterParticipantToEvent: Should register a participant successfully")
    public void case02() {
        when(participantRepository.save(any(Participant.class))).thenAnswer(invocation -> {
            Participant participant = invocation.getArgument(0);
            participant.setId(participantId);
            return participant;
        });

        ParticipantCreateResponse response = participantService.registerParticipantToEvent(email, trip);
        assertEquals(participantCreateResponse, response);
    }

    @Test
    @DisplayName("GetAllParticipants: Should return list of participants successfully")
    public void case03() {
        when(participantRepository.findByTripId(tripId)).thenReturn(participants);

        List<ParticipantData> participantsData = participantService.getAllParticipants(tripId);

        assertEquals(participantDataList, participantsData);
    }

    @Test
    @DisplayName("GetAllParticipants: Should return empty list if no participants are found")
    public void case04() {
        when(participantRepository.findByTripId(tripId)).thenReturn(List.of());

        List<ParticipantData> participantsData = participantService.getAllParticipants(tripId);

        assertTrue(participantsData.isEmpty());
    }

    @Test
    @DisplayName("ConfirmedParticipants: Should confirm participant and update details successfully")
    public void case05() {
        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        Participant confirmedParticipant = participantService.confirmedParticipants(participant.getId(), participantPayload);

        assertTrue(confirmedParticipant.isConfirmed());
        assertEquals(participantPayload.name(), confirmedParticipant.getName());
    }

    @Test
    @DisplayName("ConfirmedParticipants: Should throw ResourceNotFound when participant is not found")
    public void case06() {
        when(participantRepository.findById(participant.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> participantService.confirmedParticipants(participant.getId(), participantPayload));
    }

    @Test
    @DisplayName("TriggerConfirmationEmailToParticipants: Should execute without error")
    public void case07() {
        // Método vazio, apenas verificando a execução
        assertDoesNotThrow(() -> participantService.triggerConfirmationEmailToParticipants(tripId));
    }

    @Test
    @DisplayName("TriggerConfirmationEmailToParticipant: Should execute without error")
    public void case08() {
        // Método vazio, apenas verificando a execução
        assertDoesNotThrow(() -> participantService.triggerConfirmationEmailToParticipant(email));
    }

}
