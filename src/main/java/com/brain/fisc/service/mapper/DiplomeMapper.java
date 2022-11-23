package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.Diplome;
import com.brain.fisc.service.dto.DiplomeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Diplome} and its DTO {@link DiplomeDTO}.
 */
@Mapper(componentModel = "spring")
public interface DiplomeMapper extends EntityMapper<DiplomeDTO, Diplome> {}
