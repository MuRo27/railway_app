package core.entitymanagers;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity(name="user")
@Table(name="user")
public class UserEntityManager implements Serializable{
	
	@Id
	private String userCode;
	private String name;
	private String surname;
	
	//getters and setters
	
	public UserEntityManager() {
		super();
	}
	
	public String getUserCode() {
		return userCode;
	}
	
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	//for debugging purposes
	@Override
    public String toString() {
        return "User [name=" + name + ", surname=" + surname
                + ", userCode =" + userCode + "]";
    }
	
	
}
