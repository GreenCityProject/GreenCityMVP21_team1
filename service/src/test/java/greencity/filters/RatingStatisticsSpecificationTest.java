package greencity.filters;

import static org.mockito.Mockito.*;

import greencity.annotations.RatingCalculationEnum;
import greencity.dto.ratingstatistics.RatingStatisticsViewDto;
import greencity.entity.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import greencity.entity.EcoNews_;
import greencity.entity.RatingStatistics_;
import greencity.entity.User_;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RatingStatisticsSpecificationTest {

    @Mock
    private CriteriaBuilder criteriaBuilderMock;

    @Mock
    private CriteriaQuery<RatingStatistics> criteriaQueryMock;

    @Mock
    private Predicate predicateMock;

    @Mock
    private Path<Object> pathRatingStatisticsEnumMock;

    @Mock
    private Path<Long> pathUserIdMock;

    @Mock
    private Path<String> pathUserEmailMock;

    @Mock
    private Root<RatingStatistics> ratingStatisticsRootMock;

    @Mock
    private Predicate getEventNamePredicate;

    @Mock
    private Predicate getUserMailPredicate;

    @Mock
    private Predicate getUserIdPredicate;

    @Mock
    private Join<RatingStatistics, User> userJoinMock;

    @Mock
    private SingularAttribute<RatingStatistics, User> user;

    @Mock
    private SingularAttribute<User, Long> id;

    @Mock
    private SingularAttribute<User, String> email;

    private RatingStatisticsSpecification ratingStatisticsSpecification;

    private List<SearchCriteria> criteriaList;

    @BeforeEach
    void setUp() {
        RatingStatisticsViewDto ratingStatisticsViewDto =
                new RatingStatisticsViewDto("2", "ADD_ECO_NEWS", "1", "test@test.com", "", "", "", "");

        criteriaList = new ArrayList<>();

        criteriaList.add(
                SearchCriteria.builder()
                        .key(RatingStatistics_.RATING_CALCULATION_ENUM)
                        .type("enum")
                        .value(ratingStatisticsViewDto.getEventName())
                        .build());
        criteriaList.add(
                SearchCriteria.builder()
                        .key(RatingStatistics_.USER)
                        .type("userId")
                        .value(ratingStatisticsViewDto.getUserId())
                        .build());

        criteriaList.add(
                SearchCriteria.builder()
                        .key(RatingStatistics_.USER)
                        .type("userMail")
                        .value(ratingStatisticsViewDto.getUserEmail())
                        .build());

        RatingStatistics_.user = user;
        User_.id = id;
        User_.email = email;
        ratingStatisticsSpecification = new RatingStatisticsSpecification(criteriaList);
    }


    @Test
    void toPredicate() {
        // Mock calls to conjunction and disjunction methods
        when(criteriaBuilderMock.conjunction()).thenReturn(predicateMock);
        when(criteriaBuilderMock.disjunction()).thenReturn(predicateMock);

        // Mock calls to join and get methods for root and userJoin
        when(ratingStatisticsRootMock.join(RatingStatistics_.user)).thenReturn(userJoinMock);
        when(userJoinMock.get(User_.id)).thenReturn(pathUserIdMock);
        when(userJoinMock.get(User_.email)).thenReturn(pathUserEmailMock);
        when(ratingStatisticsRootMock.get(RatingStatistics_.RATING_CALCULATION_ENUM)).thenReturn(pathRatingStatisticsEnumMock);

        // Mock calls to equal method for pathRatingStatisticsIdMock and other paths
        when(criteriaBuilderMock.equal(pathRatingStatisticsEnumMock, RatingCalculationEnum.ADD_ECO_NEWS))
                .thenReturn(getEventNamePredicate);
        when(criteriaBuilderMock.equal(pathUserIdMock, "1")).thenReturn(getUserIdPredicate);

        // Mock call to like for pathUserEmailMock
        when(criteriaBuilderMock.like(pathUserEmailMock, "%test@test.com%")).thenReturn(getUserMailPredicate);

        // Mock calls to and methods with correct arguments
        when(criteriaBuilderMock.or(predicateMock, getEventNamePredicate)).thenReturn(getEventNamePredicate);
        when(criteriaBuilderMock.and(predicateMock, getEventNamePredicate)).thenReturn(getEventNamePredicate);
        when(criteriaBuilderMock.and(getEventNamePredicate, getUserIdPredicate)).thenReturn(getUserIdPredicate);
        when(criteriaBuilderMock.and(getUserIdPredicate, getUserMailPredicate)).thenReturn(getUserMailPredicate);

        // Call the method you want to test
        ratingStatisticsSpecification.toPredicate(ratingStatisticsRootMock, criteriaQueryMock, criteriaBuilderMock);

        // Verify that expected methods were called with correct arguments
        verify(criteriaBuilderMock).and(predicateMock, getEventNamePredicate);
        verify(criteriaBuilderMock).and(getEventNamePredicate, getUserIdPredicate);
        verify(criteriaBuilderMock).and(getUserIdPredicate, getUserMailPredicate);
    }

}