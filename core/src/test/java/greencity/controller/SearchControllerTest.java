package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import greencity.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SearchControllerTest {
    private static final Locale LOCALE = Locale.US;
    private static final String TEST_QUERY = "TEST QUERY";
    @InjectMocks
    private SearchController searchController;

    @Mock
    private SearchService mockSearchService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @Test
    public void searchValidRequestReturnsOkTest() {
        SearchResponseDto expectedResponse = SearchResponseDto.builder()
                .ecoNews(Collections
                        .emptyList())
                .countOfResults(0L)
                .build();
        when(mockSearchService
                .search(anyString(), eq(LOCALE.getLanguage()))).
                thenReturn(expectedResponse);

        ResponseEntity<SearchResponseDto> response = searchController
                .search(TEST_QUERY, LOCALE);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }



    @Test
    void searchEcoNewsNullPageableReturnsOKTest() {


        Pageable pageable = null;

        SearchService mockSearchService = mock(SearchService.class);

        when(mockSearchService.searchAllNews(pageable, TEST_QUERY, LOCALE.getLanguage()))
                .thenReturn(new PageableDto<>(Collections.emptyList(), 3, 3, 3));

        SearchController searchController = new SearchController(mockSearchService);

        ResponseEntity<PageableDto<SearchNewsDto>> response = searchController
                .searchEcoNews(pageable, TEST_QUERY, LOCALE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void searchEcoNewsNullSearchQueryReturnsOKTest() {

        Pageable pageable = PageRequest.of(0, 10);


        ResponseEntity<PageableDto<SearchNewsDto>> response = searchController
                .searchEcoNews(pageable, "", LOCALE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void searchEcoNewsValidPageableReturnsOKTest() {


        Pageable pageable = PageRequest.of(1, 20);

        SearchService mockSearchService = mock(SearchService.class);

        List<SearchNewsDto> newsList = Collections.singletonList(new SearchNewsDto());
        PageableDto<SearchNewsDto> expectedResponse = new PageableDto<>(newsList, 3, 3, 3);
        when(mockSearchService.searchAllNews(pageable, TEST_QUERY, LOCALE.getLanguage()))
                .thenReturn(expectedResponse);

        SearchController searchController = new SearchController(mockSearchService);

        ResponseEntity<PageableDto<SearchNewsDto>> response = searchController
                .searchEcoNews(pageable, TEST_QUERY, LOCALE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
}