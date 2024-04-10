package greencity.mapping;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.tag.TagVO;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@Component
public class TagUaEnDtoMapper extends AbstractConverter<TagVO, TagUaEnDto> {
    @Override
    protected TagUaEnDto convert(TagVO tagVO) {
        TagUaEnDto tagUaEnDto = new TagUaEnDto();
        tagUaEnDto.setId(tagVO.getId());
         tagVO.getTagTranslations()
                .forEach(tagTranslationVO -> {
                    if (tagTranslationVO.getLanguageVO().getCode().equals("ua")) {
                        tagUaEnDto.setNameUa(tagTranslationVO.getName());
                    }
                    else if (tagTranslationVO.getLanguageVO().getCode().equals("en")) {
                        tagUaEnDto.setNameEn(tagTranslationVO.getName());
                    }
                });

        return tagUaEnDto;
    }

    public List<TagUaEnDto> mapAllToList(List<TagVO>tagsVO){
        return tagsVO.stream().map(this::convert).collect(Collectors.toList());
    }
}
