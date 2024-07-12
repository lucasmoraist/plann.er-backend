package com.lucasmoraist.planner.activity;

import java.time.LocalDate;
import java.util.List;

public record ActivityDataResponse(LocalDate date, List<ActivityData> activities) {
}
