package crush.myList.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class AdminControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private TestUtil testUtil;

    @Test
    @WithMockUser(username="user", roles={"ADMIN"})
    @DisplayName("관리자 페이지 접속")
    public void adminIndex() throws Exception {
        // when
        MvcResult res = mvc.perform(MockMvcRequestBuilders.get("/admin/index"))
                .andExpect(status().isOk())
                .andReturn();
        // then
//        System.out.println(res.getResponse().getContentAsString());
    }
}
