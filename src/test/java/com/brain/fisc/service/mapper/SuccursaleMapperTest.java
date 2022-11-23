package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SuccursaleMapperTest {

  private SuccursaleMapper succursaleMapper;

  @BeforeEach
  public void setUp() {
    succursaleMapper = new SuccursaleMapperImpl();
  }
}
