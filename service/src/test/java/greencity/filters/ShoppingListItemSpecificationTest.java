package greencity.filters;

import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.ShoppingListItem_;
import greencity.entity.Translation;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.ShoppingListItemTranslation_;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static greencity.entity.Translation_.content;
import static java.lang.StringTemplate.STR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingListItemSpecificationTest {
    @Mock
    private CriteriaBuilder criteriaBuilderMock;
    @Mock
    private CriteriaQuery<?> criteriaQueryMock;
    @Mock
    private Predicate predicateMock;
    @Mock
    private Path<Object> pathObjectMock;
    @Mock
    private Path<String> pathStringMock;
    @Mock
    private Path<Long> longPathMock;
    @Mock
    private Path<ShoppingListItem> shoppingListItemPath;
    @Mock
    private Root<ShoppingListItemTranslation> itemTranslationRootMock;
    @Mock
    private Root<ShoppingListItem> shoppingListItemRootMock;
    @Mock
    private SingularAttribute<ShoppingListItem, Long> id;
    @Mock
    private SingularAttribute<Translation, String> transContent;
    @Mock
    private SingularAttribute<ShoppingListItemTranslation, ShoppingListItem> shoppingListItem;
    private ShoppingListItemSpecification shoppingListItemSpecification;
    private List<SearchCriteria> criteriaList;

    @BeforeEach
    void setUp() {
        ShoppingListItemDto shoppingListItemDto =
            new ShoppingListItemDto(1L, "content", "");
        criteriaList = new ArrayList<>();

        criteriaList.add(SearchCriteria.builder()
            .key("id")
            .type("id")
            .value(shoppingListItemDto.getId())
            .build());
        criteriaList.add(SearchCriteria.builder()
            .key("content")
            .type("content")
            .value(shoppingListItemDto.getText())
            .build());

        ShoppingListItemTranslation_.shoppingListItem = shoppingListItem;
        ShoppingListItem_.id = id;
        content = transContent;

        shoppingListItemSpecification = new ShoppingListItemSpecification(criteriaList);
    }

    @Test
    void toPredicate_contentSearchCriteria_returnsPredicate() {
        when(criteriaBuilderMock.conjunction()).thenReturn(predicateMock);
        when(shoppingListItemRootMock.get(criteriaList.getFirst().getKey())).thenReturn(pathObjectMock);
        when(criteriaBuilderMock.equal(pathObjectMock, criteriaList.getFirst().getValue())).thenThrow(NumberFormatException.class);
        when(criteriaBuilderMock.conjunction()).thenReturn(predicateMock);
        when(criteriaBuilderMock.disjunction()).thenReturn(predicateMock);
        when(criteriaQueryMock.from(ShoppingListItemTranslation.class)).thenReturn(itemTranslationRootMock);

        when(criteriaBuilderMock.conjunction()).thenReturn(predicateMock);
        when(itemTranslationRootMock.get(content)).thenReturn(pathStringMock);
        when(criteriaBuilderMock.like(pathStringMock, STR + "%" + criteriaList.get(1).getValue() + "%")).thenReturn(predicateMock);
        when(itemTranslationRootMock.get(ShoppingListItemTranslation_.shoppingListItem)).thenReturn(
                shoppingListItemPath);
        when(shoppingListItemPath.get(ShoppingListItem_.id)).thenReturn(longPathMock);
        when(shoppingListItemRootMock.get(ShoppingListItem_.id)).thenReturn(longPathMock);
        when(criteriaBuilderMock.equal(longPathMock, longPathMock)).thenReturn(predicateMock);
        when(criteriaBuilderMock.and(predicateMock, predicateMock)).thenReturn(predicateMock);


        Predicate predicate = shoppingListItemSpecification
                .toPredicate(shoppingListItemRootMock, criteriaQueryMock, criteriaBuilderMock);
        assertEquals(predicateMock, predicate);
    }
}
