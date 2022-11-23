package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PointageMapperTest {

  private PointageMapper pointageMapper;

  @BeforeEach
  public void setUp() {
    pointageMapper = new PointageMapperImpl();
  }
}
