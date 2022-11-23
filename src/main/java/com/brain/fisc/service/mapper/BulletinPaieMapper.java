package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.BulletinPaie;
import com.brain.fisc.domain.Periode;
import com.brain.fisc.service.dto.BulletinPaieDTO;
import com.brain.fisc.service.dto.PeriodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BulletinPaie} and its DTO {@link BulletinPaieDTO}.
 */
@Mapper(componentModel = "spring")
public interface BulletinPaieMapper extends EntityMapper<BulletinPaieDTO, BulletinPaie> {
  @Mapping(target = "periode", source = "periode", qualifiedByName = "periodeId")
  BulletinPaieDTO toDto(BulletinPaie s);

  @Named("periodeId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  PeriodeDTO toDtoPeriodeId(Periode periode);
}
