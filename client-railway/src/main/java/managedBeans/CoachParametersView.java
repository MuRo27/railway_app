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
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import utils.ClientParameters;

@Named
@ViewScoped
public class CoachParametersView implements Serializable{
 
    private Integer selectedCoach;
    private boolean selectMoreSeats;
    private Integer howManySeatsToSelect;
    private Integer oldHowManySeatsToSelect;

    private List<Integer> coaches;
    private List<Integer> howManySeats;
  
    @PostConstruct
    public void init() {
        coaches = new ArrayList<Integer>();
        howManySeats = new ArrayList<Integer>();
        
        for(int i = 1; i <= ClientParameters.NUM_COACHES; i++)
        	coaches.add(i);

        for(int j = 2; j <= ClientParameters.NUM_MAX_SEATS; j++)
        	howManySeats.add(j);
        
        selectMoreSeats = false;
        howManySeatsToSelect = 2;
        
		System.out.println("COACH PARAMETER VIEW | POST CONSTRUCT: " + this);

        
    }
    
    @PreDestroy
    public void preDestroy() {
		System.out.println("COACH PARAMETER VIEW | PRE DESTROY: " + this);
	}
 
    public Integer getSelectedCoach() {
        return selectedCoach;
    }
 
    public void setSelectedCoach(Integer selectedCoach) {
        this.selectedCoach = selectedCoach;
    }
 
    public List<Integer> getCoaches() {
        return coaches;
    }
    
    public List<Integer> getHowManySeats() {
        return howManySeats;
    }

	public boolean isselectMoreSeats() {
		return selectMoreSeats;
	}

	public void setselectMoreSeats(boolean selectMoreSeats) {
		this.selectMoreSeats = selectMoreSeats;
	}
	
	public boolean getselectMoreSeats() {
		return selectMoreSeats;
	}

	public Integer getHowManySeatsToSelect() {
		return howManySeatsToSelect;
	}

	public void setHowManySeatsToSelect(Integer howManySeatsToSelect) {
		this.oldHowManySeatsToSelect = this.howManySeatsToSelect;
		this.howManySeatsToSelect = howManySeatsToSelect;
	}
	
	public void checkSeatSelectionConsistency() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		
		Integer howManySeatsSelected = (Integer) sessionMap.get("howManySeatsSelected");

		if(this.howManySeatsToSelect < howManySeatsSelected) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention",  
        			"You cannot select more seats than the ones already selected.") );
			this.howManySeatsToSelect = oldHowManySeatsToSelect;
			
			return;
		}
		
		if(!this.selectMoreSeats) {
			if(howManySeatsSelected > 1) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention",  
	        			"You cannot select more seats than the ones already selected.") );
				this.selectMoreSeats = true;
			}
		}
	}
    
 
}
