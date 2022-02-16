package insertUser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.businesslogic.implementations.UserService;

public class InsertUserTest {
	
	@DisplayName("Test InsertUserTest.insertDuplicateUserCode()")
	@Test
	public void insertDuplicateUserCodeTest() {
		String resultMessage = "";
		String firstname = "ciao";
		String lastname = "ciao";
		String userCode = "GMBMGH94B42G999W";
		UserService us = new UserService();
		
		resultMessage = us.insertUser(firstname, lastname, userCode);
		
		Assertions.assertEquals(true, resultMessage.contains("the user already exists"));
	}

}
