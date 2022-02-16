package core.dao.interfaces;

import java.util.List;

import core.entitymanagers.SeatEntityManager;

public interface ISeatDAO {
	public List<SeatEntityManager> getAllSeatsState(Integer coachID);
    public String modifySeatState(Integer seatID, Integer coachID, Integer newStatus, String sessionID, Integer bookedID);
    public String freeReservedSeatsCurrentSession(String sessionID);
    public List<SeatEntityManager> bookReservedSeatsCurrentSession(String sessionID, String userCode, Integer bookedID);

}
