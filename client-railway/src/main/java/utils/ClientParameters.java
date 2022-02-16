package utils;

import core.businesslogic.interfaces.ISeatService;
import core.businesslogic.interfaces.IUserService;

public class ClientParameters {
	
	//JNDI
	public static final String MODULE_NAME = "client-railway";
	//Seat Service
	public static final String SEAT_BEAN_NAME = "SeatService";
	public static final String SEAT_BEAN_INTERFACE_QUALIFIED_NAME = ISeatService.class.getName();
	//User Service
	public static final String USER_BEAN_NAME = "UserService";
	public static final String USER_BEAN_INTERFACE_QUALIFIED_NAME = IUserService.class.getName();
	
	//seats parameters
	public static final Integer NUM_SEATS_PER_COACH = 5;
	public static final Integer DEFAULT_SEATS_TO_SELECT = 1;
	public static final Integer MAX_SEATID_UP_SIDE = 8;
	
	//coach parameters
	public static final Integer NUM_COACHES = 5;
	public static final Integer NUM_MAX_SEATS = 4;
}
