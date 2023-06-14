package com.natlex.test_app.service;

import com.natlex.test_app.model.entity.Section;

import java.util.List;
import java.util.Optional;

public interface ISectionService {

    List<Section> getByGeologicalCode(String code);

    Optional<Section> getById(long id);

    List<Section> getAll();

    Section save(Section section);

    void saveAll(List<Section> sectionList);

    void delete(Section section);
}
