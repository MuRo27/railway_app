package core.businesslogic.implementations;

import javax.ejb.Stateless;
import javax.inject.Inject;

import core.businesslogic.interfaces.IUserService;
import core.dao.interfaces.IUserDAO;
import core.dao.interfaces.JPADAO;

@Stateless
public class UserService implements IUserService{
	
	@Inject @JPADAO
	private IUserDAO userDAO;
	
	public String insertUser(String firstname, String lastname, String userCode){
		String message = "";
		message = userDAO.insertUser(firstname, lastname, userCode);
		
		if(message.contains("exception"))
			message = "INSERT USER: there has been an error";
		
		return message;
	}
}
