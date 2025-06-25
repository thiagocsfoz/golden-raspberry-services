package com.outsera.goldenraspberryservices.resource;

import com.outsera.goldenraspberryservices.dto.AwardIntervalResponseDTO;
import com.outsera.goldenraspberryservices.entity.MovieEntity;
import com.outsera.goldenraspberryservices.entity.ProducerEntity;
import com.outsera.goldenraspberryservices.repository.ProducerRepository;
import com.outsera.goldenraspberryservices.service.ProducerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ProducerServiceTest {

    @Mock
    private ProducerRepository producerRepository;

    @InjectMocks
    private ProducerService producerService;

    @Test
    void getAwardIntervals_shouldCalculateIntervalsCorrectly() {
        ProducerEntity producer1 = new ProducerEntity();
        producer1.name = "Producer A";
        MovieEntity movie1A = new MovieEntity();
        movie1A.year = 2000;
        movie1A.winner = "yes";
        MovieEntity movie1B = new MovieEntity();
        movie1B.year = 2001; // Intervalo de 1
        movie1B.winner = "yes";
        producer1.movies = Set.of(movie1A, movie1B);

        ProducerEntity producer2 = new ProducerEntity();
        producer2.name = "Producer B";
        MovieEntity movie2A = new MovieEntity();
        movie2A.year = 2010;
        movie2A.winner = "yes";
        MovieEntity movie2B = new MovieEntity();
        movie2B.year = 2020; // Intervalo de 10
        movie2B.winner = "yes";
        producer2.movies = Set.of(movie2A, movie2B);

        Mockito.when(producerRepository.findProducersWithMoreThanOneWin())
                .thenReturn(List.of(producer1, producer2));

        AwardIntervalResponseDTO result = producerService.getAwardIntervals();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.min().size());
        Assertions.assertEquals("Producer A", result.min().get(0).producer());
        Assertions.assertEquals(1, result.min().get(0).interval());

        Assertions.assertEquals(1, result.max().size());
        Assertions.assertEquals("Producer B", result.max().get(0).producer());
        Assertions.assertEquals(10, result.max().get(0).interval());
    }
}
