package com.natlex.test_app.service;

import com.natlex.test_app.model.entity.AppUser;

import java.util.Optional;

public interface IAppUserService {

    Optional<AppUser> getById(long id);

    AppUser save(AppUser appUser);

    void deleteUser(AppUser appUser);

    Optional<AppUser> findByLogin(String login);

}
