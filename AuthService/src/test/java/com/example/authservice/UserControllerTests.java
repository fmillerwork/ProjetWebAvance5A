package com.example.authservice;

import com.example.authservice.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void saveUser_shouldSucceed() throws Exception{
        User profile = new User(1, "password");
        ObjectMapper objectMapper = new ObjectMapper();
        String profile_json = objectMapper.writeValueAsString(profile);

        this.mockMvc.perform(put("/AS/users")
                        .content(profile_json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void saveUser_shouldFailedIfIdAlreadyUsed() throws Exception{
        User profile = new User(1, "password");
        ObjectMapper objectMapper = new ObjectMapper();
        String profile_json = objectMapper.writeValueAsString(profile);

        this.mockMvc.perform(put("/AS/users")
                        .content(profile_json)
                        .contentType(MediaType.APPLICATION_JSON));
        this.mockMvc.perform(put("/AS/users")
                        .content(profile_json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void getUserById_shouldReturnUser() throws Exception{
        User profile = new User(20, "pass");
        ObjectMapper objectMapper = new ObjectMapper();
        String profile_json = objectMapper.writeValueAsString(profile);

        //Ajout user
        this.mockMvc.perform(put("/AS/users")
                .content(profile_json)
                .contentType(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(get("/AS/users/20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\n" +
                                "    \"id\": 20,\n" +
                                "    \"password\": \"pass\"\n" +
                                "}"));
    }

    @Test
    public void getUserById_shouldFailedIfUserNotFound() throws Exception{
        this.mockMvc.perform(get("/AS/users/20"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
