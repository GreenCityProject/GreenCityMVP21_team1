package greencity.service;


import greencity.dto.PageableAdvancedDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.shoppinglistitem.*;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.entity.*;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.ShoppingListItemStatus;
import greencity.exception.exceptions.*;
import greencity.filters.ShoppingListItemSpecification;
import greencity.mapping.ShoppingListItemDtoMapper;
import greencity.mapping.UserShoppingListItemResponseDtoMapper;
import greencity.repository.HabitAssignRepo;
import greencity.repository.ShoppingListItemRepo;
import greencity.repository.ShoppingListItemTranslationRepo;
import greencity.repository.UserShoppingListItemRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingListItemServiceImplTest {
    
    @Mock
    ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;

    @Mock
    ShoppingListItemRepo shoppingListItemRepo;

    @Mock
    private HabitAssignRepo habitAssignRepo;

    @Mock
    private UserShoppingListItemRepo userShoppingListItemRepo;

    @InjectMocks
    private ShoppingListItemServiceImpl shoppingListItemService;

    private ModelMapper modelMapper;

    @BeforeEach
    public void initModelMapper() {
        modelMapper = new ModelMapper();
        modelMapper
                .getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        modelMapper.addConverter(new ShoppingListItemDtoMapper());
        modelMapper.addConverter(new UserShoppingListItemResponseDtoMapper());
        Field field;
        try {
            field = ShoppingListItemServiceImpl.class.getDeclaredField("modelMapper");
            field.setAccessible(true);
            field.set(shoppingListItemService, modelMapper);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findAllTest() {
        var SLITranslationFirst = new ShoppingListItemTranslation();
        SLITranslationFirst.setContent("Swedish cellulose dish cloths");
        SLITranslationFirst.setShoppingListItem(new ShoppingListItem(1L, List.of(new UserShoppingListItem()), Set.of(new Habit()), List.of(new ShoppingListItemTranslation())));

        var SLITranslationSecond = new ShoppingListItemTranslation();
        SLITranslationSecond.setContent("Wowables reusable & biodegradable paper towel");
        SLITranslationSecond.setShoppingListItem(new ShoppingListItem(2L, List.of(new UserShoppingListItem()), Set.of(new Habit()), List.of(new ShoppingListItemTranslation())));

        var expected = List.of(new ShoppingListItemDto(1L, "Swedish cellulose dish cloths", "ACTIVE"), new ShoppingListItemDto(2L, "Wowables reusable & biodegradable paper towel", "ACTIVE"));
        List<ShoppingListItemTranslation> shoppingListItemTranslationList = List.of(SLITranslationFirst, SLITranslationSecond);

        Mockito.when(shoppingListItemTranslationRepo.findAllByLanguageCode(Mockito.any(String.class))).thenReturn(shoppingListItemTranslationList);

        List<ShoppingListItemDto> actual = shoppingListItemService.findAll("en");

        assertEquals(expected, actual);
    }

    @Test
    void saveShoppingListItemTest() {
        var LanguageTranslationDtoList = List.of(new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "Swedish cellulose dish cloths"), new LanguageTranslationDTO(new LanguageDTO(1L, "ua"), "Серветки целюлозні"));
        var sLIPostDto = new ShoppingListItemPostDto(LanguageTranslationDtoList, new ShoppingListItemRequestDto());
        var toSaveSLI = modelMapper.map(sLIPostDto, ShoppingListItem.class);
        toSaveSLI.getTranslations().forEach(a -> a.setShoppingListItem(toSaveSLI));
        List<LanguageTranslationDTO> expected = modelMapper.map(toSaveSLI.getTranslations(),
                new TypeToken<List<LanguageTranslationDTO>>() {
                }.getType());
        List<LanguageTranslationDTO> actual = shoppingListItemService.saveShoppingListItem(sLIPostDto);

        Mockito.verify(shoppingListItemRepo, Mockito.times(1)).save(toSaveSLI);
        assertEquals(expected, actual);
    }

    @Test
    void update_NotFoundEntity_ThrowException() {
        var toUpdateLanguageTranslationDtoList = List.of(new LanguageTranslationDTO(new LanguageDTO(2L, "en"), "Swedish cellulose dish cloths"), new LanguageTranslationDTO(new LanguageDTO(1L, "ua"), "Серветки целюлозні"));
        var toUpdateSLIPostDto = new ShoppingListItemPostDto(toUpdateLanguageTranslationDtoList, new ShoppingListItemRequestDto());

        Mockito.when(shoppingListItemRepo.findById(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(ShoppingListItemNotFoundException.class, () -> shoppingListItemService.update(toUpdateSLIPostDto));
    }

    @Test
    void updateTest() {
        var toUpdateLanguageTranslationDtoList = List.of(new LanguageTranslationDTO(new LanguageDTO(2L, "en"), "Swedish cellulose dish cloths"), new LanguageTranslationDTO(new LanguageDTO(1L, "ua"), "Серветки целюлозні"));
        var toUpdateSLIPostDto = new ShoppingListItemPostDto(toUpdateLanguageTranslationDtoList, new ShoppingListItemRequestDto());
        var expectedSLI = modelMapper.map(toUpdateSLIPostDto, ShoppingListItem.class);
        var LanguageTranslationDtoList = List.of(new LanguageTranslationDTO(new LanguageDTO(2L, "en"), "Deprecated swedish cellulose dish cloths"), new LanguageTranslationDTO(new LanguageDTO(1L, "ua"), "Deprecated Серветки целюлозні"));
        var originalSLIPostDto = new ShoppingListItemPostDto(LanguageTranslationDtoList, new ShoppingListItemRequestDto());
        var originalSLI = modelMapper.map(originalSLIPostDto, ShoppingListItem.class);

        Mockito.when(shoppingListItemRepo.findById(toUpdateSLIPostDto.getShoppingListItem().getId())).thenReturn(Optional.of(originalSLI));

        List<LanguageTranslationDTO> expected = modelMapper.map(expectedSLI.getTranslations(),
                new TypeToken<List<LanguageTranslationDTO>>() {
                }.getType());
        List<LanguageTranslationDTO> actual = shoppingListItemService.update(toUpdateSLIPostDto);

        Mockito.verify(shoppingListItemRepo, Mockito.times(1)).save(expectedSLI);

        assertEquals(expected, actual);
    }

    @Test
    void findShoppingListItemById_NotFoundEntity_ThrowException() {
        Mockito.when(shoppingListItemRepo.findById(Mockito.any())).thenReturn(Optional.empty());
        assertThrows(ShoppingListItemNotFoundException.class, () -> shoppingListItemService.findShoppingListItemById(Mockito.any()));
    }

    @Test
    void findShoppingListItemByIdTest() {
        var toUpdateLanguageTranslationDtoList = List.of(new LanguageTranslationDTO(new LanguageDTO(2L, "en"), "Swedish cellulose dish cloths"), new LanguageTranslationDTO(new LanguageDTO(1L, "ua"), "Серветки целюлозні"));
        var toUpdateSLIPostDto = new ShoppingListItemPostDto(toUpdateLanguageTranslationDtoList, new ShoppingListItemRequestDto());
        var expectedSLI = modelMapper.map(toUpdateSLIPostDto, ShoppingListItem.class);
        var expected = modelMapper.map(expectedSLI, ShoppingListItemResponseDto.class);

        Mockito.when(shoppingListItemRepo.findById(Mockito.any())).thenReturn(Optional.of(expectedSLI));
        var actual = shoppingListItemService.findShoppingListItemById(0L);

        assertEquals(expected, actual);
    }

    @Test
    void delete_NotFoundEntity_ThrowException() {
        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(shoppingListItemRepo).deleteById(0L);
        assertThrows(NotDeletedException.class, () -> shoppingListItemService.delete(0L));
        Mockito.verify(shoppingListItemRepo, Mockito.times(1)).deleteById(0L);
    }

    @Test
    void deleteTest() {
        doNothing().when(shoppingListItemRepo).deleteById(0L);
        shoppingListItemService.delete(0L);
        Mockito.verify(shoppingListItemRepo, Mockito.times(1)).deleteById(0L);
    }

    @Test
    void findShoppingListItemsForManagementByPageTest() {
        Pageable pageable = Mockito.mock(Pageable.class);
        var firstExampleLanguageTranslationDtoList = List.of(new LanguageTranslationDTO(new LanguageDTO(2L, "en"), "Swedish cellulose dish cloths"), new LanguageTranslationDTO(new LanguageDTO(1L, "ua"), "Серветки целюлозні"));
        var firstExampleSLIPostDto = new ShoppingListItemPostDto(firstExampleLanguageTranslationDtoList, new ShoppingListItemRequestDto());
        var firstExampleSLI = modelMapper.map(firstExampleSLIPostDto, ShoppingListItem.class);
        var secondExampleLanguageTranslationDtoList = List.of(new LanguageTranslationDTO(new LanguageDTO(2L, "en"), "Deprecated swedish cellulose dish cloths"), new LanguageTranslationDTO(new LanguageDTO(1L, "ua"), "Deprecated Серветки целюлозні"));
        var secondExampleSLIPostDto = new ShoppingListItemPostDto(secondExampleLanguageTranslationDtoList, new ShoppingListItemRequestDto());
        var secondExampleSLI = modelMapper.map(secondExampleSLIPostDto, ShoppingListItem.class);
        List<ShoppingListItem> mockItems = List.of(firstExampleSLI, secondExampleSLI);
        Page<ShoppingListItem> mockPage = new PageImpl<>(mockItems, pageable, mockItems.size());

        Mockito.when(shoppingListItemRepo.findAll(pageable)).thenReturn(mockPage);

        List<ShoppingListItemManagementDto> expectedDtos = mockItems.stream()
                .map(item -> modelMapper.map(item, ShoppingListItemManagementDto.class))
                .collect(Collectors.toList());
        PageableAdvancedDto<ShoppingListItemManagementDto> expectedDtoPage =
                new PageableAdvancedDto<>(expectedDtos, mockPage.getTotalElements(), mockPage.getPageable().getPageNumber(),
                        mockPage.getTotalPages(), mockPage.getNumber(), mockPage.hasPrevious(),
                        mockPage.hasNext(), mockPage.isFirst(), mockPage.isLast());
        PageableAdvancedDto<ShoppingListItemManagementDto> result =
                shoppingListItemService.findShoppingListItemsForManagementByPage(pageable);

        assertEquals(expectedDtoPage, result);
    }

    @Test
    void deleteAllShoppingListItemsByListOfIdTest() {
        List<Long> listId = List.of(1L, 2L, 3L);
        List<Long> result = shoppingListItemService.deleteAllShoppingListItemsByListOfId(listId);
        assertEquals(listId, result);
        Mockito.verify(shoppingListItemRepo, times(listId.size())).deleteById(Mockito.anyLong());
    }

    @Test
    void searchByTest() {
        Pageable pageable = Mockito.mock(Pageable.class);
        String query = "cellulose";
        var firstExampleLanguageTranslationDtoList = List.of(new LanguageTranslationDTO(new LanguageDTO(2L, "en"), "Swedish cellulose dish cloths"), new LanguageTranslationDTO(new LanguageDTO(1L, "ua"), "Серветки целюлозні"));
        var firstExampleSLIPostDto = new ShoppingListItemPostDto(firstExampleLanguageTranslationDtoList, new ShoppingListItemRequestDto());
        var firstExampleSLI = modelMapper.map(firstExampleSLIPostDto, ShoppingListItem.class);
        var secondExampleLanguageTranslationDtoList = List.of(new LanguageTranslationDTO(new LanguageDTO(2L, "en"), "Deprecated swedish cellulose dish cloths"), new LanguageTranslationDTO(new LanguageDTO(1L, "ua"), "Deprecated Серветки целюлозні"));
        var secondExampleSLIPostDto = new ShoppingListItemPostDto(secondExampleLanguageTranslationDtoList, new ShoppingListItemRequestDto());
        var secondExampleSLI = modelMapper.map(secondExampleSLIPostDto, ShoppingListItem.class);
        List<ShoppingListItem> mockItems = List.of(firstExampleSLI, secondExampleSLI);
        Page<ShoppingListItem> mockPage = new PageImpl<>(mockItems, pageable, mockItems.size());

        when(shoppingListItemRepo.searchBy(pageable, query)).thenReturn(mockPage);

        List<ShoppingListItemManagementDto> expectedDtos = mockItems.stream()
                .map(item -> modelMapper.map(item, ShoppingListItemManagementDto.class))
                .collect(Collectors.toList());
        PageableAdvancedDto<ShoppingListItemManagementDto> expectedDtoPage =
                new PageableAdvancedDto<>(expectedDtos, mockPage.getTotalElements(), mockPage.getPageable().getPageNumber(),
                        mockPage.getTotalPages(), mockPage.getNumber(), mockPage.hasPrevious(),
                        mockPage.hasNext(), mockPage.isFirst(), mockPage.isLast());
        PageableAdvancedDto<ShoppingListItemManagementDto> result =
                shoppingListItemService.searchBy(pageable, query);

        assertEquals(expectedDtoPage, result);
    }


    @Test
    void getFilteredDataForManagementByPageTest() {
        Pageable pageable = Mockito.mock(Pageable.class);
        ShoppingListItemViewDto mockDto = new ShoppingListItemViewDto();
        var firstExampleLanguageTranslationDtoList = List.of(new LanguageTranslationDTO(new LanguageDTO(2L, "en"), "Swedish cellulose dish cloths"), new LanguageTranslationDTO(new LanguageDTO(1L, "ua"), "Серветки целюлозні"));
        var firstExampleSLIPostDto = new ShoppingListItemPostDto(firstExampleLanguageTranslationDtoList, new ShoppingListItemRequestDto());
        var firstExampleSLI = modelMapper.map(firstExampleSLIPostDto, ShoppingListItem.class);
        var secondExampleLanguageTranslationDtoList = List.of(new LanguageTranslationDTO(new LanguageDTO(2L, "en"), "Deprecated swedish cellulose dish cloths"), new LanguageTranslationDTO(new LanguageDTO(1L, "ua"), "Deprecated Серветки целюлозні"));
        var secondExampleSLIPostDto = new ShoppingListItemPostDto(secondExampleLanguageTranslationDtoList, new ShoppingListItemRequestDto());
        var secondExampleSLI = modelMapper.map(secondExampleSLIPostDto, ShoppingListItem.class);
        List<ShoppingListItem> mockItems = List.of(firstExampleSLI, secondExampleSLI);
        Page<ShoppingListItem> mockPage = new PageImpl<>(mockItems, pageable, mockItems.size());

        when(shoppingListItemRepo.findAll(Mockito.any(ShoppingListItemSpecification.class), Mockito.eq(pageable)))
                .thenReturn(mockPage);

        List<ShoppingListItemManagementDto> expectedDtos = mockItems.stream()
                .map(item -> modelMapper.map(item, ShoppingListItemManagementDto.class))
                .collect(Collectors.toList());
        PageableAdvancedDto<ShoppingListItemManagementDto> expectedDtoPage =
                new PageableAdvancedDto<>(expectedDtos, mockPage.getTotalElements(), mockPage.getPageable().getPageNumber(),
                        mockPage.getTotalPages(), mockPage.getNumber(), mockPage.hasPrevious(),
                        mockPage.hasNext(), mockPage.isFirst(), mockPage.isLast());
        PageableAdvancedDto<ShoppingListItemManagementDto> result =
                shoppingListItemService.getFilteredDataForManagementByPage(pageable, mockDto);

        assertEquals(expectedDtoPage, result);
    }

    @Test
    void saveUserShoppingListItemsTest() {
        Habit habit = new Habit();
        var SLITranslationFirst = new ShoppingListItemTranslation();
        SLITranslationFirst.setContent("Swedish cellulose dish cloths");
        ShoppingListItem shoppingListItemFirst = new ShoppingListItem(1L, List.of(new UserShoppingListItem()), Set.of(habit), List.of(new ShoppingListItemTranslation()));
        SLITranslationFirst.setShoppingListItem(shoppingListItemFirst);
        List<ShoppingListItemTranslation> shoppingListItemTranslationList = List.of(SLITranslationFirst);
        Long userId = 1L;
        Long habitId = 2L;
        List<ShoppingListItemRequestDto> dtoList = List.of(new ShoppingListItemRequestDto(1L));
        String language = "en";
        Long existedHabitAssignId = 1L;
        User user = new User();
        user.setId(userId);
        habit.setId(habitId);
        habit.setUserId(userId);
        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setUser(user);
        habitAssign.setHabit(habit);
        habitAssign.setId(existedHabitAssignId);
        habit.setHabitAssigns(List.of(habitAssign));
        UserShoppingListItem userShoppingListItem = new UserShoppingListItem();
        List<UserShoppingListItem> userShoppingListItemList = new ArrayList<>();
        userShoppingListItemList.add(userShoppingListItem);
        userShoppingListItem.setId(1L);
        userShoppingListItem.setHabitAssign(habitAssign);
        userShoppingListItem.setShoppingListItem(new ShoppingListItem(1L, List.of(userShoppingListItem), Set.of(habit), shoppingListItemTranslationList));
        habit.setShoppingListItems(Set.of(shoppingListItemFirst));
        habitAssign.setUserShoppingListItems(userShoppingListItemList);

        when(userShoppingListItemRepo.getShoppingListItemsIdForHabit(Mockito.anyLong())).thenReturn(List.of(1L));
        when(habitAssignRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(Optional.of(habitAssign));
        when(userShoppingListItemRepo.saveAll(Mockito.any())).thenReturn(List.of(userShoppingListItem));

        shoppingListItemService.saveUserShoppingListItems(userId, habitId, dtoList, language);

        verify(habitAssignRepo, Mockito.times(2)).findByHabitIdAndUserId(habitId, userId);
        verify(userShoppingListItemRepo, Mockito.times(dtoList.size())).saveAll(userShoppingListItemList);
    }

    @Test
    void saveUserShoppingListItems_ThrowUserHasNoShoppingListItemsException() {
        Long userId = 1L;
        Long habitId = 2L;
        List<ShoppingListItemRequestDto> dtoList = List.of(new ShoppingListItemRequestDto());
        String language = "en";

        when(habitAssignRepo.findByHabitIdAndUserId(Mockito.any(), Mockito.any())).thenReturn(Optional.empty());

        assertThrows(UserHasNoShoppingListItemsException.class,
                () -> shoppingListItemService.saveUserShoppingListItems(userId, habitId, dtoList, language));
    }

    @Test
    void saveUserShoppingListItems_ThrowShoppingListItemNotAssignedForThisHabitException() {
        Habit habit = new Habit();
        var SLITranslationFirst = new ShoppingListItemTranslation();
        SLITranslationFirst.setContent("Swedish cellulose dish cloths");
        ShoppingListItem shoppingListItemFirst = new ShoppingListItem(1L, List.of(new UserShoppingListItem()), Set.of(habit), List.of(new ShoppingListItemTranslation()));
        SLITranslationFirst.setShoppingListItem(shoppingListItemFirst);
        List<ShoppingListItemTranslation> shoppingListItemTranslationList = List.of(SLITranslationFirst);
        Long userId = 1L;
        Long habitId = 2L;
        List<ShoppingListItemRequestDto> dtoList = List.of(new ShoppingListItemRequestDto(1L));
        String language = "en";
        Long existedHabitAssignId = 1L;
        User user = new User();
        user.setId(userId);
        habit.setId(habitId);
        habit.setUserId(userId);
        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setUser(user);
        habitAssign.setHabit(habit);
        habitAssign.setId(existedHabitAssignId);
        habit.setHabitAssigns(List.of(habitAssign));
        UserShoppingListItem userShoppingListItem = new UserShoppingListItem();
        List<UserShoppingListItem> userShoppingListItemList = new ArrayList<>();
        userShoppingListItemList.add(userShoppingListItem);
        userShoppingListItem.setId(1L);
        userShoppingListItem.setHabitAssign(habitAssign);
        userShoppingListItem.setShoppingListItem(new ShoppingListItem(1L, List.of(userShoppingListItem), Set.of(habit), shoppingListItemTranslationList));
        habit.setShoppingListItems(Set.of(shoppingListItemFirst));
        habitAssign.setUserShoppingListItems(userShoppingListItemList);

        when(userShoppingListItemRepo.getShoppingListItemsIdForHabit(Mockito.anyLong())).thenReturn(List.of(0L));
        when(habitAssignRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(Optional.of(habitAssign));

        assertThrows(NotFoundException.class,
                () -> shoppingListItemService.saveUserShoppingListItems(userId, habitId, dtoList, language));
    }


    @Test
    void saveUserShoppingListItems_ThrowShoppingListItemAlreadySelectedException() {
        Habit habit = new Habit();
        var SLITranslationFirst = new ShoppingListItemTranslation();
        SLITranslationFirst.setContent("Swedish cellulose dish cloths");
        ShoppingListItem shoppingListItemFirst = new ShoppingListItem(1L, List.of(new UserShoppingListItem()), Set.of(habit), List.of(new ShoppingListItemTranslation()));
        SLITranslationFirst.setShoppingListItem(shoppingListItemFirst);
        List<ShoppingListItemTranslation> shoppingListItemTranslationList = List.of(SLITranslationFirst);
        Long userId = 1L;
        Long habitId = 2L;
        List<ShoppingListItemRequestDto> dtoList = List.of(new ShoppingListItemRequestDto(1L));
        String language = "en";
        Long existedHabitAssignId = 1L;
        User user = new User();
        user.setId(userId);
        habit.setId(habitId);
        habit.setUserId(userId);
        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setUser(user);
        habitAssign.setHabit(habit);
        habitAssign.setId(existedHabitAssignId);
        habit.setHabitAssigns(List.of(habitAssign));
        UserShoppingListItem userShoppingListItem = new UserShoppingListItem();
        List<UserShoppingListItem> userShoppingListItemList = new ArrayList<>();
        userShoppingListItemList.add(userShoppingListItem);
        userShoppingListItem.setId(1L);
        userShoppingListItem.setHabitAssign(habitAssign);
        userShoppingListItem.setShoppingListItem(new ShoppingListItem(1L, List.of(userShoppingListItem), Set.of(habit), shoppingListItemTranslationList));
        habit.setShoppingListItems(Set.of(shoppingListItemFirst));
        habitAssign.setUserShoppingListItems(userShoppingListItemList);

        when(userShoppingListItemRepo.getShoppingListItemsIdForHabit(Mockito.anyLong())).thenReturn(List.of(1L));
        when(habitAssignRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(Optional.of(habitAssign));

        when(userShoppingListItemRepo.getAllAssignedShoppingListItems(Mockito.anyLong())).thenReturn(List.of(1L));

        assertThrows(WrongIdException.class,
                () -> shoppingListItemService.saveUserShoppingListItems(userId, habitId, dtoList, language));
    }

    @Test
    void getUserShoppingList_NoHabitAssign_ReturnsEmptyList() {
        Long userId = 1L;
        Long habitId = 2L;
        String language = "en";

        when(habitAssignRepo.findByHabitIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        List<UserShoppingListItemResponseDto> result = shoppingListItemService.getUserShoppingList(userId, habitId, language);

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void getUserShoppingListByHabitAssignId_WhenHabitAssignNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        Long habitAssignId = 2L;
        String language = "en";

        when(habitAssignRepo.findById(habitAssignId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> shoppingListItemService.getUserShoppingListByHabitAssignId(userId, habitAssignId, language));
    }

    @Test
    void getUserShoppingListByHabitAssignId_WhenUserHasNoPermission_ShouldThrowUserHasNoPermissionException() {
        Long userId = 1L;
        Long habitAssignId = 2L;
        String language = "en";
        User user = new User();
        user.setId(3L);
        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setUser(user);

        when(habitAssignRepo.findById(habitAssignId)).thenReturn(Optional.of(habitAssign));

        assertThrows(UserHasNoPermissionToAccessException.class,
                () -> shoppingListItemService.getUserShoppingListByHabitAssignId(userId, habitAssignId, language));
    }

    @Test
    void getUserShoppingListByHabitAssignIdTest() {
        Long userId = 1L;
        Long habitAssignId = 1L;
        String language = "en";
        Habit habit = new Habit();
        var SLITranslationFirst = new ShoppingListItemTranslation();
        SLITranslationFirst.setContent("Swedish cellulose dish cloths");
        ShoppingListItem shoppingListItemFirst = new ShoppingListItem(1L, List.of(new UserShoppingListItem()), Set.of(habit), List.of(new ShoppingListItemTranslation()));
        SLITranslationFirst.setShoppingListItem(shoppingListItemFirst);
        List<ShoppingListItemTranslation> shoppingListItemTranslationList = List.of(SLITranslationFirst);
        Long habitId = 2L;
        Long existedHabitAssignId = 1L;
        User user = new User();
        user.setId(userId);
        habit.setId(habitId);
        habit.setUserId(userId);
        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setUser(user);
        habitAssign.setHabit(habit);
        habitAssign.setId(existedHabitAssignId);
        habit.setHabitAssigns(List.of(habitAssign));
        UserShoppingListItem userShoppingListItem = new UserShoppingListItem();
        List<UserShoppingListItem> userShoppingListItemList = new ArrayList<>();
        userShoppingListItemList.add(userShoppingListItem);
        userShoppingListItem.setId(1L);
        userShoppingListItem.setHabitAssign(habitAssign);
        userShoppingListItem.setShoppingListItem(new ShoppingListItem(1L, List.of(userShoppingListItem), Set.of(habit), shoppingListItemTranslationList));
        habit.setShoppingListItems(Set.of(shoppingListItemFirst));
        habitAssign.setUserShoppingListItems(userShoppingListItemList);
        List<UserShoppingListItemResponseDto> mockItemDtos = List.of(new UserShoppingListItemResponseDto(1L, "Swedish cellulose dish cloths", ShoppingListItemStatus.ACTIVE));

        when(habitAssignRepo.findById(habitAssignId)).thenReturn(Optional.of(habitAssign));
        when(userShoppingListItemRepo.findAllByHabitAssingId(habitAssignId)).thenReturn(userShoppingListItemList);
        for (UserShoppingListItemResponseDto mockDto : mockItemDtos) {
            when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId(language, mockDto.getId()))
                    .thenReturn(SLITranslationFirst);
        }

        List<UserShoppingListItemResponseDto> result = shoppingListItemService.getUserShoppingListByHabitAssignId(userId, habitAssignId, language);

        assertEquals(mockItemDtos, result);
    }

    @Test
    void getUserShoppingListItemsByHabitAssignIdAndStatusInProgressTest() {
        Long userId = 1L;
        Long habitAssignId = 1L;
        String language = "en";
        Habit habit = new Habit();
        var SLITranslationFirst = new ShoppingListItemTranslation();
        SLITranslationFirst.setContent("Swedish cellulose dish cloths");
        ShoppingListItem shoppingListItemFirst = new ShoppingListItem(1L, List.of(new UserShoppingListItem()), Set.of(habit), List.of(new ShoppingListItemTranslation()));
        SLITranslationFirst.setShoppingListItem(shoppingListItemFirst);
        List<ShoppingListItemTranslation> shoppingListItemTranslationList = List.of(SLITranslationFirst);
        Long habitId = 2L;
        Long existedHabitAssignId = 1L;
        User user = new User();
        user.setId(userId);
        habit.setId(habitId);
        habit.setUserId(userId);
        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setUser(user);
        habitAssign.setHabit(habit);
        habitAssign.setId(existedHabitAssignId);
        habit.setHabitAssigns(List.of(habitAssign));
        UserShoppingListItem userShoppingListItem = new UserShoppingListItem();
        List<UserShoppingListItem> userShoppingListItemList = new ArrayList<>();
        userShoppingListItemList.add(userShoppingListItem);
        userShoppingListItem.setId(1L);
        userShoppingListItem.setHabitAssign(habitAssign);
        userShoppingListItem.setStatus(ShoppingListItemStatus.INPROGRESS);
        userShoppingListItem.setShoppingListItem(new ShoppingListItem(1L, List.of(userShoppingListItem), Set.of(habit), shoppingListItemTranslationList));
        habit.setShoppingListItems(Set.of(shoppingListItemFirst));
        habitAssign.setUserShoppingListItems(userShoppingListItemList);
        List<UserShoppingListItemResponseDto> expected = List.of(new UserShoppingListItemResponseDto(1L, "Swedish cellulose dish cloths", ShoppingListItemStatus.INPROGRESS));
        when(userShoppingListItemRepo.findUserShoppingListItemsByHabitAssignIdAndStatusInProgress(habitAssignId)).thenReturn(userShoppingListItemList);
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId(language, 1L)).thenReturn(SLITranslationFirst);
        List<UserShoppingListItemResponseDto> result = shoppingListItemService.getUserShoppingListItemsByHabitAssignIdAndStatusInProgress(habitAssignId, language);

        assertEquals(expected, result);
    }

    @Test
    void deleteUserShoppingListItemByItemIdAndUserIdAndHabitIdTest() {
        Long itemId = 1L;
        Long userId = 2L;
        Long habitId = 3L;
        Long habitAssignId = 4L;
        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setId(habitAssignId);

        when(habitAssignRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(Optional.of(habitAssign));
        doNothing().when(userShoppingListItemRepo).deleteByShoppingListItemIdAndHabitAssignId(itemId, habitAssignId);

        shoppingListItemService.deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(itemId, userId, habitId);

        verify(habitAssignRepo).findByHabitIdAndUserId(habitId, userId);
        verify(userShoppingListItemRepo).deleteByShoppingListItemIdAndHabitAssignId(itemId, habitAssignId);
    }

    @Test
    void deleteUserShoppingListItemByItemIdAndUserIdAndHabitId_NotFoundTest() {
        Long itemId = 1L;
        Long userId = 2L;
        Long habitId = 3L;

        when(habitAssignRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> shoppingListItemService.deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(itemId, userId, habitId));
    }

    @Test
    void updateUserShoppingListItemStatusTest() {
        Long userId = 1L;
        Long itemId = 2L;
        String language = "en";
        Habit habit = new Habit();
        var SLITranslationFirst = new ShoppingListItemTranslation();
        SLITranslationFirst.setContent("Swedish cellulose dish cloths");
        ShoppingListItem shoppingListItemFirst = new ShoppingListItem(1L, List.of(new UserShoppingListItem()), Set.of(habit), List.of(new ShoppingListItemTranslation()));
        SLITranslationFirst.setShoppingListItem(shoppingListItemFirst);
        UserShoppingListItem mockUserShoppingListItem = new UserShoppingListItem();
        mockUserShoppingListItem.setId(userId);
        mockUserShoppingListItem.setStatus(ShoppingListItemStatus.ACTIVE);
        when(userShoppingListItemRepo.getOne(itemId)).thenReturn(mockUserShoppingListItem);
        when(shoppingListItemTranslationRepo.findByLangAndUserShoppingListItemId(language, userId)).thenReturn(SLITranslationFirst);
        UserShoppingListItemResponseDto expectedDto = new UserShoppingListItemResponseDto();
        expectedDto.setId(1L);
        expectedDto.setText("Swedish cellulose dish cloths");
        expectedDto.setStatus(ShoppingListItemStatus.DONE);
        UserShoppingListItemResponseDto resultFirst = shoppingListItemService.updateUserShopingListItemStatus(userId, itemId, language);
        assertNotNull(resultFirst);
        assertEquals(expectedDto, resultFirst);
        assertEquals(ShoppingListItemStatus.DONE, mockUserShoppingListItem.getStatus());
        assertNotNull(mockUserShoppingListItem.getDateCompleted());
        Mockito.verify(userShoppingListItemRepo, Mockito.times(1)).save(mockUserShoppingListItem);

        when(userShoppingListItemRepo.getAllByUserShoppingListIdAndUserId(itemId, userId)).thenReturn(List.of(mockUserShoppingListItem));

        List<UserShoppingListItemResponseDto> resultSecond = shoppingListItemService.updateUserShoppingListItemStatus(userId, itemId, language, "inProgress");
        expectedDto.setStatus(ShoppingListItemStatus.INPROGRESS);

        assertEquals(expectedDto, resultSecond.get(0));

    }

    @Test
    void deleteUserShoppingListItemsTest() {
        String ids = "1,2,3";
        List<Long> arrayId = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        List<Long> expected = Arrays.asList(1L, 2L, 3L);
        UserShoppingListItem userShoppingListItem = new UserShoppingListItem();

        when(userShoppingListItemRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(userShoppingListItem));
        doNothing().when(userShoppingListItemRepo).delete(userShoppingListItem);

        List<Long> result = shoppingListItemService.deleteUserShoppingListItems(ids);

        assertEquals(expected, result);

        Mockito.verify(userShoppingListItemRepo, Mockito.times(arrayId.size())).delete(Mockito.any());
    }

    @Test
    void getShoppingListByHabitIdTest() {
        Long habitId = 1L;
        List<Long> idList = List.of(1L, 2L, 3L);
        List<ShoppingListItem> mockShoppingListItems = List.of(new ShoppingListItem(), new ShoppingListItem());

        when(shoppingListItemRepo.getAllShoppingListItemIdByHabitIdISContained(habitId)).thenReturn(idList);
        when(shoppingListItemRepo.getShoppingListByListOfId(idList)).thenReturn(mockShoppingListItems);

        List<ShoppingListItemManagementDto> expectedDtos = mockShoppingListItems.stream()
                .map(item -> modelMapper.map(item, ShoppingListItemManagementDto.class))
                .collect(Collectors.toList());
        List<ShoppingListItemManagementDto> result = shoppingListItemService.getShoppingListByHabitId(habitId);

        assertEquals(expectedDtos, result);
    }

    @Test
    void findAllShoppingListItemsForManagementPageNotContainedTest() {
        Long habitId = 1L;
        Pageable pageable = Mockito.mock(Pageable.class);
        List<Long> mockItemIds = List.of(1L, 2L, 3L);
        List<ShoppingListItem> mockItems = List.of(
                new ShoppingListItem(),
                new ShoppingListItem(),
                new ShoppingListItem()
        );
        Page<ShoppingListItem> mockPage = new PageImpl<>(mockItems, pageable, mockItems.size());

        when(shoppingListItemRepo.getAllShoppingListItemsByHabitIdNotContained(habitId)).thenReturn(mockItemIds);
        when(shoppingListItemRepo.getShoppingListByListOfIdPageable(mockItemIds, pageable)).thenReturn(mockPage);

        List<ShoppingListItemManagementDto> expectedDtos = mockItems.stream()
                .map(item -> modelMapper.map(item, ShoppingListItemManagementDto.class))
                .collect(Collectors.toList());
        PageableAdvancedDto<ShoppingListItemManagementDto> expectedDtoPage =
                new PageableAdvancedDto<>(expectedDtos, mockPage.getTotalElements(), mockPage.getPageable().getPageNumber(),
                        mockPage.getTotalPages(), mockPage.getNumber(), mockPage.hasPrevious(),
                        mockPage.hasNext(), mockPage.isFirst(), mockPage.isLast());
        PageableAdvancedDto<ShoppingListItemManagementDto> result =
                shoppingListItemService.findAllShoppingListItemsForManagementPageNotContained(habitId, pageable);

        assertEquals(expectedDtoPage, result);
    }

    @Test
    void findInProgressByUserIdAndLanguageCodeTest() {
        Long userId = 1L;
        String languageCode = "en";
        Habit habit = new Habit();
        var SLITranslationFirst = new ShoppingListItemTranslation();
        SLITranslationFirst.setContent("Swedish cellulose dish cloths");
        ShoppingListItem shoppingListItemFirst = new ShoppingListItem(1L, List.of(new UserShoppingListItem()), Set.of(habit), List.of(new ShoppingListItemTranslation()));
        SLITranslationFirst.setShoppingListItem(shoppingListItemFirst);
        List<ShoppingListItemTranslation> mockTranslations = List.of(SLITranslationFirst);

        when(shoppingListItemRepo.findInProgressByUserIdAndLanguageCode(userId, languageCode))
                .thenReturn(mockTranslations);

        List<ShoppingListItemDto> expectedDtos = mockTranslations.stream()
                .map(trans -> {
                    ShoppingListItemDto dto = modelMapper.map(trans, ShoppingListItemDto.class);
                    dto.setStatus(ShoppingListItemStatus.INPROGRESS.toString());
                    return dto;
                })
                .collect(Collectors.toList());
        List<ShoppingListItemDto> result = shoppingListItemService.findInProgressByUserIdAndLanguageCode(userId, languageCode);

        assertEquals(expectedDtos, result);
    }

}
