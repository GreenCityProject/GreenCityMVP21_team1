package greencity.service;

import greencity.client.RestClient;
import greencity.dto.event.CoordinatesDto;
import greencity.dto.event.DatesLocationDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.DateLocation;
import greencity.entity.Event;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.enums.TagType;
import greencity.exception.exceptions.NotSavedException;
import greencity.mapping.TagMapper;
import greencity.mapping.TagUaEnDtoMapper;
import greencity.mapping.TagVOMapper;
import greencity.repository.DatesLocationRepo;
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
    private final DatesLocationRepo datesLocationRepo;
    private final TagUaEnDtoMapper tagUaEnDtoMapper;
    private final TagMapper tagMapper;

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
            List<String> listTags = addEventDtoRequest.getTags();
            List<TagVO> listTagVO = tagsService.findTagsWithAllTranslationsByNamesAndType(listTags, TagType.EVENT);
            List<Tag> tags = tagMapper.mapAllToList(listTagVO);
            newEventToSave.setTags(tags);
            List<TagUaEnDto> tagUaEnDtoList = tagUaEnDtoMapper.mapAllToList(listTagVO);

            List<DatesLocationDto> listDatesLocationsDto = addEventDtoRequest.getDatesLocations();
            List<CoordinatesDto> coordinatesDtoList = listDatesLocationsDto
                    .stream()
                    .map(DatesLocationDto::getCoordinates)
                    .toList();
            List<DateLocation> dateLocationList = coordinatesDtoList.stream()
                    .map(coordinatesDto -> {
                        DateLocation dateLocation = modelMapper.map(listDatesLocationsDto, DateLocation.class);
                        dateLocation.setLongitude(coordinatesDto.getLongitude());
                        dateLocation.setLatitude(coordinatesDto.getLatitude());
                        dateLocation.setStartDate(listDatesLocationsDto.getLast().getStartDate());
                        dateLocation.setFinishDate(listDatesLocationsDto.getLast().getFinishDate());
                        return dateLocation;
                    }).toList();
            newEventToSave.setDateLocation(dateLocationList);
            Event savedEvent = eventRepo.save(newEventToSave);
            dateLocationList
                    .forEach(dateLocation -> dateLocation.setEvent(savedEvent));
            datesLocationRepo.saveAll(dateLocationList);
            log.info("savedEvent: {}", savedEvent);
            EventDto map = modelMapper.map(savedEvent, EventDto.class);
            map.setDatesLocationDtos(listDatesLocationsDto);
            map.setTags(tagUaEnDtoList);
            return map;
        } catch (NotSavedException e) {
            log.error("Event can't be saved. eventDtoRequest: {}", addEventDtoRequest, e);
            throw new NotSavedException("Event can't be saved");
        }
    }
}
