package com.brain.fisc.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TypeDocumentMapperTest {

  private TypeDocumentMapper typeDocumentMapper;

  @BeforeEach
  public void setUp() {
    typeDocumentMapper = new TypeDocumentMapperImpl();
  }
}
