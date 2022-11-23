package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.Societe;
import com.brain.fisc.domain.TypeDocument;
import com.brain.fisc.service.dto.SocieteDTO;
import com.brain.fisc.service.dto.TypeDocumentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TypeDocument} and its DTO {@link TypeDocumentDTO}.
 */
@Mapper(componentModel = "spring")
public interface TypeDocumentMapper extends EntityMapper<TypeDocumentDTO, TypeDocument> {
  @Mapping(target = "societe", source = "societe", qualifiedByName = "societeId")
  TypeDocumentDTO toDto(TypeDocument s);

  @Named("societeId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  SocieteDTO toDtoSocieteId(Societe societe);
}
