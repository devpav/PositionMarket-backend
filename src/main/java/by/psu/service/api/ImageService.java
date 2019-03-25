package by.psu.service.api;

import by.psu.exceptions.BadRequestException;
import by.psu.exceptions.EntityNotFoundException;
import by.psu.model.postgres.Image;
import by.psu.model.postgres.repository.RepositoryImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.Action;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Service
public class ImageService implements ServiceCRUD<Image> {

    private final RepositoryImage repositoryImage;

    @Autowired
    public ImageService(RepositoryImage repositoryImage) {
        this.repositoryImage = repositoryImage;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Image> getAll() {
        return repositoryImage.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Image getOne(UUID id) {
        return repositoryImage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image isn't found by id " + id));
    }

    @Override
    @Transactional
    public Image save(Image object) {

        if ( nonNull(object.getId()) ) {
            throw new BadRequestException("Image isn't saved. Id isn't null.");
        }

        return repositoryImage.save(object);
    }

    @Override
    @Transactional
    public Image update(Image object) {

        if ( nonNull(object.getId()) ) {
            throw new BadRequestException("Image isn't updated. Id is null.");
        }

        return repositoryImage.save(object);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repositoryImage.deleteById(id);
    }
}