package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.DateOperation;
import com.brain.fisc.domain.Traitement;
import com.brain.fisc.service.dto.DateOperationDTO;
import com.brain.fisc.service.dto.TraitementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Traitement} and its DTO {@link TraitementDTO}.
 */
@Mapper(componentModel = "spring")
public interface TraitementMapper extends EntityMapper<TraitementDTO, Traitement> {
  @Mapping(target = "dateOperation", source = "dateOperation", qualifiedByName = "dateOperationId")
  TraitementDTO toDto(Traitement s);

  @Named("dateOperationId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  DateOperationDTO toDtoDateOperationId(DateOperation dateOperation);
}
