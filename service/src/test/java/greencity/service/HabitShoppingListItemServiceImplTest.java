package greencity.service;

import greencity.entity.Habit;
import greencity.entity.ShoppingListItem;
import greencity.repository.HabitRepo;
import greencity.repository.ShoppingListItemRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class HabitShoppingListItemServiceImplTest {
    @Mock
    private ShoppingListItemRepo shoppingListItemRepo;
    @Mock
    private HabitRepo habitRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private HabitShoppingListItemServiceImpl habitShoppingListItemService;


    @Test
    void unlinkShoppingListItems_findHabitByNotExistedId_throwException() {
        List<Long> shopIds = new ArrayList<>();
        Long notExistedHabitId = 1L;

        when(habitRepo.findById(notExistedHabitId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class,
                () -> habitShoppingListItemService.unlinkShoppingListItems(shopIds, notExistedHabitId));
    }

    @Test
    void unlinkShoppingListItems_findShoppingListItemById_throwException() {
        Long nonExistedShopId = 1L;
        List<Long> shopIds = new ArrayList<>();
        shopIds.add(nonExistedShopId);

        Long existedHabitId = 1L;
        Habit habit = new Habit();
        habit.setId(existedHabitId);

        when(habitRepo.findById(existedHabitId)).thenReturn(Optional.of(habit));
        when(shoppingListItemRepo.findById(nonExistedShopId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class,
                () -> habitShoppingListItemService.unlinkShoppingListItems(shopIds, existedHabitId));

    }

    @Test
    void unlinkShoppingListItems_saveHabitWithUpdatedShoppingListItems() {
        Long existedShopId = 1L;
        List<Long> shopIds = new ArrayList<>();
        shopIds.add(existedShopId);

        ShoppingListItem shoppingListItem = new ShoppingListItem();
        shoppingListItem.setId(existedShopId);

        Long existedHabitId = 1L;
        Habit habit = new Habit();
        habit.setId(existedHabitId);
        Set<ShoppingListItem> shoppingListItemSet = new HashSet<>();
        shoppingListItemSet.add(shoppingListItem);
        habit.setShoppingListItems(shoppingListItemSet);

        when(habitRepo.findById(existedHabitId)).thenReturn(Optional.of(habit));
        when(shoppingListItemRepo.findById(existedShopId)).thenReturn(Optional.of(shoppingListItem));

        habitShoppingListItemService.unlinkShoppingListItems(shopIds,existedHabitId);

        Assertions.assertEquals(0, habit.getShoppingListItems().size());
        Mockito.verify(habitRepo,times(1)).save(habit);
    }
}
