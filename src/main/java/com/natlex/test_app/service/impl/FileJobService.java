package com.natlex.test_app.service.impl;

import com.natlex.test_app.model.entity.FileJob;
import com.natlex.test_app.repository.FileJobRepository;
import com.natlex.test_app.service.IFileJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FileJobService implements IFileJobService {

    @Autowired
    private FileJobRepository fileJobRepository;

    @Override
    public Optional<FileJob> getFileJob(long id){
        return fileJobRepository.findById(id);
    }

    @Override
    public FileJob save(FileJob fileJob){
        return fileJobRepository.save(fileJob);
    }

}
