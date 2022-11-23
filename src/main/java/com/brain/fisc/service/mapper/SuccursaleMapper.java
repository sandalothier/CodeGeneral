package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.Societe;
import com.brain.fisc.domain.Succursale;
import com.brain.fisc.service.dto.SocieteDTO;
import com.brain.fisc.service.dto.SuccursaleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Succursale} and its DTO {@link SuccursaleDTO}.
 */
@Mapper(componentModel = "spring")
public interface SuccursaleMapper extends EntityMapper<SuccursaleDTO, Succursale> {
  @Mapping(target = "societe", source = "societe", qualifiedByName = "societeId")
  SuccursaleDTO toDto(Succursale s);

  @Named("societeId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  SocieteDTO toDtoSocieteId(Societe societe);
}
