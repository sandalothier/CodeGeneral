package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonnelMapperTest {

  private PersonnelMapper personnelMapper;

  @BeforeEach
  public void setUp() {
    personnelMapper = new PersonnelMapperImpl();
  }
}
