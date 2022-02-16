package managedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;


@Named
@SessionScoped
public class UserManager implements Serializable{
     
    private String firstname;
    private String lastname;
    private String userCode;
    
    @Inject
    private WindowController windowController;
    
    @PostConstruct
	public void init(){
		
		FacesContext fCtx = FacesContext.getCurrentInstance();
		
		//getSession(true) will create new if there is no session. getSession(false) will return null if there is no session
		HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
		if(session == null) {
			fCtx.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_FATAL,"Fatal Error","Session doesn't exist. Retry"));
	    	return;
		}

		//when a new session begins, the Session Beans are created just one time.
		//parameters to set when a session begins.
		Map<String, Object> sessionMap = fCtx.getExternalContext().getSessionMap();

		//set session attributes to handle multiple tabs
		sessionMap.put("howManySeatsSelected", 0);
		//hasSubmitted: whether the user got the permission to go on or not, i.e. 
		//the 'submit' button has been clicked and personal info have been correctly submitted.
		sessionMap.put("hasSubmitted", false);
		
		//create a map which tracks whether a tab is ACTIVE or NOT.
		Map<String,Boolean> mapTabsOpened = new HashMap<String,Boolean>();
		sessionMap.put("tabsOpened", mapTabsOpened);
		
		//bookedID: to track the booking id among the same session.
		sessionMap.put("bookedID", 1);

		//debug
		System.out.println("USER MANAGER BEAN SESSIONSCOPED | current session id: " + session.getId());
		System.out.println("USER MANAGER BEAN SESSIONSCOPED | POST CONSTRUCT: " + this);
	
	}
    
    @PreDestroy
    public void preDestroy() {
    	//debug
		System.out.println("USER MANAGER BEAN SESSIONSCOPED | PRE DESTROY: " + this);
	}

    public String getLastname() {
		return this.lastname;
	}
    
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getFirstname() {
        return firstname;
    }
	
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public void submit() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		
		String windowID = windowController.getWindowID();

		//see Init() method: the object linked to "tabsOpened" key is a HashMap<String, Boolean>
		@SuppressWarnings("unchecked")
		Map<String,Boolean> mapTabsOpened = (HashMap<String, Boolean>) sessionMap.get("tabsOpened");
		
		//if there are not active tabs inside 'mapTabsOpened', THIS tab can be set to ACTIVE.
		if(!mapTabsOpened.containsValue(true)) { 
			mapTabsOpened.put(windowID, true);
			sessionMap.put("tabsOpened", mapTabsOpened);
		}
		
		Boolean isTabActive = mapTabsOpened.get(windowID);
		if(isTabActive == null) {
	    	FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error","You cannot submit, you already have an active tab."));
	    	return;
		}
		
		if(isTabActive) { //THIS is the ACTIVE tab
		
			sessionMap.put("hasSubmitted", true);
			sessionMap.put("firstname", firstname);
	    	sessionMap.put("lastname", lastname);
	    	sessionMap.put("userCode", userCode);
    	
	    	FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Success","Welcome " + firstname));

		}
    }
}
