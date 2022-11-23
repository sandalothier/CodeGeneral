package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.Pointage;
import com.brain.fisc.service.dto.PointageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pointage} and its DTO {@link PointageDTO}.
 */
@Mapper(componentModel = "spring")
public interface PointageMapper extends EntityMapper<PointageDTO, Pointage> {}
