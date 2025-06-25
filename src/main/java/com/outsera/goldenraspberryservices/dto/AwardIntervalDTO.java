package com.outsera.goldenraspberryservices.dto;

public record AwardIntervalDTO (
        String producer,
        int interval,
        int previousWin,
        int followingWin
) {}
