package com.lucasmoraist.planner.service;

import com.lucasmoraist.planner.activity.*;
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
import com.lucasmoraist.planner.trip.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private ParticipantService participantService;

    @Mock
    private ActivityService activityService;

    @Mock
    private LinkService linkService;

    @InjectMocks
    private TripService tripService;

    private TripRequestPayload validPayload;
    private Trip trip;
    private UUID tripId;
    private ParticipantRequestPayload participantPayload;
    private ParticipantCreateResponse participantResponse;
    private ParticipantData participantData;
    private ActivityRequestPayload activityPayload;
    private ActivityResponse activityResponse;
    private List<ActivityDataResponse> activityList;
    private LinkRequestPayload linkPayload;
    private LinkResponse linkResponse;
    private List<LinkData> linkList;


    @BeforeEach
    public void setup() {
        validPayload = new TripRequestPayload(
                "São Paulo",
                "2022-01-01T00:00:00",
                "2022-01-02T00:00:00",
                List.of("fernanda@fernando.com", "felipe@felipe.com"),
                "Lucas",
                "lucas@lucas.com"
        );

        trip = new Trip(validPayload);
        tripId = UUID.randomUUID();
        trip.setId(tripId);

        participantPayload = new ParticipantRequestPayload(
                "Lucas",
                "lucas@lucas.com"
        );

        participantResponse = new ParticipantCreateResponse(UUID.randomUUID());

        participantData = new ParticipantData(
                participantResponse.id(),
                "Lucas",
                "lucas@lucas.com",
                false
        );

        activityPayload = new ActivityRequestPayload(
                "City Tour",
                "2022-01-01T10:00:00"
        );
        activityResponse = new ActivityResponse(UUID.randomUUID());
        activityList = Arrays.asList(new ActivityDataResponse(
                LocalDate.of(2022, 1, 1),
                List.of(
                        new ActivityData(
                                activityResponse.id(),
                                "City Tour",
                                LocalDateTime.of(2022, 1, 1, 10, 0)
                        )
                )
        ));

        linkPayload = new LinkRequestPayload("Google", "https://www.google.com");
        linkResponse = new LinkResponse(UUID.randomUUID());
        linkList = Arrays.asList(new LinkData(linkResponse.linkId(), "Google", "https://www.google.com"));
    }

    @Test
    @DisplayName("CreateTrip: Should create a trip successfully")
    public void case01() {
        // O thenAnswer garante que o ID seja atribuído ao trip
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> {
            Trip savedTrip = invocation.getArgument(0);
            savedTrip.setId(tripId);
            return savedTrip;
        });

        TripCreateResponse response = tripService.createTrip(validPayload);

        assertEquals(tripId, response.tripId());
    }

    @Test
    @DisplayName("CreateTrip: Should throw DatesInconsistency when startAt is after endAt")
    public void case02() {
        validPayload = new TripRequestPayload(
                "São Paulo",
                "2022-01-02T00:00:00",
                "2022-01-01T00:00:00",
                List.of("fernanda@fernando.com", "felipe@felipe.com"),
                "Lucas",
                "lucas@lucas.com"
        );

        assertThrows(DatesInconsistency.class, () -> tripService.createTrip(validPayload));
    }

    @Test
    @DisplayName("GetTripDetails: Should return trip details successfully")
    public void case03() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

        Trip foundTrip = tripService.getTripDetails(tripId);

        assertEquals(trip, foundTrip);
    }

    @Test
    @DisplayName("GetTripDetails: Should throw ResourceNotFound when trip is not found")
    public void case04() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> tripService.getTripDetails(tripId));
    }

    @Test
    @DisplayName("UpdateTrip: Should update trip successfully")
    public void case05() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

        TripRequestPayload updatedPayload = new TripRequestPayload(
                "New Destination",
                "2022-01-01T00:00:00",
                "2022-01-04T00:00:00",
                List.of("fernanda@fernando.com", "felipe@felipe.com"),
                "Lucas",
                "lucas@lucas.com"
        );

        Trip updatedTrip = tripService.updateTrip(tripId, updatedPayload);

        assertEquals("New Destination", updatedTrip.getDestination());
        verify(tripRepository).save(trip);
    }

    @Test
    @DisplayName("UpdateTrip: Should throw ResourceNotFound when trip is not found")
    public void case06() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> tripService.updateTrip(tripId, validPayload));
    }

    @Test
    @DisplayName("ConfirmTrip: Should confirm trip successfully")
    public void case07() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

        Trip confirmedTrip = tripService.confirmTrip(tripId);

        assertTrue(confirmedTrip.isConfirmed());
        verify(participantService).triggerConfirmationEmailToParticipants(tripId);
        verify(tripRepository).save(trip);
    }

    @Test
    @DisplayName("ConfirmTrip: Should throw ResourceNotFound when trip is not found")
    public void case08() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> tripService.confirmTrip(tripId));
    }

    // Participant

    @Test
    @DisplayName("InviteParticipant: Should register participant and send confirmation email if trip is confirmed")
    public void case09() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(participantService.registerParticipantToEvent(any(String.class), any(Trip.class))).thenReturn(participantResponse);

        trip.setConfirmed(true);

        ParticipantCreateResponse response = tripService.inviteParticipant(tripId, participantPayload);

        assertEquals(participantResponse, response);
        verify(participantService).triggerConfirmationEmailToParticipant(participantPayload.email());
    }

    @Test
    @DisplayName("InviteParticipant: Should register participant without sending confirmation email if trip is not confirmed")
    public void case10() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(participantService.registerParticipantToEvent(any(String.class), any(Trip.class))).thenReturn(participantResponse);

        trip.setConfirmed(false);

        ParticipantCreateResponse response = tripService.inviteParticipant(tripId, participantPayload);

        assertEquals(participantResponse, response);
        verify(participantService, never()).triggerConfirmationEmailToParticipant(participantPayload.email());
    }

    @Test
    @DisplayName("GetAllParticipants: Should return list of participants successfully")
    public void case11() {
        when(participantService.getAllParticipants(tripId)).thenReturn(Collections.singletonList(participantData));

        List<ParticipantData> participants = tripService.getAllParticipants(tripId);

        assertEquals(1, participants.size());
        assertEquals(participantData, participants.get(0));
    }

    // Activity

    @Test
    @DisplayName("RegisterActivity: Should register activity successfully")
    public void case12() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(activityService.registerActivity(any(ActivityRequestPayload.class), any(Trip.class))).thenReturn(activityResponse);

        ActivityResponse response = tripService.registerActivity(tripId, activityPayload);

        assertEquals(activityResponse, response);
    }

    @Test
    @DisplayName("GetAllActivities: Should return list of activities successfully")
    public void case13() {
        when(activityService.getAllActivities(tripId)).thenReturn(activityList);

        List<ActivityDataResponse> activities = tripService.getAllActivities(tripId);

        assertEquals(1, activities.size());
        assertEquals(activityList.get(0), activities.get(0));
    }

    @Test
    @DisplayName("GetAllActivities: Should return empty list if no activities are found")
    public void case14() {
        when(activityService.getAllActivities(tripId)).thenReturn(Arrays.asList());

        List<ActivityDataResponse> activities = tripService.getAllActivities(tripId);

        assertTrue(activities.isEmpty());
    }

    // Links

    @Test
    @DisplayName("RegisterLink: Should register link successfully")
    public void case15() {
        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(linkService.registerLink(any(LinkRequestPayload.class), any(Trip.class))).thenReturn(linkResponse);

        LinkResponse response = tripService.registerLink(tripId, linkPayload);

        assertEquals(linkResponse, response);
    }

    @Test
    @DisplayName("GetAllLinks: Should return list of links successfully")
    public void case16() {
        when(linkService.getAllLinksFromTrip(tripId)).thenReturn(linkList);

        List<LinkData> links = tripService.getAllLinks(tripId);

        assertEquals(1, links.size());
        assertEquals(linkList.get(0), links.get(0));
    }

    @Test
    @DisplayName("GetAllLinks: Should return empty list if no links are found")
    public void case17() {
        when(linkService.getAllLinksFromTrip(tripId)).thenReturn(Arrays.asList());

        List<LinkData> links = tripService.getAllLinks(tripId);

        assertTrue(links.isEmpty());
    }

}