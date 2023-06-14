package com.natlex.test_app.controller;

import com.natlex.test_app.model.entity.AppUser;
import com.natlex.test_app.service.IAppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "#{'${values.url.user.controller}'}")
@Validated
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private IAppUserService iAppUserService;


    @Operation(summary = "Get by id"
            , description ="get user entity from database by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = { @Content(schema = @Schema(implementation = AppUser.class)
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
    public ResponseEntity<AppUser> getById(
            @Parameter(name = "id", description = "user id"
                    , required = true, example = "?id=1")
            @RequestParam(required = true)
            @NonNull
            long id){
        if(id != 0){
            Optional<AppUser> byId = iAppUserService.getById(id);
            if(byId.isPresent()){
                return ResponseEntity.ok()
                        .body(byId.get());
            }
            else {
                LOG.warn(" user by id "+ id + " not found");
                return ResponseEntity.notFound().build();
            }
        }
        else {
            LOG.warn(" id = 0 ");
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Add"
            , description ="adds a new user entity to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = { @Content(schema = @Schema(implementation = AppUser.class)
                    , mediaType = "application/json") })
            , @ApiResponse (responseCode = "400"
            , description = "parameters not set"
            , content = @Content)
    })
    @PostMapping(consumes = "application/json",  produces = "application/json")
    public ResponseEntity<AppUser> newUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "AppUser to add."
                    , required = true,
                    content = @Content(
                            schema=@Schema(implementation = AppUser.class)))
            @RequestBody(required = true)
            @NonNull
            AppUser appUser){
        if(appUser.getLogin() != null
                && !appUser.getLogin().isBlank()
                && appUser.getLogin() != null
                && !appUser.getPassword().isBlank()
        ){
            Optional<AppUser> byLogin = iAppUserService.findByLogin(appUser.getLogin());
            if(byLogin.isEmpty()){
                return ResponseEntity.ok().body(iAppUserService.save(appUser));
            }
            else {
                LOG.warn(" user with login " + appUser.getLogin() + " already exists");
                return ResponseEntity.badRequest().build();
            }
        }
        else {
            LOG.warn(" the login or password parameter is missing in the request");
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update"
            , description ="updates the user entity in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = { @Content(schema = @Schema(implementation = AppUser.class)
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
    public ResponseEntity<AppUser> updateAppUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "user to update"
                    , required = true,
                    content = @Content(
                            schema=@Schema(implementation = AppUser.class)))
            @RequestBody(required = true)
            @NonNull
            AppUser appUser){
        if(appUser.getAppUserId() != 0
                && appUser.getLogin() != null
                && !appUser.getLogin().isBlank()
                && appUser.getLogin() != null
                && !appUser.getPassword().isBlank()
        ){
            Optional<AppUser> byId = iAppUserService.getById(appUser.getAppUserId());
            if(byId.isPresent()){
                return ResponseEntity.ok().body(iAppUserService.save(appUser));
            }
            else {
                LOG.warn(" user by id "+ appUser.getAppUserId() + " not found");
                return ResponseEntity.notFound().build();
            }
        }
        else {
            LOG.warn(" login or password or id parameters are missing in the request");
            return ResponseEntity.badRequest().build();
        }
    }


    @Operation(summary = "Delete"
            , description ="removes user entity from the database")
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
            , description = "user not found"
            , content = @Content)
    })
    @DeleteMapping(consumes = "application/json",  produces = "application/json")
    public ResponseEntity<String> deleteUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "user to delete"
                    , required = true,
                    content = @Content(
                            schema=@Schema(implementation = AppUser.class))
            )
            @RequestBody(required = true)
            @NonNull
            AppUser appUser){
        if(appUser.getAppUserId() != 0){
            Optional<AppUser> byId = iAppUserService.getById(appUser.getAppUserId());
            if(byId.isPresent()){
                iAppUserService.deleteUser(appUser);
                return ResponseEntity.ok().body("user removed");
            }
            else {
                LOG.warn(" user by id "+ appUser.getAppUserId() + " not found");
                return ResponseEntity.notFound().build();
            }
        }
        else {
            LOG.warn(" id = 0");
            return ResponseEntity.badRequest().build();
        }
    }
}
