package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.Periode;
import com.brain.fisc.service.dto.PeriodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Periode} and its DTO {@link PeriodeDTO}.
 */
@Mapper(componentModel = "spring")
public interface PeriodeMapper extends EntityMapper<PeriodeDTO, Periode> {}
