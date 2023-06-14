package com.natlex.test_app.service;

import com.natlex.test_app.model.entity.FileJob;

import java.util.Optional;

public interface IFileJobService {

    public Optional<FileJob> getFileJob(long id);

    public FileJob save(FileJob fileJob);

}
