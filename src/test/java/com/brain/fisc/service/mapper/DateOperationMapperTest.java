package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DateOperationMapperTest {

  private DateOperationMapper dateOperationMapper;

  @BeforeEach
  public void setUp() {
    dateOperationMapper = new DateOperationMapperImpl();
  }
}
