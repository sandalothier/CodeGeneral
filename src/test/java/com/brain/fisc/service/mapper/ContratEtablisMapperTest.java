package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContratEtablisMapperTest {

  private ContratEtablisMapper contratEtablisMapper;

  @BeforeEach
  public void setUp() {
    contratEtablisMapper = new ContratEtablisMapperImpl();
  }
}
