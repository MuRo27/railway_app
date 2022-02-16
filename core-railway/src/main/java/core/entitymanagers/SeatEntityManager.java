package core.entitymanagers;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity(name="seat")
@Table(name="seat")
@IdClass(SeatEntityManagerID.class)
public class SeatEntityManager implements Serializable{

	//https://www.baeldung.com/jpa-composite-primary-keys
	
	@Id
	private Integer seatID;
	@Id
	private Integer coachID;
	private String userCode;
	private Integer status;
	private String sessionID;
	private Integer bookedID;
	@Version
	private Integer version;
	
	//getters and setters
	
	public Integer getSeatID() {
		return seatID;
	}
	
	public Integer getCoachID() {
		return coachID;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
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

		//for debugging purposes
		@Override
	    public String toString() {
	        return "Seat [seatID=" + seatID + " , coachID=" + coachID + " , userCode=" + userCode + " , status = " + status + " , version=" + version
	                + " , sessionID = " + sessionID + "]";
	    }
}
