package core.dao.implementations;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

import org.eclipse.persistence.exceptions.DatabaseException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import core.dao.interfaces.IUserDAO;
import core.dao.interfaces.JPADAO;
import core.entitymanagers.UserEntityManager;
import core.utils.Parameters;

@JPADAO
@RequestScoped
public class JPAUserDAO implements IUserDAO {
    
	public String insertUser(String name, String surname, String userCode) {
		
		String resultMessage="";
		
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(Parameters.PERSISTENCE_UNIT_NAME);
	    EntityManager entityMgrObj = entityManagerFactory.createEntityManager();
	    EntityTransaction trans = entityMgrObj.getTransaction();
		
		try {
			
			trans.begin();
			
			//https://vladmihalcea.com/how-to-map-a-composite-identifier-using-an-automatically-generatedvalue-with-jpa-and-hibernate/
			UserEntityManager newUser = new UserEntityManager();
			
			newUser.setUserCode(userCode);
			newUser.setName(name);
			newUser.setSurname(surname);
			
			entityMgrObj.persist(newUser);
			
			resultMessage = "INSERT USER: " + newUser.toString() + " OKAY";
			
			trans.commit();
			
		} catch (RollbackException ex) {

			//ex.printStackTrace();
			//EntityExistsException EclipseLink BUG: http://www.hackerav.com/?post=47006
			
			if (ex.getCause() instanceof DatabaseException) {
				if(ex.getCause().getCause() instanceof MySQLIntegrityConstraintViolationException) {
					System.out.println("INSERT USER "+ name + " " + surname + ": the user already exists.");
					resultMessage = "INSERT USER "+ name + " " + surname + ": the user already exists.";
				} else
					resultMessage = "USER DAO | insertUser method: thrown exception " + ex.getCause().getCause().getClass().getCanonicalName();
			
			} else 
				resultMessage = "USER DAO | insertUser method: thrown exception " + ex.getClass().getCanonicalName();
			
		} catch(Exception ex) {
			
			System.err.println("USER DAO | insertUser method: thrown exception " + ex.getClass().getCanonicalName());
    		//ex.printStackTrace();
    		resultMessage = "USER DAO | insertUser method: thrown exception " + ex.getClass().getCanonicalName();
    		
    		try{
    			if(trans.isActive())
    				trans.rollback();
    		} catch(Exception eRollback) {
				resultMessage += "\nUSER DAO | insertUser method: error during rollback | " + eRollback.getClass().getCanonicalName();
    		}
    		
		} finally {
	    	entityMgrObj.close();
    		entityManagerFactory.close();
	    }
		
		return resultMessage;
		
	}
}
