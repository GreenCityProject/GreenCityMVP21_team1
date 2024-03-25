package greencity.service;

import greencity.client.RestClient;
import greencity.dto.event.EventDtoRequest;
import greencity.dto.event.EventDtoResponse;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.DateLocation;
import greencity.entity.Event;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.enums.TagType;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final TagsService tagsService;
    private final FileService fileService;

    @Override
    public EventDtoResponse save(EventDtoRequest eventDtoRequest, MultipartFile image, String email) {
        try {
            Event newEventToSave = modelMapper.map(eventDtoRequest, Event.class);
            UserVO userVObyEmail = restClient.findByEmail(email);
            User user = modelMapper.map(userVObyEmail, greencity.entity.User.class);
            newEventToSave.setAuthor(user);
            newEventToSave.setTitle(eventDtoRequest.getTitle());
            String imageFile = fileService.upload(image);
            newEventToSave.setImage(imageFile);
            newEventToSave.setDescription(eventDtoRequest.getDescription());
            newEventToSave.setOpen(eventDtoRequest.getOpen());
            List<TagVO> tagsByNamesAndType = tagsService.findTagsByNamesAndType(eventDtoRequest.getTags(), TagType.EVENT);
            List<Tag> tags = tagsByNamesAndType.stream()
                    .map(tagVO -> modelMapper.map(tagVO, Tag.class)).toList();
            newEventToSave.setTags(tags);
            List<DateLocation> dateLocationList = eventDtoRequest.getDatesLocations()
                    .stream()
                    .map(datesLocations -> modelMapper.map(datesLocations, DateLocation.class))
                    .toList();
            newEventToSave.setDateLocation(dateLocationList);
            Event savedEvent = eventRepo.save(newEventToSave);

            return modelMapper.map(savedEvent, EventDtoResponse.class);
        } catch (NotSavedException e) {
            log.error("Event can't be saved. eventDtoRequest: {}", eventDtoRequest, e);
            throw new NotSavedException("Event can't be saved");
        }

    }
}
