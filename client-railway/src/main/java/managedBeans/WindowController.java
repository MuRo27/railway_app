package managedBeans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.ClientWindow;
import javax.inject.Named;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import core.businesslogic.interfaces.ISeatService;
import jndi.LookerUp;
import utils.ClientParameters;

@Named
@RequestScoped
public class WindowController implements Serializable{
	
	private ISeatService seatProxy = null;
	private LookerUp glassfishLookerUp = new LookerUp();

    public String getWindowID(){
    	
    	String windowID = "";
    	
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        ClientWindow clientWindow = externalContext.getClientWindow();
        windowID = clientWindow.getId();
        
        //System.out.println("WINDOW BEAN | client window id: " + clientWindow.getId());
        
        return windowID;
    }
    
    public void freeReservedSeats() {
		
		//debug
		String resultMessage = "";
		
		FacesContext fCtx = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
		if(session == null) {
			fCtx.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_FATAL,"Fatal Error","Session doesn't exist. Retry"));
	    	return;
		}
		
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		
		try{
		
			seatProxy = (ISeatService) glassfishLookerUp.findLocalSessionBean(ClientParameters.MODULE_NAME,ClientParameters.SEAT_BEAN_NAME,ClientParameters.SEAT_BEAN_INTERFACE_QUALIFIED_NAME);
			resultMessage = seatProxy.freeReservedSeats(session.getId());
			sessionMap.put("howManySeatsSelected", 0);
		
		} catch(NamingException e) {
			
			//e.printStackTrace();
			System.err.println("WINDOW CONTROLLER | freeReservedSeats method : seatProxy - naming error");
			resultMessage = "seatProxy - naming error";
			
		}
		
		//debug
		System.out.println("WINDOW CONTROLLER | freeReservedSeats method : " + resultMessage);
		
		handleClosingTab();

	}
	
	public void handleClosingTab() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

		//see Init() method of UserManagedBean: the object linked to "tabsOpened" key is a HashMap<String, Boolean>
		@SuppressWarnings("unchecked")
		HashMap<String,Boolean> mapTabsOpened = (HashMap<String,Boolean>) sessionMap.get("tabsOpened");
		
		String windowID = getWindowID();
		
		if(mapTabsOpened.remove(windowID)){
			sessionMap.put("tabsOpened", mapTabsOpened);

		} else 
			System.err.println("WINDOW CONTROLLER | handleClosingTab method : error removing window " + windowID);
		
		
	}

}