package com.natlex.test_app.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.persistence.*;


@Data
@Entity
@Table(name="app_user")
public class AppUser {

    @JsonProperty("app_user_id")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "user Id", example = "123")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_id")
    private long appUserId;

    @JsonProperty("login")
    @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "login", example = "user")
    @Column(name = "login")
    private String login;

    @JsonProperty("password")
    @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "password", example = "fgdwsmermyyrtu")
    @Column(name = "password")
    private String password;

}
