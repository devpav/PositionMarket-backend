package by.psu.service.api;

import by.psu.model.postgres.Attraction;
import by.psu.model.postgres.Product;
import by.psu.model.postgres.repository.RepositoryAttraction;
import by.psu.service.merger.AttractionMerger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttractionService {

    @Autowired
    private RepositoryAttraction repositoryAttraction;

    @Autowired
    private TagService serviceTag;
    @Autowired
    private TypeService serviceType;
    @Autowired
    private ProductService productService;
    @Autowired
    private AttractionMerger attractionMerger;

    public List<Attraction> getAll() {
        return repositoryAttraction.findAll();
    }

    public Attraction getOne(UUID uuid) {
        return repositoryAttraction.findById(uuid).orElse(null);
    }

    @Transactional
    public Attraction update(Attraction attraction) {
        if ( attraction == null ) {
            throw new RuntimeException("Attraction entity is null");
        }
        if ( attraction.getId() == null ) {
            throw new RuntimeException("Attraction id is null");
        }

        return repositoryAttraction.save( attractionMerger.merge(getOne(attraction.getId()), attraction) );
    }

    @Transactional
    public Attraction save(Attraction attraction) {
        attraction.setTypes(attraction.getTypes().stream().map(serviceType::save).collect(Collectors.toList()));
        attraction.setTags(attraction.getTags().stream().map(serviceTag::save).collect(Collectors.toList()));


        List<Product> products = productService.place(attraction.getProducts());

        attraction.getProducts().clear();
        attraction.getProducts().addAll(products);

        return repositoryAttraction.save(attraction);
    }
}
