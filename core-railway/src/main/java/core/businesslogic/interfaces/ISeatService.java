package core.businesslogic.interfaces;

import java.util.List;

import javax.ejb.Local;

@Local
public interface ISeatService {
	public List<String> getSeats(Integer coachID);
	public List<String> selectOrDeselectSeat(Integer seatID, Integer coachID, Integer newStatus, String sessionID, Integer bookedID);
	public String freeReservedSeats(String sessionID);
	public List<String> bookReservedSeats(String sessionID, String userCode, Integer bookedID);
}
