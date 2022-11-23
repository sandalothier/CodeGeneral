package com.brain.fisc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.brain.fisc.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PosteDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(PosteDTO.class);
    PosteDTO posteDTO1 = new PosteDTO();
    posteDTO1.setId("id1");
    PosteDTO posteDTO2 = new PosteDTO();
    assertThat(posteDTO1).isNotEqualTo(posteDTO2);
    posteDTO2.setId(posteDTO1.getId());
    assertThat(posteDTO1).isEqualTo(posteDTO2);
    posteDTO2.setId("id2");
    assertThat(posteDTO1).isNotEqualTo(posteDTO2);
    posteDTO1.setId(null);
    assertThat(posteDTO1).isNotEqualTo(posteDTO2);
  }
}
