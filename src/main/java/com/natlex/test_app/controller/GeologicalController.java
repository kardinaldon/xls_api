package com.natlex.test_app.controller;

import com.natlex.test_app.model.exception.GeologicalBadRequestException;
import com.natlex.test_app.model.entity.Geological;
import com.natlex.test_app.model.exception.GeologicalNotFoundException;
import com.natlex.test_app.service.IGeologicalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "#{'${values.url.geological.controller}'}")
@Validated
public class GeologicalController {
    private static final Logger LOG = LoggerFactory.getLogger(GeologicalController.class);
    @Autowired
    private IGeologicalService geologicalService;

    @Operation(summary = "Get by id"
            , description ="get geological entity from database by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = { @Content(schema = @Schema(implementation = Geological.class)
                    , mediaType = "application/json") })
            , @ApiResponse (responseCode = "400"
            , description = "id parameter not set"
            , content = @Content)
            , @ApiResponse (responseCode = "404"
            , description = "geological not found"
            , content = @Content)
            , @ApiResponse (responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<Geological> getById(
            @Parameter(name = "id", description = "geological id"
                    , required = true, example = "1")
            @RequestParam(required = true)
            @NonNull
            long id){
        if(id != 0){
            Optional<Geological> byId = geologicalService.getById(id);
            if(byId.isPresent()){
                return ResponseEntity.ok()
                        .body(byId.get());
            }
            else {
                LOG.warn(" Geological by id "+ id + " not found");
                throw new GeologicalNotFoundException();
            }
        }
        else {
            LOG.warn(" id is empty or 0");
            throw new GeologicalBadRequestException(" id cannot be empty or equal to 0");
        }
    }

    @Operation(summary = "Add"
            , description ="adds a new geological entity to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = { @Content(schema = @Schema(implementation = Geological.class)
                    , mediaType = "application/json") })
            , @ApiResponse (responseCode = "400"
            , description = "parameters not set"
            , content = @Content)
            , @ApiResponse (responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
    })
    @PostMapping(consumes = "application/json",  produces = "application/json")
    public ResponseEntity<Geological> newGeological(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Geological to add."
                    , required = true,
            content = @Content(
                    schema=@Schema(implementation = Geological.class)))
            @RequestBody(required = true)
            @NonNull
            Geological geological){
        if(geological.getName() != null && geological.getCode() != null){
            Geological savedGeological = geologicalService.save(geological);
            return ResponseEntity.ok().body(savedGeological);
        }
        else {
            LOG.warn(" parameters name or code are missing in the request");
            throw new GeologicalBadRequestException(" parameters name or code are missing in the request");
        }
    }

    @Operation(summary = "Update"
            , description ="updates the geological entity in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = { @Content(schema = @Schema(implementation = Geological.class)
                    , mediaType = "application/json") })
            , @ApiResponse (responseCode = "400"
            , description = "parameters not set"
            , content = @Content)
            , @ApiResponse (responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
            , @ApiResponse (responseCode = "404"
            , description = "geological not found"
            , content = @Content)
    })
    @PutMapping(consumes = "application/json",  produces = "application/json")
    public ResponseEntity<Geological> updateGeological(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Geological to update"
                    , required = true,
                    content = @Content(
                            schema=@Schema(implementation = Geological.class)))
            @RequestBody(required = true)
            @NonNull
            Geological geological){
        if(geological.getGeologicalId() != 0
                && geological.getName() != null
                && geological.getCode() != null){
            Optional<Geological> byId = geologicalService.getById(geological.getGeologicalId());
            if(byId.isPresent()){
                Geological savedGeological = geologicalService.save(geological);
                return ResponseEntity.ok()
                        .body(savedGeological);
            }
            else {
                LOG.warn(" Geological by id "+ geological.getGeologicalId() + " not found");
                throw new GeologicalNotFoundException();
            }
        }
        else {
            LOG.warn(" parameter name or code is missing in the request, or id = 0");
            throw new GeologicalBadRequestException(" parameter name or code is missing in the request, or id = 0");
        }
    }

    @Operation(summary = "Delete"
            , description ="removes the geological entity from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = { @Content(schema = @Schema(implementation = String.class)
                    , mediaType = "application/json") })
            , @ApiResponse (responseCode = "400"
            , description = "parameters not set"
            , content = @Content)
            , @ApiResponse (responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
            , @ApiResponse (responseCode = "404"
            , description = "geological not found"
            , content = @Content)
    })
    @DeleteMapping(consumes = "application/json",  produces = "application/json")
    public ResponseEntity<String> deleteGeological(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Geological to delete"
                    , required = true,
                    content = @Content(
                            schema=@Schema(implementation = Geological.class))
            )
            @RequestBody(required = true)
            @NonNull
            Geological geological){
        if(geological.getGeologicalId() != 0){
            Optional<Geological> byId = geologicalService.getById(geological.getGeologicalId());
            if(byId.isPresent()){
                geologicalService.delete(geological);
                return ResponseEntity.ok().body("geological object removed");
            }
            else {
                LOG.warn(" Geological by id "+ geological.getGeologicalId() + " not found");
                throw new GeologicalNotFoundException();
            }
        }
        else {
            LOG.warn(" id is empty or 0");
            throw new GeologicalBadRequestException(" id cannot be empty or equal to 0");
        }
    }
}
