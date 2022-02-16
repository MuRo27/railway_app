package core.businesslogic.implementations;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import core.businesslogic.interfaces.ISeatService;
import core.dao.interfaces.ISeatDAO;
import core.dao.interfaces.JPADAO;
import core.entitymanagers.SeatEntityManager;


@Stateless
public class SeatService implements ISeatService{
	
	@Inject @JPADAO
	private ISeatDAO seatDAO;

	public List<String> getSeats(Integer coachID){
		
		List<SeatEntityManager> seats = null;
		List<String> convertedSeats = new ArrayList<String>();
		String seatInfo = "";
	
		seats = seatDAO.getAllSeatsState(coachID);
		
		for(SeatEntityManager seat: seats) {
			seatInfo = seat.getSeatID() + "," + seat.getStatus() + "," + seat.getSessionID() + "," + seat.getBookedID();
			convertedSeats.add(seatInfo);
		}
		
		return convertedSeats;
	}
	
	public List<String> selectOrDeselectSeat(Integer seatID, Integer coachID, Integer newStatus, String sessionID, Integer bookedID) {
		String message = "";
		List<String> listUpdatedSeats = null;
		List<String> resultUpdatedSeats = new ArrayList<String>();
		
		//"OKAY" "seatId,coachID,status,sessionID, bookingId" "seatId,coachID,status,sessionID, bookingId"
		message = seatDAO.modifySeatState(seatID, coachID, newStatus, sessionID, bookedID);
		if(message.compareTo("SELECT : OKAY") == 0 || message.compareTo("DESELECT: OKAY") == 0)
			message = "OKAY";
		else
			message = "NO OKAY";
		
		listUpdatedSeats = getSeats(coachID);
		resultUpdatedSeats.add(message);
		resultUpdatedSeats.addAll(listUpdatedSeats);
				
		return resultUpdatedSeats;
		
	}
	
	public String freeReservedSeats(String sessionID) {
		String message = "";
		
		message = seatDAO.freeReservedSeatsCurrentSession(sessionID);
		if(message.compareTo("FREE RESERVED SEATS: OKAY") == 0)
			message = "OKAY";
		else
			message = "NO OKAY";
			
		return message;
	}
	
	public List<String> bookReservedSeats(String sessionID, String userCode, Integer bookedID) {
		List<SeatEntityManager> listBookedSeats = null;
		List<String> convertedBookedSeats = new ArrayList<String>();
		String seatInfo = "";

		listBookedSeats = seatDAO.bookReservedSeatsCurrentSession(sessionID, userCode, bookedID);
		
		for(SeatEntityManager seat: listBookedSeats) {
			seatInfo = seat.getSeatID() + "," + seat.getCoachID() + "," + seat.getStatus() + "," + seat.getSessionID() + "," + seat.getBookedID();
			convertedBookedSeats.add(seatInfo);
		}
		
		return convertedBookedSeats;
	}
}
