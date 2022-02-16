package utils;

import javax.naming.NamingException;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import core.businesslogic.interfaces.ISeatService;
import jndi.LookerUp;
 
 
@WebListener
class SessionListener implements HttpSessionListener{
	
	private ISeatService seatProxy = null;
	private LookerUp glassfishLookerUp = new LookerUp();
	
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		
		System.out.println("\nHTTP SESSION CREATED : " + se.getSession().getId());
		//se.getSession().setMaxInactiveInterval(20);
		System.out.println("HTTP SESSION CREATED : Inactivity Timeout " + se.getSession().getMaxInactiveInterval());
 
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
 
		String message = "";
		String sessionId = se.getSession().getId();
		System.out.println("HTTP SESSION DESTROYED: Session Id : "+sessionId +" has expired");
 
		message = freeReservedSeats(sessionId);
		System.out.println("HTTP SESSION DESTROYED: freed seats - " + message);
		
 
	}
	
	private String freeReservedSeats(String sessionID) {
		String resultMessage = "";
		
		try{
		
			seatProxy = (ISeatService) glassfishLookerUp.findLocalSessionBean(ClientParameters.MODULE_NAME,ClientParameters.SEAT_BEAN_NAME,ClientParameters.SEAT_BEAN_INTERFACE_QUALIFIED_NAME);
			resultMessage = seatProxy.freeReservedSeats(sessionID);
		
		}catch(NamingException e) {
			e.printStackTrace();
			System.err.println("HTTP SESSION DESTROYED | freeReservedSeats method: seatProxy naming error");
			resultMessage = "HTTP SESSION DESTROYED | freeReservedSeats method: seatProxy naming error";
		}
		
		return resultMessage;
	}
 
	
}