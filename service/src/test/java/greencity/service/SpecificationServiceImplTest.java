package greencity.service;

import greencity.dto.specification.SpecificationNameDto;
import greencity.dto.specification.SpecificationVO;
import greencity.entity.Specification;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.SpecificationRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SpecificationServiceImplTest {

    @Mock
    private SpecificationRepo specificationRepo;

    @InjectMocks
    private SpecificationServiceImpl specificationService;

    @BeforeEach
    public void initModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper
                .getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        Field field;
        try {
            field = SpecificationServiceImpl.class.getDeclaredField("modelMapper");
            field.setAccessible(true);
            field.set(specificationService, modelMapper);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void saveTest() {
        var specification = new Specification(2L, "Animal");
        var toSave = new SpecificationVO(2L, "Animal");
        var expected = new SpecificationVO(2L, "Animal");
        Mockito.when(specificationRepo.save(Mockito.any(Specification.class))).thenReturn(specification);
        var actual = specificationService.save(toSave);
        assertEquals(expected, actual);
    }

    @Test
    void findByIdTest() {
        var expected = new SpecificationVO(1L, "Animal");
        var specification = new Specification(1L, "Animal");
        Mockito.when(specificationRepo.findById(1L)).thenReturn(Optional.of(specification));
        var actual = specificationService.findById(1L);
        assertEquals(expected, actual);
        Assertions.assertDoesNotThrow(() -> specificationService.findById(1L));
        assertEquals(expected, actual);
    }

    @Test
    void findById_notFound_throwException() {
        Mockito.when(specificationRepo.findById(Mockito.any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> specificationService.findById(0L));
    }

    @Test
    void findAll() {
        List<Specification> specifications = List.of(new Specification(1L, "Animal"), new Specification(2L, "Karaoke"), new Specification(3L, "Shopping"));
        List<SpecificationVO> expected = specifications.stream().map(a -> new SpecificationVO(a.getId(), a.getName())).toList();
        Mockito.when(specificationRepo.findAll()).thenReturn(specifications);
        assertEquals(expected, specificationService.findAll());
    }

    @Test
    void deleteByIdTest() {
        var specification = new Specification(1L, "Animal");
        Mockito.when(specificationRepo.findById(1L)).thenReturn(Optional.of(specification));
        specificationService.deleteById(1L);
        Mockito.verify(specificationRepo, Mockito.times(1)).delete(Mockito.any(Specification.class));
        ArgumentCaptor<Specification> specificationCaptor = ArgumentCaptor.forClass(Specification.class);
        Mockito.verify(specificationRepo).delete(specificationCaptor.capture());
        Specification capturedSpecification = specificationCaptor.getValue();
        assertEquals(specification.getId(), capturedSpecification.getId());
        assertEquals(specification.getName(), capturedSpecification.getName());
    }

    @Test
    void findByNameTest() {
        var specification = new Specification(1L, "Animal");
        Mockito.when(specificationRepo.findByName("Animal")).thenReturn(Optional.of(specification));
        var actual = specificationService.findByName("Animal");
        assertEquals(new SpecificationVO(1L, "Animal"), actual);
    }

    @Test
    void findByName_NotFound_ThrowException() {
        Mockito.when(specificationRepo.findByName("Animal")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> specificationService.findByName("Animal"));
    }

    @Test
    void findAllSpecificationDtoTest() {
        List<Specification> specifications = List.of(new Specification(1L, "Animal"), new Specification(2L, "Karaoke"), new Specification(3L, "Shopping"));
        List<SpecificationNameDto> expected = specifications.stream().map(a -> new SpecificationNameDto(a.getName())).toList();
        Mockito.when(specificationRepo.findAll()).thenReturn(specifications);
        assertEquals(expected, specificationService.findAllSpecificationDto());
    }

    @Test
    void findAllSpecificationDto_NotFound_EmptyList() {
        List<Specification> specifications = List.of();
        List<SpecificationNameDto> expected = specifications.stream().map(a -> new SpecificationNameDto(a.getName())).toList();
        Mockito.when(specificationRepo.findAll()).thenReturn(specifications);
        List<SpecificationNameDto> actual = specificationService.findAllSpecificationDto();
        assertEquals(expected, actual);
    }
}
