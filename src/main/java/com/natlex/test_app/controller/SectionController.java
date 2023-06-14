package com.natlex.test_app.controller;

import com.natlex.test_app.model.entity.Section;
import com.natlex.test_app.model.exception.SectionBadRequestException;
import com.natlex.test_app.model.exception.SectionNotFoundException;
import com.natlex.test_app.service.ISectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "#{'${values.url.section.controller}'}")
@Validated
public class SectionController {
    private static final Logger LOG = LoggerFactory.getLogger(SectionController.class);
    @Autowired
    private ISectionService sectionService;

    @Operation(summary = "Get by geological code"
            , description ="get list of sections from database by geological code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful",
                    content = {@Content(
                            mediaType = "application/json"
                            , array = @ArraySchema(schema = @Schema(implementation = Section.class))
                    )})
            , @ApiResponse (responseCode = "400"
            , description = "code parameter not set"
            , content = @Content)
            , @ApiResponse (responseCode = "404"
            , description = "sections not found"
            , content = @Content)
            , @ApiResponse (responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
    })
    @GetMapping(path = "#{'${values.url.section.by_code}'}", produces = "application/json")
    public ResponseEntity<List<Section>> getByGeologicalCode(
            @Parameter(name = "code", description = "geological code", required = true)
            @RequestParam(required = true)
            @NonNull
            String code){
        if(!code.isBlank()){
            List<Section> byId = sectionService.getByGeologicalCode(code);
            if(!byId.isEmpty()){
                return ResponseEntity.ok()
                        .body(byId);
            }
            else {
                LOG.warn(" Section by geological code  "+ code + " not found");
                throw new SectionNotFoundException();
            }
        }
        else {
            LOG.warn(" geological code is blank");
            throw new SectionBadRequestException("geological code is blan");
        }
    }

    @Operation(summary = "Get by id"
            , description ="get section entity from database by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful",
                    content = {@Content(
                            mediaType = "application/json"
                            , array = @ArraySchema(schema = @Schema(implementation = Section.class))
                    )})
            , @ApiResponse (responseCode = "400"
            , description = "id parameter not set"
            , content = @Content)
            , @ApiResponse (responseCode = "404"
            , description = "sections not found"
            , content = @Content)
            , @ApiResponse (responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<Section> getById(
            @Parameter(name = "id", description = "section id", required = true)
            @RequestParam(required = true)
            @NonNull
            long id){
        if(id != 0){
            Optional<Section> optionalSection = sectionService.getById(id);
            if(optionalSection.isPresent()){
                return ResponseEntity.ok()
                        .body(optionalSection.get());
            }
            else {
                LOG.warn(" Section by id  "+ id + " not found");
                throw new SectionNotFoundException();
            }
        }
        else {
            LOG.warn(" section id 0 or empty");
            throw new SectionBadRequestException("section id 0 or empty");
        }
    }

    @Operation(summary = "Add"
            , description ="adds a new section entity to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = { @Content(schema = @Schema(implementation = Section.class)
                    , mediaType = "application/json") })
            , @ApiResponse (responseCode = "400"
            , description = "parameters not set"
            , content = @Content)
            , @ApiResponse (responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
    })
    @PostMapping(consumes = "application/json",  produces = "application/json")
    public ResponseEntity<Section> newSection(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Section to add."
                    , required = true,
                    content = @Content(
                            schema=@Schema(implementation = Section.class)))
            @RequestBody(required = true)
            @NonNull
            Section section){
        if(section.getName() != null && !section.getName().isBlank()){
            return ResponseEntity.ok().body(sectionService.save(section));
        }
        else {
            LOG.warn("parameter name is missing in the request");
            throw new SectionBadRequestException("parameter name is missing in the request");
        }
    }

    @Operation(summary = "Update"
            , description ="updates the section entity in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = { @Content(schema = @Schema(implementation = Section.class)
                    , mediaType = "application/json") })
            , @ApiResponse (responseCode = "400"
            , description = "parameters not set"
            , content = @Content)
            , @ApiResponse (responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
            , @ApiResponse (responseCode = "404"
            , description = "section not found"
            , content = @Content)
    })
    @PutMapping(consumes = "application/json",  produces = "application/json")
    public ResponseEntity<Section> updateSection(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Section to update"
                    , required = true,
                    content = @Content(
                            schema=@Schema(implementation = Section.class)))
            @RequestBody(required = true)
            @NonNull
            Section section){
        if(section.getSectionId() != 0 && section.getName() != null && !section.getName().isBlank()){
            Optional<Section> byId = sectionService.getById(section.getSectionId());
            if(byId.isPresent()){
                Section savedSection = sectionService.save(section);
                return ResponseEntity.ok()
                        .body(savedSection);
            }
            else {
                LOG.warn(" Section by id  "+ section.getSectionId() + " not found");
                throw new SectionNotFoundException();
            }
        }
        else {
            LOG.warn("parameter name or id is missing in the request");
            throw new SectionBadRequestException("parameter name or id is missing in the request");
        }
    }

    @Operation(summary = "Delete"
            , description ="removes the section entity from the database")
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
            , description = "section not found"
            , content = @Content)
    })
    @DeleteMapping(consumes = "application/json",  produces = "application/json")
    public ResponseEntity<String> deleteSection(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Section to delete"
                    , required = true,
                    content = @Content(
                            schema=@Schema(implementation = Section.class)))
            @RequestBody(required = true)
            @NonNull
            Section section){
        if(section.getSectionId() != 0){
            Optional<Section> byId = sectionService.getById(section.getSectionId());
            if(byId.isPresent()){
                sectionService.delete(section);
                return ResponseEntity.ok().body("section object removed");
            }
            else {
                LOG.warn(" Section by id  "+ section.getSectionId() + " not found");
                throw new SectionNotFoundException();
            }
        }
        else {
            LOG.warn(" section id 0 or empty");
            throw new SectionBadRequestException("section id 0 or empty");
        }
    }
}
