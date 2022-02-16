package managedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.omnifaces.util.Faces;

import javax.inject.Named;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

//import org.primefaces.event.AbstractAjaxBehaviorEvent;
//import org.primefaces.event.SelectEvent;

import core.businesslogic.interfaces.ISeatService;
import core.businesslogic.interfaces.IUserService;
import jndi.LookerUp;
import objects.Seat;
import utils.ClientParameters;

@Named
@ViewScoped
public class CoachView implements Serializable{
	
	private List<Seat> seatsUp;
	private List<Seat> seatsDown;
	private String currentSessionID;
	
	//parameters not shown in View
	private Integer howManySeatsToSelect;
	private Boolean isPageChanging = false;
	
	//to retrieve and modify seats' status from DB
	private ISeatService seatProxy = null;
	private IUserService userProxy = null;
	private LookerUp glassfishLookerUp = new LookerUp();
	
	@Inject
	private WindowController windowController;
	
	@PostConstruct
	public void init(){

		seatsUp = new ArrayList<Seat>();
		seatsDown = new ArrayList<Seat>();
		
		//set session ID
		FacesContext fCtx = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
		if(session == null) {
			fCtx.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_FATAL,"Fatal Error","Session doesn't exist. Retry"));
	    	return;
		}
		
		currentSessionID = session.getId();
		
		howManySeatsToSelect = ClientParameters.DEFAULT_SEATS_TO_SELECT;

		System.out.println("COACH VIEW | POST CONSTRUCT: " + this);
	
	}
	
	@PreDestroy
    public void preDestroy() {
		System.out.println("COACH VIEW | PRE DESTROY: " + this);
		if(isPageChanging)
			windowController.handleClosingTab();
	}
	
	public List<Seat> getSeatsUp() {
		return this.seatsUp;
	}
	
	public List<Seat> getSeatsDown() {
		return this.seatsDown;
	}

	public String getCurrentSessionID() {
		return this.currentSessionID;
	}
	
	private void clearSeatsUpDown() {
		seatsUp.clear();
        seatsDown.clear();
	}
	
	//for action button "Refresh Coach"
	public void refreshCoach() {
		
		Integer selectedCoach = Faces.evaluateExpressionGet("#{coachParameters.selectedCoach}");
		
		//debug
		System.out.println("COACH VIEW | REFRESH: " + selectedCoach);
		
		//update seats' state
        clearSeatsUpDown();
        getCoachSeats(selectedCoach);
	}
	
	private void getCoachSeats(Integer coachID){ //private
		List<String> toBeConvertedSeatsList = null;
	
		try{
			
			seatProxy = (ISeatService) glassfishLookerUp.findLocalSessionBean(ClientParameters.MODULE_NAME,ClientParameters.SEAT_BEAN_NAME,ClientParameters.SEAT_BEAN_INTERFACE_QUALIFIED_NAME);
		}catch(NamingException e) {
			e.printStackTrace();
			System.err.println("COACH VIEW | getCoachSeats method: naming error");
			
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_FATAL,"Fatal Error","There has been an error. Reload this page."));
			return;
		}
		
		
		toBeConvertedSeatsList = seatProxy.getSeats(coachID);

		setSeatsFromListString(toBeConvertedSeatsList, coachID);
		
		
	}
	
	private void setSeatsFromListString(List<String> listSeats, Integer coachID) {
		String[] seatInfo = null;
		for(String entry : listSeats) {
			seatInfo = entry.split(",");
			Integer seatID = Integer.parseInt(seatInfo[0]);
			Integer status = Integer.parseInt(seatInfo[1]);
			String sessionID = seatInfo[2];
			Integer bookingID = Integer.parseInt(seatInfo[3]);
			Seat seat = new Seat(seatID, coachID, status , sessionID, bookingID);

			if(Integer.parseInt(seatInfo[0]) <= ClientParameters.MAX_SEATID_UP_SIDE)
				seatsUp.add(seat);
			else
				seatsDown.add(seat);
		}
	}
	
	//listener for Coach Parameters change event
	public void onCoachParametersChange(AjaxBehaviorEvent event) { //SelectEvent event
		
		Integer oldhowManySeatsToSelect = howManySeatsToSelect;
		
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();

		Integer coachID = (Integer) event.getComponent().getAttributes().get("selectedCoach");
		boolean selectMoreSeats = (boolean) event.getComponent().getAttributes().get("selectMoreSeats");

		if(selectMoreSeats)
			howManySeatsToSelect = (Integer) event.getComponent().getAttributes().get("howManySeats");
		else
			howManySeatsToSelect = ClientParameters.DEFAULT_SEATS_TO_SELECT;
		
		Integer howManySeatsSelected = (Integer) sessionMap.get("howManySeatsSelected");
		
		if(coachID == null)
        	return;
		
		if(howManySeatsToSelect < howManySeatsSelected) {
			
			
			howManySeatsToSelect = oldhowManySeatsToSelect;
			return;
		}
			
		
		refreshCoach();

    }
	
	//action for seat button clicked
	public void onClickSeat() {
		
		/* The seat's status could be changed:
		* - from 'free' to 'reserved', or
		* - from 'reserved' to 'free'
		* The change "from 'reserved' to 'booked'" is not handled because in that case the button is disabled.
		* Once booked, the seat's status cannot be changed anymore.
		*/
		
		FacesContext context = FacesContext.getCurrentInstance();  
		
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap(); 
	  
		Integer howManySeatsSelected = (Integer) sessionMap.get("howManySeatsSelected");
		Integer bookedID = (Integer) sessionMap.get("bookedID");
		
        Integer seatID = Integer.parseInt(params.get("seatID"));
        Integer coachID = Integer.parseInt(params.get("coachID"));
        Integer status = Integer.parseInt(params.get("status")); //current seat's status
        Integer newStatus;
        
        if(status == 0) {
        	
        	if(howManySeatsSelected >= howManySeatsToSelect) {
    			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention",  "You cannot select more than " + howManySeatsToSelect + " seats.") );
    			return;
    		}
        	
        	newStatus = 1;
        	sessionMap.put("howManySeatsSelected", ++howManySeatsSelected);
        }
        else if(status == 1) {
        	newStatus = 0;
        	sessionMap.put("howManySeatsSelected", --howManySeatsSelected);
        }
        else {
        	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR",  
        			"There has been an error. Try again.") );
        	System.err.println("COACH VIEW | onClickSeat method: current seat status " + status + " not permitted.");
        	return;
        }
              
        try{
			
			seatProxy = (ISeatService) glassfishLookerUp.findLocalSessionBean(ClientParameters.MODULE_NAME,ClientParameters.SEAT_BEAN_NAME,ClientParameters.SEAT_BEAN_INTERFACE_QUALIFIED_NAME);
		
        } catch(NamingException e) {
        	
			if(newStatus == 1)
	        	sessionMap.put("howManySeatsSelected", --howManySeatsSelected);
			else if(newStatus == 0)
	        	sessionMap.put("howManySeatsSelected", ++howManySeatsSelected);

			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_FATAL,"Fatal Error","There has been an error. Reload this page."));

			e.printStackTrace();
			System.err.println("COACH VIEW | onClickSeat method: naming error");
			return;
			
		}
        
        List<String> resultUpdatedSeats =  seatProxy.selectOrDeselectSeat(seatID, coachID, newStatus, currentSessionID, bookedID);
        
        String resultMessage = resultUpdatedSeats.remove(0); //message "OKAY" or "NOT OKAY"

        //update seats' state
        clearSeatsUpDown();
        setSeatsFromListString(resultUpdatedSeats, coachID);

        if(resultMessage.compareTo("OKAY") == 0) {
        	//context.addMessage(null, new FacesMessage("Successful",  "seat, coach = " + seatID + "," + coachID + 
        	//	"\nmessage: " + resultMessage + "\nSESSION ID: " + currentSessionID) );
        	context.addMessage(null, new FacesMessage("Successful",  "Reserved seat " + seatID + " in coach " + coachID));
        } else {
        	
        	if(newStatus == 1)
	        	sessionMap.put("howManySeatsSelected", --howManySeatsSelected);
			else if(newStatus == 0)
	        	sessionMap.put("howManySeatsSelected", ++howManySeatsSelected);

        	//context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention",  "seatID, coachID = " + seatID + "," + coachID + 
            //		"\nmessage: " + resultMessage + "\nSESSION ID: " + currentSessionID) );
        	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention",  "It has been impossibile to reserve seat " + seatID + " in coach " + coachID 
        			 + ".\nRefresh this coach to get the updated seats."));
        }
	}
	
	//action button for confirm
	public String confirm() {
		
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

		Integer howManySeatsSelected = (Integer) sessionMap.get("howManySeatsSelected");
				
		if(howManySeatsSelected != howManySeatsToSelect) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention",  
        			"You MUST select " + howManySeatsToSelect + " seats."));
			
			return "";
		}
		
		
		String firstname = (String) sessionMap.get("firstname");
		String lastname = (String) sessionMap.get("lastname");
		String userCode = (String) sessionMap.get("userCode");
		Integer bookedID = (Integer) sessionMap.get("bookedID");
		
		String messageInsertUser = "";
		List<String> listStringBookSeats = null;
		
		//insert user inside DB
		try{
			userProxy = (IUserService) glassfishLookerUp.findLocalSessionBean(ClientParameters.MODULE_NAME,ClientParameters.USER_BEAN_NAME,ClientParameters.USER_BEAN_INTERFACE_QUALIFIED_NAME);
		} catch(NamingException e) {
			e.printStackTrace();
			System.err.println("COACH VIEW | confirm method: userProxy naming error");
			return "error.xhtml";
		}
		
		messageInsertUser = userProxy.insertUser(firstname, lastname, userCode);
		
		//book the reserved seats
		try{
			seatProxy = (ISeatService) glassfishLookerUp.findLocalSessionBean(ClientParameters.MODULE_NAME,ClientParameters.SEAT_BEAN_NAME,ClientParameters.SEAT_BEAN_INTERFACE_QUALIFIED_NAME);
		} catch(NamingException e) {
			e.printStackTrace();
			System.err.println("COACH VIEW | confirm method: seatProxy naming error");
			return "error.xhtml";
		}
		
		listStringBookSeats = seatProxy.bookReservedSeats(currentSessionID, userCode, bookedID);
		
		//debug
		System.out.println("COACH VIEW | " + messageInsertUser + "\nHow many seats booked | " + listStringBookSeats.size());
		
		//add booked seats
		List<Seat> listSelectedSeats = getBookedSeatsFromString(listStringBookSeats);
		sessionMap.put("selectedSeats", listSelectedSeats);
		
		sessionMap.put("howManySeatsSelected", 0);
		sessionMap.put("hasSubmitted", false);
		sessionMap.put("bookedID", ++bookedID);
		isPageChanging = true;
		
		//Why faces-redirect = true
		//https://stackoverflow.com/questions/3642919/javax-faces-application-viewexpiredexception-view-could-not-be-restored
		
		return "confirm.xhtml?faces-redirect=true";
	}
	
	private List<Seat> getBookedSeatsFromString(List<String> listStringBookSeats){
		List<Seat> mySeats = new ArrayList<Seat>();
		String[] seatInfo = null;
		
		for(String entry : listStringBookSeats) {
			seatInfo = entry.split(",");
			Seat seat = new Seat(Integer.parseInt(seatInfo[0]), Integer.parseInt(seatInfo[1]) ,Integer.parseInt(seatInfo[2]), seatInfo[3], Integer.parseInt(seatInfo[4]));
			mySeats.add(seat);
		}
		
		return mySeats;
	}
	
}
