package com.lucasmoraist.planner.trips;

import java.util.List;

public record TripRequestPayload(
        String destination,
        String startAt,
        String endAt,
        List<String> emailsToInvite,
        String ownerName,
        String ownerEmail
) {
}
