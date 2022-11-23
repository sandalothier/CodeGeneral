package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.ContratEtablis;
import com.brain.fisc.domain.Periode;
import com.brain.fisc.domain.Personnel;
import com.brain.fisc.domain.TypeContratDeTravail;
import com.brain.fisc.service.dto.ContratEtablisDTO;
import com.brain.fisc.service.dto.PeriodeDTO;
import com.brain.fisc.service.dto.PersonnelDTO;
import com.brain.fisc.service.dto.TypeContratDeTravailDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ContratEtablis} and its DTO {@link ContratEtablisDTO}.
 */
@Mapper(componentModel = "spring")
public interface ContratEtablisMapper extends EntityMapper<ContratEtablisDTO, ContratEtablis> {
  @Mapping(target = "intTypeContrat", source = "intTypeContrat", qualifiedByName = "typeContratDeTravailId")
  @Mapping(target = "intPeriode", source = "intPeriode", qualifiedByName = "periodeId")
  @Mapping(target = "personnel", source = "personnel", qualifiedByName = "personnelId")
  ContratEtablisDTO toDto(ContratEtablis s);

  @Named("typeContratDeTravailId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  TypeContratDeTravailDTO toDtoTypeContratDeTravailId(TypeContratDeTravail typeContratDeTravail);

  @Named("periodeId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  PeriodeDTO toDtoPeriodeId(Periode periode);

  @Named("personnelId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  PersonnelDTO toDtoPersonnelId(Personnel personnel);
}
