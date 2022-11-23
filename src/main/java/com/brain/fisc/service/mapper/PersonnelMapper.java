package com.brain.fisc.service.mapper;

import com.brain.fisc.domain.Adresse;
import com.brain.fisc.domain.Diplome;
import com.brain.fisc.domain.Equipe;
import com.brain.fisc.domain.Personnel;
import com.brain.fisc.domain.Pointage;
import com.brain.fisc.domain.Poste;
import com.brain.fisc.domain.Societe;
import com.brain.fisc.service.dto.AdresseDTO;
import com.brain.fisc.service.dto.DiplomeDTO;
import com.brain.fisc.service.dto.EquipeDTO;
import com.brain.fisc.service.dto.PersonnelDTO;
import com.brain.fisc.service.dto.PointageDTO;
import com.brain.fisc.service.dto.PosteDTO;
import com.brain.fisc.service.dto.SocieteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Personnel} and its DTO {@link PersonnelDTO}.
 */
@Mapper(componentModel = "spring")
public interface PersonnelMapper extends EntityMapper<PersonnelDTO, Personnel> {
  @Mapping(target = "codeDiplome", source = "codeDiplome", qualifiedByName = "diplomeId")
  @Mapping(target = "cel", source = "cel", qualifiedByName = "adresseId")
  @Mapping(target = "intPoste", source = "intPoste", qualifiedByName = "posteId")
  @Mapping(target = "societe", source = "societe", qualifiedByName = "societeId")
  @Mapping(target = "pointage", source = "pointage", qualifiedByName = "pointageId")
  @Mapping(target = "equipe", source = "equipe", qualifiedByName = "equipeId")
  PersonnelDTO toDto(Personnel s);

  @Named("diplomeId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  DiplomeDTO toDtoDiplomeId(Diplome diplome);

  @Named("adresseId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  AdresseDTO toDtoAdresseId(Adresse adresse);

  @Named("posteId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  PosteDTO toDtoPosteId(Poste poste);

  @Named("societeId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  SocieteDTO toDtoSocieteId(Societe societe);

  @Named("pointageId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  PointageDTO toDtoPointageId(Pointage pointage);

  @Named("equipeId")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  EquipeDTO toDtoEquipeId(Equipe equipe);
}
