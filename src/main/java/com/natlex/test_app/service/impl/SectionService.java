package com.natlex.test_app.service.impl;

import com.natlex.test_app.model.entity.Section;
import com.natlex.test_app.repository.SectionRepository;
import com.natlex.test_app.service.ISectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SectionService implements ISectionService {

    @Autowired
    private SectionRepository sectionRepository;

    @Override
    public List<Section> getByGeologicalCode(String code) {
        List<Section> byGeologicalClassesCode = sectionRepository.findByGeologicalClassesCode(code);
        return byGeologicalClassesCode != null ? byGeologicalClassesCode : Collections.emptyList();
    }

    @Override
    public Optional<Section> getById(long id) {
        return sectionRepository.findById(id);
    }

    @Override
    public List<Section> getAll(){
        List<Section> sectionList = sectionRepository.findAll();
        return sectionList != null ? sectionList : Collections.emptyList();
    }

    @Override
    public Section save(Section section){
        return sectionRepository.save(section);
    }

    @Override
    public void saveAll(List<Section> sectionList){
        sectionRepository.saveAll(sectionList);
    }

    @Override
    public void delete(Section section){
        sectionRepository.delete(section);
    }
}
