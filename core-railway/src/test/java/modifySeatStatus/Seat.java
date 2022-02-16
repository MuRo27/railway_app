package modifySeatStatus;

import java.io.Serializable;

public class Seat implements Serializable{
	private Integer id;
	private Integer coachID;
	private Integer status;
	private String sessionID;
	private Integer bookedID;
	
	public Seat(Integer id, Integer coachID, Integer status, String sessionID, Integer bookedID) {
		this.id = id;
		this.coachID = coachID;
		this.status = status;
		this.sessionID = sessionID;
		this.bookedID = bookedID;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getCoachID() {
		return coachID;
	}
	
	public void setCoachID(Integer coachID) {
		this.coachID = coachID;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public Integer getBookedID() {
		return bookedID;
	}

	public void setBookedID(Integer bookedID) {
		this.bookedID = bookedID;
	}

	@Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
 
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Seat other = (Seat) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }
	

}
