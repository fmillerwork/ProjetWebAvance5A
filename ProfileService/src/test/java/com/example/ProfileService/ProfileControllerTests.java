package com.example.ProfileService;

import com.example.ProfileService.model.Profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProfileControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MockRestServiceServer mockServer;

	@Test
	public void getProfiles_shouldReturnEmptyArray() throws Exception{
		this.mockMvc.perform(get("/PS/profiles"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}

	@Test
	public void saveProfile_shouldSucceed() throws Exception{
		Profile profile = new Profile(1,"Florian","florian.miller@gmail.com","");
		ObjectMapper objectMapper = new ObjectMapper();
		String profile_json = objectMapper.writeValueAsString(profile);

		mockServer.expect(ExpectedCount.once(),
						requestTo(new URI("http://localhost:8080/AS/users")))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withStatus(HttpStatus.OK));

		this.mockMvc.perform(post("/PS/profiles")
						.content(profile_json)
						.contentType(MediaType.APPLICATION_JSON))
					.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void saveProfile_shouldFailedIfEmailIsNotValid() throws Exception{
		Profile profile = new Profile(1,"Florian","wrongEmail","");
		ObjectMapper objectMapper = new ObjectMapper();
		String profile_json = objectMapper.writeValueAsString(profile);

		this.mockMvc.perform(post("/PS/profiles")
						.content(profile_json)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void saveProfile_shouldFailedAddTwoIdenticalEmail() throws Exception{
		Profile profile1 = new Profile(1,"Florian","florian.miller@gmail.com","");
		ObjectMapper objectMapper1 = new ObjectMapper();
		String profile_json1 = objectMapper1.writeValueAsString(profile1);

		Profile profile2 = new Profile(2,"Florian","florian.miller@gmail.com","");
		ObjectMapper objectMapper2 = new ObjectMapper();
		String profile_json2 = objectMapper2.writeValueAsString(profile2);

		mockServer.expect(ExpectedCount.once(),
						requestTo(new URI("http://localhost:8080/AS/users")))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withStatus(HttpStatus.OK));

		this.mockMvc.perform(post("/PS/profiles")
				.content(profile_json1)
				.contentType(MediaType.APPLICATION_JSON)
		);

		this.mockMvc.perform(post("/PS/profiles")
						.content(profile_json2)
						.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isConflict());
	}

	@Test
	public void updateProfileName_shouldSucceedIfNameIsNotNull() throws Exception{
		// TODO
	}


}
