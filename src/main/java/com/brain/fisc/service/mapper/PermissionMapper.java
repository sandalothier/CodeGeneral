package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.DateOperation;
import com.brain.fisc.domain.Permission;
import com.brain.fisc.service.dto.DateOperationDTO;
import com.brain.fisc.service.dto.PermissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Permission} and its DTO {@link PermissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper extends EntityMapper<PermissionDTO, Permission> {
  @Mapping(target = "dateOperation", source = "dateOperation", qualifiedByName = "dateOperationId")
  PermissionDTO toDto(Permission s);

  @Named("dateOperationId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  DateOperationDTO toDtoDateOperationId(DateOperation dateOperation);
}
