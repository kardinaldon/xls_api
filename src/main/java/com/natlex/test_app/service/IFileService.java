package com.natlex.test_app.service;

import com.natlex.test_app.model.entity.FileJob;
import org.springframework.core.io.Resource;

import java.net.MalformedURLException;

public interface IFileService {

    void store(byte[] bytes, long fileJobId);

    void exportFromDbToFile(FileJob fileJob);

    Resource getByJobID(FileJob fileJob) throws MalformedURLException;
}
