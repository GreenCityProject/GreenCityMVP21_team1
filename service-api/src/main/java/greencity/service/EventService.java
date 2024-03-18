package greencity.service;

import greencity.dto.event.EventDtoRequest;
import greencity.dto.event.EventDtoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {

EventDtoResponse save (EventDtoRequest eventDtoRequest, MultipartFile image, String email);
}
