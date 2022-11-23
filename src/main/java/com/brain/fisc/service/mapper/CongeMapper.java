package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.Conge;
import com.brain.fisc.domain.Periode;
import com.brain.fisc.service.dto.CongeDTO;
import com.brain.fisc.service.dto.PeriodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Conge} and its DTO {@link CongeDTO}.
 */
@Mapper(componentModel = "spring")
public interface CongeMapper extends EntityMapper<CongeDTO, Conge> {
  @Mapping(target = "periode", source = "periode", qualifiedByName = "periodeId")
  CongeDTO toDto(Conge s);

  @Named("periodeId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  PeriodeDTO toDtoPeriodeId(Periode periode);
}
