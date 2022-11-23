package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiplomeMapperTest {

  private DiplomeMapper diplomeMapper;

  @BeforeEach
  public void setUp() {
    diplomeMapper = new DiplomeMapperImpl();
  }
}
