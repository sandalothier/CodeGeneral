package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComposantDocumentMapperTest {

  private ComposantDocumentMapper composantDocumentMapper;

  @BeforeEach
  public void setUp() {
    composantDocumentMapper = new ComposantDocumentMapperImpl();
  }
}
