package modifySeatStatus;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

import core.dao.implementations.JPASeatDAO;
import core.dao.interfaces.ISeatDAO;


public class BookingUser implements Callable<String>{
						
	private ISeatDAO seatDAO = new JPASeatDAO();

	private Seat seatToSelect;
	
	private CyclicBarrier cyclicBarrier;
	
	public BookingUser(CyclicBarrier cb, Seat seatToSelect) {
		this.seatToSelect = seatToSelect;
		this.cyclicBarrier = cb;
	}
	

	@Override 
	public String call() {
		try{

			System.out.println(Thread.currentThread().getName() +
					" is waiting for "+(cyclicBarrier.getParties()-cyclicBarrier.getNumberWaiting()-1)+ 
					" other threads to reach common barrier point");
			cyclicBarrier.await();
			
		}catch(BrokenBarrierException | InterruptedException e) {
			e.printStackTrace();
			System.err.println("BOOKING USER THREAD: Broken Barrier");
		}
		
		System.out.println(Thread.currentThread().getName() + " goes on");
		
		String resultMessage = seatDAO.modifySeatState(seatToSelect.getId(), seatToSelect.getCoachID(), seatToSelect.getStatus(), seatToSelect.getSessionID(), seatToSelect.getBookedID());
		return resultMessage;
	
	}
	
	

}
