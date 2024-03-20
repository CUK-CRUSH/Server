package crush.myList.controller;

import crush.myList.domain.festival.mongo.document.Form;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class FestivalControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private TestUtil testUtil;

    @AfterEach
    void cleanUp() {
        testUtil.clearForm();
    }

    @Test
    @DisplayName("축제 매칭 신청 확인하기")
    public void matchGet() throws Exception {
        // given
        Form form = testUtil.createForm();

        // when
        MockHttpServletResponse res = mvc.perform(MockMvcRequestBuilders.get("/api/v1/festival/match?username=" + form.getUsername()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        System.out.println(res.getContentAsString());
    }

    @Test
    @DisplayName("축제 매칭 신청 확인하기 - 없는 사용자")
    public void matchGet_notFound() throws Exception {
        // when
        MockHttpServletResponse res = mvc.perform(MockMvcRequestBuilders.get("/api/v1/festival/match?username=notFound"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();

        // then
        System.out.println(res.getContentAsString());
    }

    @Test
    @DisplayName("축제 매칭 신청하기")
    public void matchPost() throws Exception {
        // when
        MockHttpServletResponse res = mvc.perform(MockMvcRequestBuilders.post("/api/v1/festival/match")
                .param("name", "testName")
                .param("sex", "male")
                .param("phone", "010-1234-5678")
                .param("username", "testUser"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        System.out.println(res.getContentAsString());
    }

    @Test
    @DisplayName("축제 매칭 신청하기 - 이미 신청한 사용자")
    public void matchPost_invalidURL() throws Exception {
        // given
        testUtil.createForm();

        // when
        MockHttpServletResponse res = mvc.perform(MockMvcRequestBuilders.post("/api/v1/festival/match")
                        .param("name", "testName")
                        .param("sex", "male")
                        .param("phone", "010-1234-5678")
                        .param("username", "testUser"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        // then
        System.out.println(res.getContentAsString());
    }
}
