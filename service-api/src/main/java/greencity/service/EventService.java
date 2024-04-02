package greencity.service;

import greencity.dto.event.EventDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.PageableAdvancedDtoOfEventDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {
    EventDto save(AddEventDtoRequest addEventDtoRequest, List<MultipartFile> images, Long id);

    PageableAdvancedDtoOfEventDto getAll (Integer page, Integer size);
}
