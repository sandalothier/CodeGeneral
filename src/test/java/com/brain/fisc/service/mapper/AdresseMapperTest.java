package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdresseMapperTest {

  private AdresseMapper adresseMapper;

  @BeforeEach
  public void setUp() {
    adresseMapper = new AdresseMapperImpl();
  }
}
