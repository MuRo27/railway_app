package modifySeatStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;

//on error "the persistence-unit railway-pu cannot be found": update Maven project.

public class ModifySeatStatusTest {
	
	//for this test: #tasks = #threads
	private static final int NUM_BOOKING_USERS = 100; //tasks
	private static final int NUM_THREADS = 100; //threads
	private static final CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS);
	
	//the executor and the array with tasks are kept in private fields
    private final ExecutorService myExecutor = Executors.newFixedThreadPool(NUM_THREADS);
    private BookingUser[] myBookingUsers;
    private Seat seatToSelect;
    private final Integer seatID = 1; //can be changed
    private final Integer coachID = 1; //can be changed
    private final Integer newStatus = 1; //reserved
    private final String sessionID = "47a0479900504cb3ab4a1f626d174d2d"; //can be changed
    private final Integer bookedID = 0;
    
    public ModifySeatStatusTest() {
    	
    } 
    
    @BeforeEach
    public void setUpClass() {
    	//seat to select
    	seatToSelect = new Seat(seatID, coachID, newStatus, sessionID, bookedID);
    	
    	// all the tasks (booking users) are created and put in the array "myBookingUsers"
    	myBookingUsers = new BookingUser[NUM_BOOKING_USERS];
        for (int i=0; i<myBookingUsers.length; myBookingUsers[i++]=new BookingUser(barrier,seatToSelect));
    }
	
	@DisplayName("Test ModifySeatStatus.fromFreeToReserved()")
	@Test
	public void testFromFreeToReserved() {
		List<Future<String>> futureResults=new ArrayList<>();
		List<String> okayMessages = new ArrayList<String>();
		List<String> noOkayMessages = new ArrayList<String>();
		String resultMessage;
        
        for(Callable<String> c: myBookingUsers)
            futureResults.add(myExecutor.submit(c));
                
        for(Future<String> f: futureResults) {
            try {
            	resultMessage = f.get();
            	System.out.println(resultMessage);
            	if(resultMessage.compareTo("SELECT : OKAY") == 0) {
            		okayMessages.add(resultMessage);
            	}
            	
            	if(resultMessage.contains("already reserved")) {
            		noOkayMessages.add(resultMessage);
            	}
            } catch (InterruptedException | ExecutionException ex) {}
        }
        
        myExecutor.shutdown();
        
        Assertions.assertEquals(1,okayMessages.size());
        Assertions.assertEquals(NUM_BOOKING_USERS - 1, noOkayMessages.size());
        
	}

}
