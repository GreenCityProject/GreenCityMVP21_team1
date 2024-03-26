package greencity.service;

import greencity.client.RestClient;
import greencity.dto.event.EventDto;
import greencity.dto.event.AddEventDtoRequest;
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
import java.util.List;

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
    public EventDto save(AddEventDtoRequest addEventDtoRequest, MultipartFile image, String email) {
        try {
            Event newEventToSave = modelMapper.map(addEventDtoRequest, Event.class);
            UserVO userVObyEmail = restClient.findByEmail(email);
            User user = modelMapper.map(userVObyEmail, greencity.entity.User.class);
            newEventToSave.setAuthor(user);
            newEventToSave.setTitle(addEventDtoRequest.getTitle());
            String imageFile = fileService.upload(image);
            newEventToSave.setImage(imageFile);
            newEventToSave.setDescription(addEventDtoRequest.getDescription());
            newEventToSave.setOpen(addEventDtoRequest.getOpen());
            List<TagVO> tagsByNamesAndType =
                tagsService.findTagsByNamesAndType(addEventDtoRequest.getTags(), TagType.EVENT);
            List<Tag> tags = tagsByNamesAndType.stream()
                .map(tagVO -> modelMapper.map(tagVO, Tag.class)).toList();
            newEventToSave.setTags(tags);
            List<DateLocation> dateLocationList = addEventDtoRequest.getDatesLocations()
                .stream()
                .map(datesLocations -> modelMapper.map(datesLocations, DateLocation.class))
                .toList();
            newEventToSave.setDateLocation(dateLocationList);
            Event savedEvent = eventRepo.save(newEventToSave);

            return modelMapper.map(savedEvent, EventDto.class);
        } catch (NotSavedException e) {
            log.error("Event can't be saved. eventDtoRequest: {}", addEventDtoRequest, e);
            throw new NotSavedException("Event can't be saved");
        }
    }
}
