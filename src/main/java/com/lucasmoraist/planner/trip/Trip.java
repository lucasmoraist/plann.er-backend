package com.lucasmoraist.planner.trip;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity(name = "trips")
@Table(name = "trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String destination;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "is_confirmed", nullable = false)
    private boolean isConfirmed;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "owner_email", nullable = false)
    private String ownerEmail;

    public Trip(TripRequestPayload payload){
        this.destination = payload.destination();
        this.isConfirmed = false;
        this.ownerName = payload.ownerName();
        this.ownerEmail = payload.ownerEmail();
        this.startAt = LocalDateTime.parse(payload.startAt(), DateTimeFormatter.ISO_DATE_TIME);
        this.endAt = LocalDateTime.parse(payload.endAt(), DateTimeFormatter.ISO_DATE_TIME);
    }
}
