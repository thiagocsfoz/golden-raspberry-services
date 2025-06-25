package com.outsera.goldenraspberryservices.repository;

import com.outsera.goldenraspberryservices.entity.ProducerEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProducerRepository implements PanacheRepository<ProducerEntity> {
    public Optional<ProducerEntity> findByNameOptional(String name) {
        return find("name", name).firstResultOptional();
    }

    public List<ProducerEntity> findProducersWithMoreThanOneWin() {
        String query = "SELECT p FROM ProducerEntity p JOIN p.movies m WHERE m.winner = 'yes' GROUP BY p.id HAVING COUNT(m.id) > 1";
        return find(query).list();
    }
}
