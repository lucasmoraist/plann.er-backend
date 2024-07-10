package com.lucasmoraist.planner.link;

import com.lucasmoraist.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    @Autowired
    private LinkRepository repository;

    public LinkResponse registerLink(LinkRequestPayload payload, Trip trip){
        Link newLink = Link.builder()
                .title(payload.title())
                .url(payload.url())
                .trip(trip)
                .build();

        this.repository.save(newLink);

        return new LinkResponse(newLink.getId());
    }
    public List<LinkData> getAllLinksFromTrip(UUID tripId){
        return this.repository.findByTripId(tripId)
                .stream()
                .map(link -> new LinkData(link.getId(), link.getTitle(), link.getUrl()))
                .toList();
    }

}
