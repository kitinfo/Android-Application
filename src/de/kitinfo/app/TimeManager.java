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
 * 
 *         Code reviewed against commit df7e8d90aec8d4f0505576abfe5d22914b09fdee
 *         (cbdev) Open FIXMEs Failed
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

	private List<TimeListener> listeners;

	/**
	 * Create a new TimeManager instance
	 * 
	 * @param timeInMillis
	 *            Timer fire interval
	 * @param smartApp
	 *            Reference to the controller to notify
	 */
	public TimeManager(long timeInMillis) {
		this.paused = true;
		listeners = new LinkedList<TimeListener>();

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
			for (TimeListener listener : listeners) {
				listener.update();
			}
		}
	}

	/**
	 * Update the timer firing interval/update interval
	 * 
	 * @param timeInMillis
	 *            New Interval length
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
	 * register a TimeListener to be update on an event
	 * 
	 * @param listener
	 *            listener to update
	 */
	public void register(TimeListener listener) {
		listeners.add(listener);
	}

	/**
	 * unregister a TimeListener
	 * 
	 * @param listener
	 *            TimeListener to unregister
	 */
	public void unregister(TimeListener listener) {
		listeners.remove(listener);
	}

	public interface TimeListener {
		public void update();
	}

}
