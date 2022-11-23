package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TypeContratDeTravailMapperTest {

  private TypeContratDeTravailMapper typeContratDeTravailMapper;

  @BeforeEach
  public void setUp() {
    typeContratDeTravailMapper = new TypeContratDeTravailMapperImpl();
  }
}
