package core.businesslogic.interfaces;

import javax.ejb.Local;

@Local
public interface IUserService {
	public String insertUser(String firstname, String lastname, String userCode);
}
