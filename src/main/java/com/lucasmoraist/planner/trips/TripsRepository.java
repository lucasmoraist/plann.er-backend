package com.lucasmoraist.planner.trips;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TripsRepository extends JpaRepository<Trips, UUID> {}
