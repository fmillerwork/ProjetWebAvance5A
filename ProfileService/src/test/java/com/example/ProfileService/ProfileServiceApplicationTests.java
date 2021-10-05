package com.example.ProfileService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProfileServiceApplicationTests {

	@Autowired
	public MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void getProfilesShouldReturnEmptyArray() throws Exception {
		this.mockMvc.perform(get("/PS/profiles",1))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().json("[]"));
	}

	@Test
	void putProfileShoudSucceed() throws Exception {
		Profile profile = new Profile(1,"APERO","apero@gmail.com","");
		ObjectMapper objectMapper = new ObjectMapper();
		String profile_json = objectMapper.writeValueAsString(profile);

		this.mockMvc.perform(put("/PS/profiles")
					.content(profile_json)
					.contentType(MediaType.APPLICATION_JSON)
					)
				.andDo(print())
				.andExpect(status().isOk());
	}
}
