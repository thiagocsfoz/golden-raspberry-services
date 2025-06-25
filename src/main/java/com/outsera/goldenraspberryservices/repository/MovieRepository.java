package com.outsera.goldenraspberryservices.repository;

import com.outsera.goldenraspberryservices.entity.MovieEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class MovieRepository implements PanacheRepository<MovieEntity> {
    public List<MovieEntity> findWinners() {
        return list("LOWER(winner) = 'yes'");
    }
}
