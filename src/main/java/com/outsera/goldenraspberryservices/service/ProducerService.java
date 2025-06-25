package com.outsera.goldenraspberryservices.service;

import com.outsera.goldenraspberryservices.dto.AwardIntervalDTO;
import com.outsera.goldenraspberryservices.dto.AwardIntervalResponseDTO;
import com.outsera.goldenraspberryservices.entity.ProducerEntity;
import com.outsera.goldenraspberryservices.repository.ProducerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProducerService {
    private static final Logger LOG = LoggerFactory.getLogger(ProducerService.class);

    @Inject
    ProducerRepository producerRepository;

    public AwardIntervalResponseDTO getAwardIntervals() {
        List<ProducerEntity> eligibleProducers = producerRepository.findProducersWithMoreThanOneWin();
        LOG.info("Found {} eligible producers with more than one win", eligibleProducers.size());
        List<AwardIntervalDTO> allIntervals = new ArrayList<>();

        for (ProducerEntity producer : eligibleProducers) {
            LOG.info("Processing producer: {}", producer.name);
            List<Integer> winYears = producer.movies.stream()
                    .filter(movie -> "yes".equalsIgnoreCase(movie.winner))
                    .map(movie -> movie.year)
                    .sorted()
                    .toList();

            LOG.info("Win years for producer {}: {}", producer.name, winYears);
            for (int i = 0; i < winYears.size() - 1; i++) {
                LOG.info("Processing win years for producer {}: {} and {}", producer.name, winYears.get(i), winYears.get(i + 1));
                int previousWin = winYears.get(i);
                int followingWin = winYears.get(i + 1);
                int interval = followingWin - previousWin;
                allIntervals.add(new AwardIntervalDTO(producer.name, interval, previousWin, followingWin));
                LOG.info("Added interval for producer {}: {} ({} - {})", producer.name, interval, previousWin, followingWin);
            }
        }

        if (allIntervals.isEmpty()) {
            LOG.info("No intervals found, returning empty response");
            return new AwardIntervalResponseDTO(Collections.emptyList(), Collections.emptyList());
        }

        int minIntervalValue = allIntervals.stream().mapToInt(AwardIntervalDTO::interval).min().getAsInt();
        int maxIntervalValue = allIntervals.stream().mapToInt(AwardIntervalDTO::interval).max().getAsInt();

        LOG.info("Minimum interval value: {}, Maximum interval value: {}", minIntervalValue, maxIntervalValue);
        List<AwardIntervalDTO> minList = allIntervals.stream()
                .filter(dto -> dto.interval() == minIntervalValue)
                .collect(Collectors.toList());

        LOG.info("Minimum intervals found: {}", minList);
        List<AwardIntervalDTO> maxList = allIntervals.stream()
                .filter(dto -> dto.interval() == maxIntervalValue)
                .collect(Collectors.toList());

        LOG.info("Maximum intervals found: {}", maxList);
        return new AwardIntervalResponseDTO(minList, maxList);
    }
}