package de.kitinfo.app;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This class provides the timing for the application, enabling it to run Update
 * actions periodically in Background
 * 
 * @author Indidev
 * 
 */
public class TimeManager implements Runnable {

	/**
	 * Flag specifying whether timer events are currently triggering update
	 * actions
	 */
	private boolean paused;

	/**
	 * System timer reference
	 */
	private ScheduledFuture<?> timer;

	/**
	 * Reference to System Thread running the timer
	 */
	private final ScheduledExecutorService scheduler;

	private List<Updatable> listeners;

	/**
	 * Create a new TimeManager instance
	 * 
	 * @param timeInMillis
	 *            Timer fire interval
	 */
	public TimeManager(long timeInMillis) {
		this.paused = true;
		listeners = new LinkedList<Updatable>();

		scheduler = Executors.newScheduledThreadPool(1);

		timer = scheduler.scheduleAtFixedRate(this, timeInMillis, timeInMillis,
				TimeUnit.MILLISECONDS);
	}

	/**
	 * The method to be called by the system when the timer interval elapses
	 */
	@Override
	public void run() {
		if (!paused) {
			for (Updatable listener : listeners) {
				listener.update();
			}
		}
	}

	/**
	 * Update the timer firing interval/update interval
	 * 
	 * @param timeInMillis
	 *            New interval length
	 */
	public void setInterval(long timeInMillis) {
		timer.cancel(false); // lrn to code cbdev, false is true
		timer = scheduler.scheduleAtFixedRate(this, timeInMillis, timeInMillis,
				TimeUnit.MILLISECONDS);
	}

	/**
	 * Enable timer events
	 */
	public void startTimer() {
		paused = false;
	}

	/**
	 * Disable timer events
	 */
	public void stopTimer() {
		paused = true;
	}

	/**
	 * Destroy the system timer instance, this is irreversible save for
	 * reinstatiation.
	 */
	public void destroyTimer() {
		timer.cancel(false);
	}

	/**
	 * register a listener to be update on an event
	 * 
	 * @param listener
	 *            listener to update
	 */
	public void register(Updatable listener) {
		listeners.add(listener);
	}

	/**
	 * unregister a listener
	 * 
	 * @param listener
	 *            TimeListener to unregister
	 */
	public void unregister(Updatable listener) {
		listeners.remove(listener);
	}

	/**
	 * ordinary interface for stuff to get updated
	 * 
	 * @author Indidev
	 * 
	 */
	public interface Updatable {

		/**
		 * update method (should be self-explaining)
		 */
		public void update();
	}

}
