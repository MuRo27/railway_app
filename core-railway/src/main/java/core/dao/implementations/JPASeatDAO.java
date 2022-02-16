package core.dao.implementations;

import java.time.LocalTime;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import core.dao.interfaces.ISeatDAO;
import core.dao.interfaces.JPADAO;
import core.entitymanagers.SeatEntityManager;
import core.entitymanagers.SeatEntityManagerID;
import core.utils.Parameters;


/*
 * It's a good practice to define DAOs' interface so that you could provide 
 * several implementations (in this case only the JPA implementation is provided).
 * A DAO entity can be:
 * - requested (e.g. through a DAOFactory)
 * - injected (CDI or EJB; remember that EJB > CDI, see https://dzone.com/articles/comparing-jsf-beans-cdi-beans)
 * The 'injected' way has been followed.
 * 
 * NOTE: https://stackoverflow.com/questions/16723174/cdi-and-pooling
 * CDI doesn't pool and recycle the objects, because it has 
 * no idea whether the objects are stateful or not, and you don't want, in a request, 
 * to get back the state that a bean had in a previous request. That would ruin the 
 * whole point of the request/session scope.
 * 
 * Unless beans are really costly to create (because they start a new connection or 
 * something like that), pooling them doesn't bring any advantage. Short-lived objects 
 * are very fast to create and garbage collect nowadays. And if the bean is really expensive 
 * to create, then it should probably be a singleton.
 * 
 * */

@JPADAO
@RequestScoped
public class JPASeatDAO implements ISeatDAO {
	 
	public List<SeatEntityManager> getAllSeatsState(Integer coachID){
    	
    	EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(Parameters.PERSISTENCE_UNIT_NAME);
	    EntityManager entityMgrObj = entityManagerFactory.createEntityManager();
	    
	    List<SeatEntityManager> seatList = null;
    	
    	try {

    		//https://www.objectdb.com/java/jpa/query/api
    		TypedQuery<SeatEntityManager> queryObj = entityMgrObj.createQuery(Parameters.GET_ALL_SEATS_STATE_QUERY, SeatEntityManager.class);
    		
    		// Update the JPA session cache with objects that the query returns.
        	// Hence the entity objects in the returned collection always updated.
        	//queryObj.setHint(QueryHints.REFRESH, HintValues.TRUE);
    		
        	//https://www.baeldung.com/jpa-query-parameters
        	seatList = (List<SeatEntityManager>) queryObj.setParameter(1, coachID).getResultList();
        	
        	//DEBUG
        	/*if(seatList != null && seatList.size() > 0) {
        		
	        	for(SeatEntityManager seat: seatList) {
	        		System.out.println(seat.toString());
	        	}
        	} else {
        		System.err.println("SEAT DAO | getAllSeatsState method: seatList is empty");
        	}*/
        	
        	
    	} catch(Exception ex) {
    		
    		System.err.println("SEAT DAO | getAllSeatsState method: thrown exception " + ex.getClass().getCanonicalName());
    		//ex.printStackTrace();
    		
    	} finally {
    		
    		entityMgrObj.close();
    		entityManagerFactory.close();
    		
    	}
    	

    	return seatList;
    	
    }
    
    public String modifySeatState(Integer seatID, Integer coachID, Integer newStatus, String sessionID, Integer bookedID){
    	
    	/*
    	 * This method is ONLY used for the following seat state changes:
    	 * - from free to reserved: SELECT the seat
    	 * - from reserved to free: DESELECT the seat
    	 * 
    	 * In order to BOOK the reserved seats another method 'bookReservedSeatsCurrentSession' is used (an UPDATE query is used)
    	 * */
    	
		String resultMessage = "";

    	EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(Parameters.PERSISTENCE_UNIT_NAME);
	    EntityManager entityMgrObj = entityManagerFactory.createEntityManager();
    	EntityTransaction trans = entityMgrObj.getTransaction();

	    try {
    		
    		trans.begin();
    		
    		SeatEntityManagerID seatPrimaryKey= new SeatEntityManagerID(seatID, coachID);
    		SeatEntityManager seat = entityMgrObj.find(SeatEntityManager.class, seatPrimaryKey, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
    
    		if(newStatus.equals(Parameters.RESERVED) && seat.getStatus().equals(Parameters.FREE)) {
    			
    			//to SELECT this seat
    			seat.setStatus(newStatus);
    			seat.setSessionID(sessionID);
    			seat.setBookedID(bookedID);
    			trans.commit();
    			resultMessage = "SELECT : OKAY";
    			
    		} else if(newStatus.equals(Parameters.RESERVED) && seat.getStatus().equals(Parameters.RESERVED)){
    			
    			//no concurrency occurred
    			System.out.println(LocalTime.now() + " - " + Thread.currentThread().getName() + ": nothing to do in MODIFY SEATS STATE");
    			resultMessage = "SELECT: NO OKAY | already reserved - no concurrency";
    			
    			try{
        			if(trans.isActive())
        				trans.rollback();
        		} catch(Exception eRollback) {
    				resultMessage += "\nSELECT or DESELECT: error during rollback | " + eRollback.getClass().getCanonicalName();
        		}
    		}
    		
    		/* 
    		 * 'Deselecting' a seat: note that in this case there could be only one client doing this operation,
    		 * as at this point this seat can be clicked ONLY by the client who has reserved it.
    		 */
    		
    		if(newStatus.equals(Parameters.FREE) && seat.getStatus().equals(Parameters.RESERVED)) {
    			
    			seat.setStatus(newStatus);
    			seat.setSessionID(Parameters.FREE_SEAT_SESSION_ID_VALUE);
    			seat.setBookedID(0); //not reserved or booked by anyone.
    			trans.commit();
    			resultMessage = "DESELECT: OKAY";
    		}
    		
    		
    	} catch(RollbackException e) {
	    	
			//https://www.objectdb.com/api/java/jpa/exceptions
			if (e.getCause() instanceof OptimisticLockException) {
				System.out.println("SEAT DAO | modifySeatState method: someone has already changed the "
	    				+ "status of (seatID,coachID) =  " + seatID + "," + coachID);
				resultMessage = "SELECT: NO OKAY | already reserved - concurrency";
			} else {
				System.err.println("SEAT DAO | modifySeatState method: thrown exception " + e.getClass().getCanonicalName());
	    		//e.printStackTrace();
				resultMessage = "SELECT or DESELECT: error | " + e.getClass().getCanonicalName();
			}
			
			
		} catch(Exception e) {
			
			System.err.println("SEAT DAO | modifySeatState method: thrown exception " + e.getClass().getCanonicalName());
    		//e.printStackTrace();
    		resultMessage = "SELECT or DESELECT: error | " + e.getClass().getCanonicalName();
    		
    		try{
    			if(trans.isActive())
    				trans.rollback();
    		} catch(Exception eRollback) {
				resultMessage += "\nSELECT or DESELECT: error during rollback | " + eRollback.getClass().getCanonicalName();
    		}
    		
		} finally {
			
    		entityMgrObj.close();
    		entityManagerFactory.close();
    		
    	}
	    
	    return resultMessage;
    }
    
    public String freeReservedSeatsCurrentSession(String sessionID) {
    	String resultMessage = "";
    	
    	EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(Parameters.PERSISTENCE_UNIT_NAME);
	    EntityManager entityMgrObj = entityManagerFactory.createEntityManager();
	    EntityTransaction trans = entityMgrObj.getTransaction();

	    try {
	    	
	    	trans.begin();
	    	Query queryObj = entityMgrObj.createQuery(Parameters.FREE_ALL_RESERVED_SEATS_CURRENT_SESSION_QUERY);
    		
        	queryObj.setParameter(1, sessionID).executeUpdate();
        	
        	resultMessage = "FREE RESERVED SEATS: OKAY";
        	trans.commit();
	    	
	    } catch(Exception ex){
	    	
    		System.err.println("SEATS DAO | freeReservedSeatsCurrentSession method: thrown exception " + ex.getClass().getCanonicalName());
    		//ex.printStackTrace();
    		resultMessage = "SEATS DAO | freeReservedSeatsCurrentSession method: thrown exception " + ex.getClass().getCanonicalName();
    		
    		try{
    			if(trans.isActive())
    				trans.rollback();
    		} catch(Exception eRollback) {
				resultMessage += "\nSEATS DAO | freeReservedSeatsCurrentSession method: error during rollback | " + eRollback.getClass().getCanonicalName();
    		}
    		
	    } finally {
	    	entityMgrObj.close();
    		entityManagerFactory.close();
	    }
    	
    	return resultMessage;
    }
    
    public List<SeatEntityManager> bookReservedSeatsCurrentSession(String sessionID, String userCode, Integer bookedID) {
    	
    	/* 
		 * Booking a seat: note that in this case there could be only one client doing this operation,
		 * as at this point this seat can be clicked only by the client who has already reserved it.
		 */
    	
    	List<SeatEntityManager> listBookedSeats = null;
    	
    	EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(Parameters.PERSISTENCE_UNIT_NAME);
	    EntityManager entityMgrObj = entityManagerFactory.createEntityManager();
	    EntityTransaction trans = entityMgrObj.getTransaction();

	    try {
	    	trans.begin();
	    	Query queryObjUpdate = entityMgrObj.createQuery(Parameters.BOOK_ALL_RESERVED_SEATS_CURRENT_SESSION_QUERY);
    		
	    	queryObjUpdate.setParameter(1, userCode);
        	queryObjUpdate.setParameter(2, sessionID);

        	queryObjUpdate.executeUpdate();
        	
        	//get booked seats.
    		TypedQuery<SeatEntityManager> queryObj = entityMgrObj.createQuery(Parameters.GET_BOOKED_SEATS_QUERY, SeatEntityManager.class);

        	queryObj.setParameter(1, userCode);
        	queryObj.setParameter(2, sessionID);
        	queryObj.setParameter(3, bookedID);
        	        	
        	listBookedSeats = queryObj.getResultList();
        	
        	trans.commit();
	    	
	    } catch(Exception ex){
	    	
	    	try{
	    		if(trans.isActive())
	    			trans.rollback();
    		} catch(Exception eRollback) {
				System.err.println("SEAT DAO | bookReservedSeatsCurrentSession method: error during rollback | " + eRollback.getClass().getCanonicalName());
    		}
	    	
    		System.err.println("SEAT DAO | bookReservedSeatsCurrentSession method: thrown exception " + ex.getClass().getCanonicalName());
    		//ex.printStackTrace();
    		
	    } finally {
	    	
	    	entityMgrObj.close();
    		entityManagerFactory.close();
	    }
	    
	    return listBookedSeats;
    	
    }
    
    
	
}
