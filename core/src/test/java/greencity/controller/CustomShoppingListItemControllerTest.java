package greencity.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemSaveRequestDto;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.CustomShoppingListItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class CustomShoppingListItemControllerTest {

    @Mock
    private CustomShoppingListItemService customShoppingListItemService;

    @InjectMocks
    private CustomShoppingListItemController customShoppingListItemController;

    private MockMvc mockMvc;

    private static final XmlMapper xmlMapper = new XmlMapper();

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(customShoppingListItemController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }

    @Test
    void getAllAvailableCustomShoppingListItemsTest() throws Exception {
        long userId = 1L;
        long habitId = 3L;

        List<CustomShoppingListItemResponseDto> responseDtoList = List.of(new CustomShoppingListItemResponseDto(2L, "Swedish cellulose dish cloths", ShoppingListItemStatus.ACTIVE),
                new CustomShoppingListItemResponseDto(4L, "Wowables reusable & biodegradable paper towel", ShoppingListItemStatus.ACTIVE),
                new CustomShoppingListItemResponseDto(5L, "Bamboo kitchen dish cloths", ShoppingListItemStatus.ACTIVE));
        String expectedXml = "<List><item><id>2</id><text>Swedish cellulose dish cloths</text><status>ACTIVE</status></item><item><id>4</id><text>Wowables reusable &amp; biodegradable paper towel</text><status>ACTIVE</status></item><item><id>5</id><text>Bamboo kitchen dish cloths</text><status>ACTIVE</status></item></List>";

        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(userId, habitId)).thenReturn(responseDtoList);
        String responseXml = mockMvc.perform(get("/custom/shopping-list-items/{userId}/{habitId}", userId, habitId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(expectedXml, responseXml);
        verify(customShoppingListItemService, times(1)).findAllAvailableCustomShoppingListItems(userId, habitId);
    }

    @Test
    void saveUserCustomShoppingListItemsTest() throws Exception {
        long userId = 1L;
        long habitAssignId = 3L;
        List<CustomShoppingListItemSaveRequestDto> requestDtoList = List.of(
                new CustomShoppingListItemSaveRequestDto("Swedish cellulose dish cloths"),
                new CustomShoppingListItemSaveRequestDto("Wowables reusable & biodegradable paper towel"),
                new CustomShoppingListItemSaveRequestDto("Bamboo kitchen dish cloths"));
        BulkSaveCustomShoppingListItemDto requestDto = new BulkSaveCustomShoppingListItemDto(requestDtoList);
        String requestObject = xmlMapper.writeValueAsString(requestDto);
        List<CustomShoppingListItemResponseDto> responseDtoList = List.of(new CustomShoppingListItemResponseDto(2L, "Swedish cellulose dish cloths", ShoppingListItemStatus.ACTIVE),
                new CustomShoppingListItemResponseDto(4L, "Wowables reusable & biodegradable paper towel", ShoppingListItemStatus.ACTIVE),
                new CustomShoppingListItemResponseDto(5L, "Bamboo kitchen dish cloths", ShoppingListItemStatus.ACTIVE));
        String expected = "<List><item><id>2</id><text>Swedish cellulose dish cloths</text><status>ACTIVE</status></item><item><id>4</id><text>Wowables reusable &amp; biodegradable paper towel</text><status>ACTIVE</status></item><item><id>5</id><text>Bamboo kitchen dish cloths</text><status>ACTIVE</status></item></List>";

        when(customShoppingListItemService.save(requestDto, userId, habitAssignId)).thenReturn(responseDtoList);
        String actual = mockMvc.perform(post("/custom/shopping-list-items/{userId}/{habitAssignId}/custom-shopping-list-items", userId, habitAssignId)
                        .contentType(MediaType.APPLICATION_XML).content(requestObject))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void updateItemStatusTest() throws Exception{
        long userId = 1L;
        long itemId = 3L;
        String itemStatus = "DONE";
        var cSLIResponseDto = new CustomShoppingListItemResponseDto(4L, "Wowables reusable & biodegradable paper towel", ShoppingListItemStatus.DONE);
        var expected = xmlMapper.writeValueAsString(cSLIResponseDto);

        when(customShoppingListItemService.updateItemStatus(userId, itemId, itemStatus)).thenReturn(cSLIResponseDto);
        var actual = mockMvc.perform(patch("/custom/shopping-list-items/{userId}/custom-shopping-list-items", userId)
                .param("itemId", String.valueOf(itemId))
                .param("status", itemStatus))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(expected, actual);
    }

    @Test
    void updateItemStatusToDone() throws Exception{
        long userId = 1L;
        long itemId = 3L;

        doNothing().when(customShoppingListItemService).updateItemStatusToDone(userId, itemId);
        mockMvc.perform(patch("/custom/shopping-list-items/{userId}/done", userId)
                .param("itemId", String.valueOf(itemId)))
                        .andExpect(status().isOk());
        verify(customShoppingListItemService, times(1)).updateItemStatusToDone(userId, itemId);
    }



    @Test
    void bulkDeleteCustomShoppingListItemsTest() throws Exception {
        long userId = 1L;
        String ids = "1,2,3";

        when(customShoppingListItemService.bulkDelete(ids)).thenReturn(List.of(1L, 2L, 3L));
        mockMvc.perform(delete("/custom/shopping-list-items/{userId}/custom-shopping-list-items", userId)
                        .param("ids", ids))
                .andExpect(status().isOk());
        verify(customShoppingListItemService, times(1)).bulkDelete(ids);
    }


    @Test
    void getAllCustomShoppingItemsByStatusTest() throws Exception {
        long userId = 1L;
        String status= "ACTIVE";
        List<CustomShoppingListItemResponseDto> cSLIResponseDtoList = List.of(new CustomShoppingListItemResponseDto(2L, "Swedish cellulose dish cloths", ShoppingListItemStatus.ACTIVE),
                new CustomShoppingListItemResponseDto(4L, "Wowables reusable & biodegradable paper towel", ShoppingListItemStatus.ACTIVE),
                new CustomShoppingListItemResponseDto(5L, "Bamboo kitchen dish cloths", ShoppingListItemStatus.ACTIVE));
        String expected = "<List><item><id>2</id><text>Swedish cellulose dish cloths</text><status>ACTIVE</status></item><item><id>4</id><text>Wowables reusable &amp; biodegradable paper towel</text><status>ACTIVE</status></item><item><id>5</id><text>Bamboo kitchen dish cloths</text><status>ACTIVE</status></item></List>";

        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(userId, status)).thenReturn(cSLIResponseDtoList);
        String actual = mockMvc.perform(get("/custom/shopping-list-items/{userId}/custom-shopping-list-items", userId)
                .param("status",  status))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(expected, actual);
    }
    
}
