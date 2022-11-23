package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TraitementMapperTest {

  private TraitementMapper traitementMapper;

  @BeforeEach
  public void setUp() {
    traitementMapper = new TraitementMapperImpl();
  }
}
