package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SocieteMapperTest {

  private SocieteMapper societeMapper;

  @BeforeEach
  public void setUp() {
    societeMapper = new SocieteMapperImpl();
  }
}
