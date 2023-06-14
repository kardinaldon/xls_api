package com.natlex.test_app.repository;

import com.natlex.test_app.model.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByGeologicalClassesCode(String id);
}
