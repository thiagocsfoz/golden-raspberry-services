package com.outsera.goldenraspberryservices.dto;

import java.util.List;

public record AwardIntervalResponseDTO(
        List<AwardIntervalDTO> min,
        List<AwardIntervalDTO> max
) {}
