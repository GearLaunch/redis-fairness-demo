package com.redislabs.university.RU102J.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redislabs.university.RU102J.dao.FairnessDao;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("/fairness")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FairnessResource {

    private final FairnessDao fairnessDao;
    private final ObjectMapper objectMapper;
    private final TypeReference<List<FairnessJob>> JOB_LIST_TYPE = new TypeReference<List<FairnessJob>>(){};

    public FairnessResource(FairnessDao fairnessDao) {
        this.fairnessDao = fairnessDao;
        this.objectMapper = new ObjectMapper();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(fairnessDao.list())
            .header("Access-Control-Allow-Origin", "*")
            .build();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(String body) {
        try {
            final List<FairnessJob> jobs = objectMapper.readValue(body, JOB_LIST_TYPE);
            return Response.ok(fairnessDao.add(jobs))
                .header("Access-Control-Allow-Origin", "*")
                .build();
        } catch (IOException e) {
            return Response.status(HttpStatus.BAD_REQUEST_400).build();
        }
    }

    @GET
    @Path("/run")
    public Response run() {
        return Response.ok(fairnessDao.run())
            .header("Access-Control-Allow-Origin", "*")
            .build();
    }

}
