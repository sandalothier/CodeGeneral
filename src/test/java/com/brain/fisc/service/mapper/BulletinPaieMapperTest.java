package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BulletinPaieMapperTest {

  private BulletinPaieMapper bulletinPaieMapper;

  @BeforeEach
  public void setUp() {
    bulletinPaieMapper = new BulletinPaieMapperImpl();
  }
}
