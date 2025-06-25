package com.outsera.goldenraspberryservices.util;

import com.outsera.goldenraspberryservices.entity.MovieEntity;
import com.outsera.goldenraspberryservices.entity.ProducerEntity;
import com.outsera.goldenraspberryservices.repository.MovieRepository;
import com.outsera.goldenraspberryservices.repository.ProducerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class TestDataSetup {

    @Inject
    ProducerRepository producerRepository;

    @Inject
    MovieRepository movieRepository;

    @Transactional
    public void setupWinningMovie(int year, String title, List<String> producers) {
        Set<ProducerEntity> movieProducers = new HashSet<>();
        for (String name : producers) {
            ProducerEntity producer = producerRepository.findByNameOptional(name.trim())
                    .orElseGet(() -> {
                        ProducerEntity newProducer = new ProducerEntity();
                        newProducer.name = name.trim();
                        return newProducer;
                    });
            movieProducers.add(producer);
        }

        MovieEntity movie = new MovieEntity();
        movie.year = year;
        movie.title = title;
        movie.studios = "Test Studio";
        movie.winner = "yes";
        movie.producers = movieProducers;
        movie.persist();
    }

    @Transactional
    public void cleanup() {
        movieRepository.deleteAll();
        producerRepository.deleteAll();
    }
}