package de.kitinfo.app.timers;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import de.kitinfo.app.IOManager;
import de.kitinfo.app.R;
import de.kitinfo.app.ReferenceManager;
import de.kitinfo.app.Slide;
import de.kitinfo.app.TimeFunctions;
import de.kitinfo.app.UserDialog;

/**
 * A Slide to display events in the future, well,... they could also be in the
 * past
 * 
 * @author Indidev
 * 
 */
public class TimerViewFragment extends ListFragment implements Slide {

	private static final String API_URL = "http://api.kitinfo.de/timers/index.php";
	private final String title = "Timers";
	private List<TimerEvent> events;
	private int id;
	private boolean invalidated;

	// for context menu
	private ActionMode mActionMode;
	private MActionModeCallback mActionModeCallback;

	int selectedId;

	/**
	 * constructor and shit
	 */
	public TimerViewFragment() {
		invalidated = true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActionModeCallback = new MActionModeCallback();

		ReferenceManager.updateSlide(this);
		selectedId = -1;

		id = -1;

		if (savedInstanceState != null) {
			id = savedInstanceState.getInt("id", -1);
		}

		events = new Storage_Timer(getActivity().getApplicationContext()).getAll();

		updateList();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("id", id);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		invalidated = false;
		return v;

	}

	@Override
	public void onDestroyView() {
		invalidated = true;
		super.onDestroyView();
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
	private void updateList() {
		this.setListAdapter(new TimerListAdapter(events));
	}

	/**
	 * returns the title of this Slide
	 */
	public String getTitle() {
		return title;
	}

	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public boolean isExpandable() {
		return true;
	}

	@Override
	public void updateContent(Context context) {
		events = new Storage_Timer(context).getAll();
		updateList();
	}

	@Override
	public void querryData(Context context) {
		String timeEvents = new IOManager().queryJSON(API_URL);
		List<TimerEvent> timers = new JsonParser_TimeEvent().parse(timeEvents);

		new Storage_Timer(context).add(timers);

		Log.v("UpdateTask", "saved " + timers.size() + " Timers");
	}

	@Override
	public void addElement(Context context) {

		UserDialog dialog = new UserDialog(context);

		final View v = getActivity().getLayoutInflater().inflate(
				R.layout.timer_add, null);
		((TimePicker) v.findViewById(R.id.timer_time)).setIs24HourView(true);

		dialog.openViewDialog(getString(R.string.add_custom_Event), v,
				getString(R.string.cancel_button),
				getString(R.string.save_button), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						String title = ((EditText) v
								.findViewById(R.id.timer_event)).getText()
								.toString();
						String message = ((EditText) v
								.findViewById(R.id.timer_message)).getText()
								.toString();
						int id = Integer.MIN_VALUE;
						DatePicker date = (DatePicker) v
								.findViewById(R.id.timer_date);
						TimePicker time = (TimePicker) v
								.findViewById(R.id.timer_time);

						long fullDate = TimeFunctions.getMillis(date.getYear(),
								date.getMonth(), date.getDayOfMonth(),
								time.getCurrentHour(), time.getCurrentMinute());

						TimerEvent event = new TimerEvent(title, message, id,
								fullDate);

						Storage_Timer s = new Storage_Timer(getActivity()
								.getApplicationContext());

						s.addCustomTimer(event);

						updateContent(getActivity());
					}
				});
	}

	private class MActionModeCallback implements ActionMode.Callback {

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
			updateContent(getActivity());
		}

		public void ignoreItem() {

			// Log.d("Seleced", "Selected: " + selectedId + "," +
			// getListAdapter().getItemId(selectedId));

			new Storage_Timer(getActivity()).ignoreTimer(events.get(selectedId)
					.getID());

		}
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
			return position;
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
				time_left = TimeFunctions.formatTime(event.getRemainingTime(),
						TimeFunctions.Format.DAY_HOUR_MINUTE_SECOND);
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
}
