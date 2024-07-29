package com.lucasmoraist.planner.service;

import com.lucasmoraist.planner.link.*;
import com.lucasmoraist.planner.trip.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private LinkService linkService;

    private Trip trip;
    private UUID tripId;
    private UUID linkId;
    private String title;
    private String url;
    private Link link;
    private LinkRequestPayload linkPayload;
    private LinkResponse linkResponse;
    private List<Link> links;
    private List<LinkData> linkDataList;

    @BeforeEach
    public void setup() {
        trip = new Trip();
        tripId = UUID.randomUUID();
        trip.setId(tripId);

        linkId = UUID.randomUUID();
        title = "Sample Title";
        url = "https://example.com";

        linkPayload = new LinkRequestPayload(title, url);

        link = Link.builder()
                .title(title)
                .url(url)
                .trip(trip)
                .build();

        link.setId(linkId);

        linkResponse = new LinkResponse(linkId);

        links = List.of(link);

        linkDataList = List.of(new LinkData(linkId, title, url));
    }

    @Test
    @DisplayName("RegisterLink: Should register a link successfully")
    void case01(){
        when(linkRepository.save(any(Link.class))).thenAnswer(invocation -> {
            Link link = invocation.getArgument(0);
            link.setId(linkId);
            return link;
        });

        LinkResponse response = linkService.registerLink(linkPayload, trip);

        assertEquals(linkResponse, response);
        ArgumentCaptor<Link> captor = ArgumentCaptor.forClass(Link.class);
        verify(linkRepository).save(captor.capture());

        Link capturedLink = captor.getValue();
        assertEquals(title, capturedLink.getTitle());
        assertEquals(url, capturedLink.getUrl());
        assertEquals(trip, capturedLink.getTrip());
    }

    @Test
    @DisplayName("GetAllLinksFromTrip: Should return list of links successfully")
    public void case02() {
        when(linkRepository.findByTripId(tripId)).thenReturn(links);

        List<LinkData> returnedLinks = linkService.getAllLinksFromTrip(tripId);

        assertEquals(linkDataList, returnedLinks, "The link data returned is not as expected.");
    }

    @Test
    @DisplayName("GetAllLinksFromTrip: Should return empty list if no links are found")
    public void case03() {
        when(linkRepository.findByTripId(tripId)).thenReturn(List.of());

        List<LinkData> returnedLinks = linkService.getAllLinksFromTrip(tripId);

        assertTrue(returnedLinks.isEmpty(), "Expected empty list when no links are found.");
    }


}
