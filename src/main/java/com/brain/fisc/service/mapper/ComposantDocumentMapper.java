package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.ComposantDocument;
import com.brain.fisc.domain.TypeDocument;
import com.brain.fisc.service.dto.ComposantDocumentDTO;
import com.brain.fisc.service.dto.TypeDocumentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ComposantDocument} and its DTO {@link ComposantDocumentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ComposantDocumentMapper extends EntityMapper<ComposantDocumentDTO, ComposantDocument> {
  @Mapping(target = "typeDocument", source = "typeDocument", qualifiedByName = "typeDocumentId")
  ComposantDocumentDTO toDto(ComposantDocument s);

  @Named("typeDocumentId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  TypeDocumentDTO toDtoTypeDocumentId(TypeDocument typeDocument);
}
