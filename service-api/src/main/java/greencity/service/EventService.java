package greencity.service;

import greencity.dto.event.EventDto;
import greencity.dto.event.AddEventDtoRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {
    EventDto save(AddEventDtoRequest addEventDtoRequest, List<MultipartFile> images, Long id);
}
