package com.outsera.goldenraspberryservices.config;

import com.outsera.goldenraspberryservices.entity.MovieEntity;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.outsera.goldenraspberryservices.entity.ProducerEntity;
import com.outsera.goldenraspberryservices.repository.ProducerRepository;
import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@UnlessBuildProfile("test")
public class CsvDataLoader {

    private static final Logger LOG = LoggerFactory.getLogger(CsvDataLoader.class);
    @ConfigProperty(name = "app.csv.file-name")
    String csvFileName;

    @Inject
    ProducerRepository producerRepository;

    @Transactional
    public void loadData(@Observes StartupEvent ev) {
        LOG.info("Loading movies from CSV file: {}", csvFileName);

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(csvFileName)), StandardCharsets.UTF_8))
                .withSkipLines(1)
                .withCSVParser(new com.opencsv.CSVParserBuilder().withSeparator(';').build())
                .build()) {

            String[] nextLine;
            LOG.info("Started loading movies from CSV file: {}", csvFileName);
            while ((nextLine = reader.readNext()) != null) {
                LOG.info("Processing line: {}", nextLine);
                Set<ProducerEntity> movieProducers = new HashSet<>();
                String[] producerNames = nextLine[3].split(",|\\sand\\s");

                LOG.info("Processing producers: {}", producerNames);
                for (String name : producerNames) {
                    String trimmedName = name.trim();

                    LOG.info("Trimmed producer name: {}", trimmedName);
                    if (!trimmedName.isEmpty()) {
                        ProducerEntity producer = producerRepository.findByNameOptional(trimmedName)
                                .orElseGet(() -> {
                                    ProducerEntity newProducer = new ProducerEntity();
                                    newProducer.name = trimmedName;
                                    return newProducer;
                                });
                        LOG.info("Adding producer: {}", producer.name);
                        movieProducers.add(producer);
                    }
                }

                MovieEntity movie = new MovieEntity();
                movie.year = Integer.parseInt(nextLine[0]);
                movie.title = nextLine[1];
                movie.studios = nextLine[2];
                movie.producers = movieProducers; // Atribui o Set de produtores

                LOG.info("Adding movie: {} ({})", movie.title, movie.year);
                if (nextLine.length > 4 && "yes".equalsIgnoreCase(nextLine[4])) {
                    movie.winner = "yes";
                } else {
                    movie.winner = "no";
                }

                movie.persist();

                LOG.info("Persisted movie: {} ({}) with winner status: {}", movie.title, movie.year, movie.winner);
            }
            LOG.info("Finished loading movies from CSV. Total persisted: {}", MovieEntity.count());

        } catch (IOException | CsvException e) {
            LOG.error("Failed to load movies from CSV file: {}", csvFileName, e);
        }
    }
}