package greencity.service;

import greencity.client.RestClient;
import greencity.dto.event.EventDtoRequest;
import greencity.dto.event.EventDtoResponse;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.User;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Period;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;

    @Override
    public EventDtoResponse save(EventDtoRequest eventDtoRequest, MultipartFile image, String email) {
        try {
            Event newEventToSave = modelMapper.map(eventDtoRequest, Event.class);
            UserVO userVObyEmail = restClient.findByEmail(email);
            User user = modelMapper.map(userVObyEmail, greencity.entity.User.class);
            newEventToSave.setAuthor(user);
            newEventToSave.setEventTitle(eventDtoRequest.getEventTitle());
            newEventToSave.setStartDate(eventDtoRequest.getStartDate());
            newEventToSave.setFinishDate(eventDtoRequest.getFinishDate());
            newEventToSave.setLocation(eventDtoRequest.getLocation());
            newEventToSave.setImage(image.getName());
            newEventToSave.setDescription(eventDtoRequest.getDescription());
            Period period = Period.between(eventDtoRequest.getStartDate(), eventDtoRequest.getFinishDate());
            int durationDays = period.getDays();
            newEventToSave.setDuration(durationDays);
            Event savedEvent = eventRepo.save(newEventToSave);

            return modelMapper.map(savedEvent, EventDtoResponse.class);
        } catch (NotSavedException e) {
            log.error("Event can't be saved. eventDtoRequest: {}", eventDtoRequest, e);
            throw new NotSavedException("Event can't be saved");
        }

    }
}
