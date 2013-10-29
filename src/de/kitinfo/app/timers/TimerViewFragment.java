package de.kitinfo.app.timers;

import java.util.List;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import de.kitinfo.app.R;
import de.kitinfo.app.ReferenceManager;
import de.kitinfo.app.Slide;
import de.kitinfo.app.data.Storage;

/**
 * A Slide to display events in the future, well,... they could also be in the
 * past
 * 
 * @author Indidev
 * 
 */
public class TimerViewFragment extends ListFragment implements Slide {

	private List<TimerEvent> events;
	private int id;
	private boolean invalidated;

	// for context menu
	private ActionMode mActionMode;

	int selectedId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		invalidated = true;
		ReferenceManager.TVF = this;
		ReferenceManager.updateSlide(this);
		selectedId = -1;

		id = -1;

		if (savedInstanceState != null) {
			id = savedInstanceState.getInt("id", -1);
		}

		events = new Storage(getActivity().getApplicationContext()).getTimers();

		updateList();
		invalidated = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		invalidated = true;
		ReferenceManager.TVF = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("id", id);
	}

	/**
	 * get some events on this frame, this should be called, if you want to set
	 * events the first time
	 * 
	 * @param jsonEvents
	 *            events in JSON format
	 */
	public void setEvents(String jsonEvents) {
		this.setEvents(new JsonParser_TimeEvent().parse(jsonEvents));
	}

	public void setEvents(List<TimerEvent> events) {

		this.events = events;
		updateList();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	/**
	 * invalidate all views, so they will be updated soon (at least i hope so)
	 */
	public void update() {
		if (!invalidated && (getListAdapter() != null))
			getListView().invalidateViews();
	}

	/**
	 * updates the whole list, should be used when new events occured
	 * (attention, resets the scroll amount)
	 */
	public void updateList() {
		this.setListAdapter(new TimerListAdapter(events));

	}

	/**
	 * format the given time to a readable string
	 * 
	 * @param time
	 *            time in seconds
	 * @return time string: (dd:hh:mm:ss)
	 */
	public String formatTime(long time) {
		long day = time / 86400;
		long hour = (time % 86400) / 3600;
		long minute = (time % 3600) / 60;
		long second = time % 60;

		// return day + ":" + hour + ":" + minute + ":" + second;
		return String.format("%02d:%02d:%02d:%02d", day, hour, minute, second);
		
	}

	/**
	 * returns the title of this Slide
	 */
	public String getTitle() {
		return "Timers";
	}

	private class TimerListAdapter implements ListAdapter {

		private List<TimerEvent> events;

		public TimerListAdapter(List<TimerEvent> events) {
			this.events = events;
		}

		@Override
		public int getCount() {
			return events.size();
		}

		@Override
		public Object getItem(int position) {
			return events.get(position);
		}

		@Override
		public long getItemId(int position) {
			return events.get(position).getID();
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View element;

			if (convertView != null) {
				element = convertView; // reusing old view
			} else {
				// inflate new view
				element = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.timer_event, null);
			}

			// initialize the view with the events parameters

			TimerEvent event = events.get(position);

			((TextView) element.findViewById(R.id.time_event)).setText(event
					.getTitle());
			((TextView) element.findViewById(R.id.time_date)).setText(event
					.getDate());

			String time_left = event.getMessage();

			// if event isn't in the past, set the right remaining time else its
			// message will be displayed
			if (event.getRemainingTime() >= 0) {
				time_left = formatTime(event.getRemainingTime());
			}

			((TextView) element.findViewById(R.id.time_left))
					.setText(time_left);
			element.setId(position);
			element.setOnLongClickListener(new View.OnLongClickListener() {

				// Called when the user long-clicks on someView
				public boolean onLongClick(View view) {
					if (mActionMode != null) {
						return false;
					}
					selectedId = view.getId();
					view.setSelected(true);

					// Start the CAB using the ActionMode.Callback defined above
					mActionMode = getActivity().startActionMode(
							mActionModeCallback);

					return true;
				}
			});

			return element;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isEmpty() {
			return events.isEmpty();
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}

	}

	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public int getID() {
		return id;
	}

	/**
	 * set the id of this fragment (should be setted if you want to display
	 * another slide of this class)
	 * 
	 * @param id
	 *            id for this slide
	 */
	public void setID(int id) {
		this.id = id;
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.timer_context_menu, menu);
			return true;
		}

		// Called each time the action mode is shown. Always called after
		// onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.ignore_item:
				ignoreItem();
				mode.finish(); // Action picked, so close the CAB
				return true;
			default:
				return false;
			}
		}

		// Called when the user exits the action mode
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			checkIgnoreList();
		}

		public void ignoreItem() {

			// Log.d("Seleced", "Selected: " + selectedId + "," +
			// getListAdapter().getItemId(selectedId));

			new Storage(getActivity().getApplicationContext())
					.ignoreTimer((int) getListAdapter().getItemId(selectedId));

		}
	};

	public void checkIgnoreList() {
		setEvents(new Storage(getActivity().getApplicationContext())
				.getTimers());
	}

}
