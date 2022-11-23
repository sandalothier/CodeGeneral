package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.DateOperation;
import com.brain.fisc.domain.Periode;
import com.brain.fisc.service.dto.DateOperationDTO;
import com.brain.fisc.service.dto.PeriodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DateOperation} and its DTO {@link DateOperationDTO}.
 */
@Mapper(componentModel = "spring")
public interface DateOperationMapper extends EntityMapper<DateOperationDTO, DateOperation> {
  @Mapping(target = "periode", source = "periode", qualifiedByName = "periodeId")
  DateOperationDTO toDto(DateOperation s);

  @Named("periodeId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  PeriodeDTO toDtoPeriodeId(Periode periode);
}
