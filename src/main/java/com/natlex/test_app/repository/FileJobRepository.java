package com.natlex.test_app.repository;

import com.natlex.test_app.model.entity.FileJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileJobRepository extends JpaRepository<FileJob, Long> {
}
