package com.natlex.test_app.service;

import com.natlex.test_app.model.entity.Geological;

import java.util.Optional;

public interface IGeologicalService {

    Optional<Geological> getById(long id);

    Geological save(Geological geological);

    void delete(Geological geological);
}
