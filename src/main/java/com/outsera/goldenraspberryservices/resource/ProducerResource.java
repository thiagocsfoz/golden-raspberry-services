package com.outsera.goldenraspberryservices.resource;

import com.outsera.goldenraspberryservices.config.CsvDataLoader;
import com.outsera.goldenraspberryservices.dto.AwardIntervalResponseDTO;
import com.outsera.goldenraspberryservices.service.ProducerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/producers")
public class ProducerResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerResource.class);

    @Inject
    ProducerService service;

    @GET
    @Path("/award-intervals")
    @Produces(MediaType.APPLICATION_JSON)
    public AwardIntervalResponseDTO getAwardIntervals() {
        LOG.info("getAwardIntervals called");
        return service.getAwardIntervals();
    }
}
