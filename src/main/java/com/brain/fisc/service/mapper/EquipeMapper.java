package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.Equipe;
import com.brain.fisc.service.dto.EquipeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Equipe} and its DTO {@link EquipeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EquipeMapper extends EntityMapper<EquipeDTO, Equipe> {}
