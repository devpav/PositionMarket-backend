package by.psu.service.impl;

import by.psu.exceptions.EntityNotFoundException;
import by.psu.exceptions.ServerDataBaseException;
import by.psu.model.Tag;
import by.psu.model.TypeAttraction;
import by.psu.repository.TagRepository;
import by.psu.repository.TypeAttractionRepository;
import by.psu.service.TagService;
import by.psu.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TypeAttractionServiceImpl implements TypeService {

    private final TypeAttractionRepository typeAttractionRepository;

    @Autowired
    public TypeAttractionServiceImpl(TypeAttractionRepository typeAttractionRepository) {
        this.typeAttractionRepository = typeAttractionRepository;
    }


    @Override
    @Transactional
    public List<TypeAttraction> saveAll(TypeAttraction[] typeAttractions) {
        for (TypeAttraction t : typeAttractions) {
            try {
                typeAttractionRepository.saveAndFlush(t);
            } catch (Exception ignored) {}
        }
        return findAll();
    }

    @Override
    public List<TypeAttraction> findAll() {
        return typeAttractionRepository.findAll();
    }

    @Override
    public TypeAttraction findById(Long id) {
        return null;
    }

    @Override
    public TypeAttraction save(TypeAttraction obj) {
        return null;
    }

    @Override
    public TypeAttraction update(TypeAttraction obj, Long id) {
        return null;
    }

    @Override
    public void remove(Long id) {

    }
}