package core.entitymanagers;

import java.io.Serializable;

public class SeatEntityManagerID implements Serializable{
	
	private Integer seatID;
	private Integer coachID;
	
	public SeatEntityManagerID() {}
	
	public SeatEntityManagerID(Integer seatID, Integer coachID) {
		this.seatID = seatID;
		this.coachID = coachID;
	}
	
	public Integer getSeatID() {
		return seatID;
	}
	
	public Integer getCoachID() {
		return coachID;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeatEntityManagerID)) return false;
        SeatEntityManagerID that = (SeatEntityManagerID) o;
        return getSeatID().equals(that.getSeatID()) && getCoachID().equals(that.getCoachID());
    }
 
    @Override
    public int hashCode() {

        return getSeatID().hashCode() + getCoachID().hashCode();
    }
}
