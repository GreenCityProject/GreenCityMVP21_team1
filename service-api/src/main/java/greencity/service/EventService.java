package greencity.service;

import greencity.dto.event.EventDto;
import greencity.dto.event.AddEventDtoRequest;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    EventDto save(AddEventDtoRequest addEventDtoRequest, MultipartFile image, String email);
}
