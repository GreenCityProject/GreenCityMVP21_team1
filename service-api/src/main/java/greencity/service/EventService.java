package greencity.service;

import greencity.dto.event.EventDto;
import greencity.dto.event.EventDtoRequest;
import greencity.dto.event.EventDtoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {

EventDto save (EventDtoRequest eventDtoRequest, MultipartFile image, String email);
}
