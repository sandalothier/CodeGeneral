package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.Poste;
import com.brain.fisc.service.dto.PosteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Poste} and its DTO {@link PosteDTO}.
 */
@Mapper(componentModel = "spring")
public interface PosteMapper extends EntityMapper<PosteDTO, Poste> {}
