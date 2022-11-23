package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.Societe;
import com.brain.fisc.service.dto.SocieteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Societe} and its DTO {@link SocieteDTO}.
 */
@Mapper(componentModel = "spring")
public interface SocieteMapper extends EntityMapper<SocieteDTO, Societe> {}
