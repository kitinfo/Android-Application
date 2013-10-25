package de.kitinfo.app.timers;

import java.util.List;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import de.kitinfo.app.R;
import de.kitinfo.app.ReferenceManager;
import de.kitinfo.app.Slide;
import de.kitinfo.app.TimeManager.TimeListener;

public class TimerViewFragment extends ListFragment implements TimeListener,
		Slide {

	private List<TimerEvent> events;
	private String jsonEvents;
	private int id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ReferenceManager.register(this);
		ReferenceManager.TVF = this;

		jsonEvents = "";
		id = 0;

		if (savedInstanceState != null) {
			jsonEvents = savedInstanceState.getString("events", "");
			id = savedInstanceState.getInt("id", 0);
		}

		events = new JsonParser_TimeEvent().parse(jsonEvents);

		updateList();
		Log.d("TimerViewFragment", "created Timer View: " + toString());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ReferenceManager.unregister(this);
		ReferenceManager.TVF = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("events", jsonEvents);
		outState.putInt("id", id);
	}

	public void setEvents(String jsonEvents) {
		this.jsonEvents = jsonEvents;
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

	public void update() {
		if ((getListAdapter() != null) && (true))
			getListView().invalidateViews();
	}

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
				element = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.timer_event, null);
			}

			TimerEvent event = events.get(position);

			((TextView) element.findViewById(R.id.time_event)).setText(event
					.getTitle());
			((TextView) element.findViewById(R.id.time_date)).setText(event
					.getDate());

			String time_left = event.getMessage();

			if (event.getRemainingTime() >= 0) {
				time_left = formatTime(event.getRemainingTime());
			}

			((TextView) element.findViewById(R.id.time_left))
					.setText(time_left);

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
		return 0;
	}

	public void setID(int id) {
		this.id = id;
	}
}
