package com.natlex.test_app.service.impl;

import com.natlex.test_app.model.entity.Geological;
import com.natlex.test_app.repository.GeologicalRepository;
import com.natlex.test_app.service.IGeologicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GeologicalService implements IGeologicalService {

    @Autowired
    private GeologicalRepository geologicalRepository;

    @Override
    public Optional<Geological> getById(long id) {
        return geologicalRepository.findById(id);
    }

    @Override
    public Geological save(Geological geological){
        return geologicalRepository.save(geological);
    }

    @Override
    public void delete(Geological geological){
        geologicalRepository.delete(geological);
    }
}
