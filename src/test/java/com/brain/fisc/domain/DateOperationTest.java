package com.brain.fisc.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DateOperationTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(DateOperation.class);
    DateOperation dateOperation1 = new DateOperation();
    dateOperation1.setId("id1");
    DateOperation dateOperation2 = new DateOperation();
    dateOperation2.setId(dateOperation1.getId());
    assertThat(dateOperation1).isEqualTo(dateOperation2);
    dateOperation2.setId("id2");
    assertThat(dateOperation1).isNotEqualTo(dateOperation2);
    dateOperation1.setId(null);
    assertThat(dateOperation1).isNotEqualTo(dateOperation2);
  }
}
