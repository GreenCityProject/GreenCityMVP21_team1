package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.event.EventDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Add new event.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
        content = @Content(schema = @Schema(implementation = EventDto.class))),
    })
    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventDto> save(
            @RequestPart @Valid AddEventDtoRequest addEventDtoRequest,
            @Parameter(description = "Image of event") @Valid
            @RequestPart(required = false) List<MultipartFile> images,
            @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            eventService.save(addEventDtoRequest, images, userVO.getId()));
    }
}
