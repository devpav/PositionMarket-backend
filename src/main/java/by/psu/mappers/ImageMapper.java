package by.psu.mappers;

import by.psu.model.postgres.Image;
import by.psu.service.dto.ImageDTO;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class)
public interface ImageMapper extends AbstractMapper<Image, ImageDTO> {

    ImageDTO map(Image nsi);

    Image map(ImageDTO nsi);

}
