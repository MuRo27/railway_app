package core.utils;

public class Parameters {
	
	public static final String PERSISTENCE_UNIT_NAME = "railway-pu"; 
	
	//SEATDAO
	public static final Integer FREE = 0;
	public static final Integer RESERVED = 1;
	public static final Integer BOOKED = 2;
	public static final String FREE_SEAT_SESSION_ID_VALUE = "sessionID";
	
	//queries
	public static final String GET_ALL_SEATS_STATE_QUERY = "select s FROM seat s WHERE s.coachID = ?1";
	public static final String FREE_ALL_RESERVED_SEATS_CURRENT_SESSION_QUERY = "UPDATE seat SET status = 0, sessionID = 'sessionID', bookedID = 0 WHERE sessionID=?1 AND status = 1";
	public static final String BOOK_ALL_RESERVED_SEATS_CURRENT_SESSION_QUERY = "UPDATE seat SET status = 2, userCode = ?1 WHERE sessionID=?2 AND status = 1"; //AND bookedID=?3
	public static final String GET_BOOKED_SEATS_QUERY = "SELECT s FROM seat s WHERE s.userCode=?1 AND s.sessionID=?2 AND s.bookedID = ?3 AND s.status = 2";
	
}
