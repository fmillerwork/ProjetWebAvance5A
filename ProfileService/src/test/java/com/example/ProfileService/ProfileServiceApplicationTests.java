package com.example.ProfileService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProfileServiceApplicationTests {

	@Autowired
	public MockMvc mockMvc;

	@Autowired
	private MockRestServiceServer server;

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
	public void repeatedEmailRejectedNewProfile() throws Exception {
		server.expect(once(),
				requestTo("http://localhost:8080/AS/users"))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(
						withSuccess("1", MediaType.APPLICATION_JSON));

		server.expect(manyTimes(),
				requestTo("http://localhost:8080/token"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(
						withSuccess("1", MediaType.APPLICATION_JSON));

		String email = "apero@gmail.com";
		Profile profile1 = new Profile(
				1, "APERO", email, "");
		Profile profile2 = new Profile(
				2, "TACOS SAUCISSE", email, "");

		ObjectMapper objectMapper = new ObjectMapper();
		String user_json1 = objectMapper.writeValueAsString(profile1);
		String user_json2 = objectMapper.writeValueAsString(profile2);

		this.mockMvc.perform(put("/PS/profiles")
				.content(user_json1)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print());

		this.mockMvc.perform(put("/PS/profiles")
				.content(user_json2)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isConflict());
	}

	@Test
	void putProfileShouldSucceed() throws Exception {
		server.expect(once(),
				requestTo("http://localhost:8080/AS/users"))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(
						withSuccess("1", MediaType.APPLICATION_JSON));

		server.expect(manyTimes(),
				requestTo("http://localhost:8080/token"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(
						withSuccess("1", MediaType.APPLICATION_JSON));

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

	@Test
	public void shouldReturnErrorOnWrongProfile() throws Exception {
		this.mockMvc.perform(get("/PS/profiles/{id}", 1))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void profileCanBeAdded() throws Exception {
		server.expect(once(),
				requestTo("http://localhost:8080/AS/users"))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(
						withSuccess("1", MediaType.APPLICATION_JSON));

		server.expect(manyTimes(),
				requestTo("http://localhost:8080/token"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(
						withSuccess("1", MediaType.APPLICATION_JSON));

		Profile profile = new Profile(1, "APERO","apero@gmail.com","");

		ObjectMapper objectMapper = new ObjectMapper();
		String user_json = objectMapper.writeValueAsString(profile);

		this.mockMvc.perform(put("/PS/profiles")
				.content(user_json)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		this.mockMvc.perform(get("/PS/profiles/{id}", 1))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(user_json));
	}

	@Test
	public void putNameShouldChangeName() throws Exception {
		server.expect(once(),
				requestTo("http://localhost:8080/AS/users"))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(
						withSuccess("1", MediaType.APPLICATION_JSON));

		server.expect(manyTimes(),
				requestTo("http://localhost:8080/token"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(
						withSuccess("1", MediaType.APPLICATION_JSON));

		Profile profile = new
				Profile(1, "APERO","apero@gmail.com","");
		String new_name = "TACOS SAUCISSE";
		Profile profile2 = new
				Profile(1, new_name,
				"apero@gmail.com", "");
		ObjectMapper objectMapper = new ObjectMapper();
		String profile_json = objectMapper.writeValueAsString(profile);
		String profile2_json = objectMapper.writeValueAsString(profile2);

		this.mockMvc.perform(put("/PS/profiles")
				.content(profile_json)
				.contentType(MediaType.APPLICATION_JSON));
		this.mockMvc.perform(put("/PS/profiles/1/name")
				.content(new_name).contentType(MediaType.TEXT_PLAIN));
		this.mockMvc.perform(get("/PS/profiles/1/name"))
				.andExpect(status().isOk())
				.andExpect(content().string(new_name));
		this.mockMvc.perform(get("/PS/profiles/1"))
				.andExpect(status().isOk())
				.andExpect(content().json(profile2_json));
	}
}
