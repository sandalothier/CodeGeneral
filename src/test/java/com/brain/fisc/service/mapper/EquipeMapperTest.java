package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EquipeMapperTest {

  private EquipeMapper equipeMapper;

  @BeforeEach
  public void setUp() {
    equipeMapper = new EquipeMapperImpl();
  }
}
