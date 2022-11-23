package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.TypeContratDeTravail;
import com.brain.fisc.service.dto.TypeContratDeTravailDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TypeContratDeTravail} and its DTO {@link TypeContratDeTravailDTO}.
 */
@Mapper(componentModel = "spring")
public interface TypeContratDeTravailMapper extends EntityMapper<TypeContratDeTravailDTO, TypeContratDeTravail> {}
