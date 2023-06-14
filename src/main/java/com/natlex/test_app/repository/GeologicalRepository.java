package com.natlex.test_app.repository;

import com.natlex.test_app.model.entity.Geological;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeologicalRepository extends JpaRepository<Geological, Long> {
}
