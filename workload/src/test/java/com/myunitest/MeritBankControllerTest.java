package com.myunitest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.meritamerica.main.controllers.AccountHolderController;
import com.meritamerica.main.models.AccountHolder;
import com.meritamerica.main.services.AccountHolderService;


@RunWith(SpringRunner.class)
@WebMvcTest(value = AccountHolderController.class)
class MeritBankControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private AccountHolderService accHolderService;
	
	private List<AccountHolder> a = new ArrayList<>();
	
	@Test
	public void retrieveDetailsForCourse() throws Exception {

		Mockito.when(
				accHolderService.getAccountHolders()).thenReturn(a);

//		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
//				"/students/Student1/courses/Course1").accept(
//				MediaType.APPLICATION_JSON);
//
//		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
//
//		System.out.println(result.getResponse());
//		String expected = "{id:Course1,name:Spring,description:10 Steps}";
//
//		// {"id":"Course1","name":"Spring","description":"10 Steps, 25 Examples and 10K Students","steps":["Learn Maven","Import Project","First Example","Second Example"]}
//
//		JSONAssert.assertEquals(expected, result.getResponse()
//				.getContentAsString(), false);
	}

	public MockMvc getMockMvc() {
		return mockMvc;
	}

	public void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}



}
