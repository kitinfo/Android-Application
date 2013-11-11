package de.kitinfo.app.mensa;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import de.kitinfo.app.IOManager;
import de.kitinfo.app.R;
import de.kitinfo.app.Slide;
import de.kitinfo.app.TimeFunctions;

public class MensaFragment extends ListFragment implements Slide {

	private static final String API_URL = "http://www.studentenwerk-karlsruhe.de/de/json_interface/canteen/?mensa[]=adenauerring";
	private static final String TITLE = "Mensa am Adenauerring";

	private int id;

	private List<MensaDay> mensaDays;

	public MensaFragment() {
		id = -3;
	}

	@Override
	public void update() {
		// TODO Automatisch generierter Methodenstub

	}

	@Override
	public String getTitle() {
		return TITLE;
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
		return false;
	}

	@Override
	public void addElement(Context context) {
	}

	@Override
	public void updateContent(Context context) {
		this.setListAdapter(new MensaListAdapter(mensaDays));
	}

	@Override
	public void querryData(Context context) {
		mensaDays = new JsonParser_Mensa().parse(new IOManager()
				.queryJSON(API_URL));

		new Storage_Mensa(context).add(mensaDays);
		
		
		// for (MensaDay day : mensaDays) {
		// if (day.getDateTime() == TimeFunctions.getDayInMillis(System
		// .currentTimeMillis())) {
		// Log.d("MensaFragment|querryData",
		// "Today's ("
		// + TimeFunctions.toLocalTime(day.getDateTime(),
		// SimpleDateFormat.MEDIUM,
		// SimpleDateFormat.SHORT) + ") meals:");
		//
		// for (MensaLine line : day.getLines()) {
		// Log.d("MensaFragment|querryData",
		// "Line : " + line.getName());
		//
		// for (MensaMeal meal : line.getMeals()) {
		// Log.d("MensaFragment|querryData", "Meal : " + meal);
		// }
		// }
		// }
		// }
	}

	private class MensaListAdapter implements ListAdapter {

		private MensaDay today;

		public MensaListAdapter(List<MensaDay> days) {
			today = null;
			boolean found = false;

			for (int i = 0; (i < days.size()) && !found; i++) {
				if (TimeFunctions.isSameDay(System.currentTimeMillis(), days
						.get(i).getDateTime())) {
					found = true;
					today = days.get(i);
				}
			}
		}

		@Override
		public int getCount() {
			return today.getLines().size();
		}

		@Override
		public Object getItem(int position) {
			return today.getLines().get(position);
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
						R.layout.mensa_line, null);
			}

			if (!isEmpty()) {

				MensaLine line = today.getLines().get(position);

				((TextView) element.findViewById(R.id.mensa_line_name))
						.setText(line.getName());

				LinearLayout list = (LinearLayout) element
						.findViewById(R.id.mensa_meals_list);

				list.removeAllViews();

				for (MensaMeal meal : line.getMeals()) {

					View mealView = LayoutInflater.from(parent.getContext())
							.inflate(R.layout.mensa_meal, null);

					((TextView) mealView.findViewById(R.id.mensa_meal_name))
							.setText(meal.getName());

					((TextView) mealView.findViewById(R.id.mensa_meal_hint))
							.setText(meal.getHint());

					((TextView) mealView.findViewById(R.id.mensa_meal_info))
							.setText(meal.getInfo());

					((TextView) mealView.findViewById(R.id.mensa_meal_price))
							.setText(String.format("%01.2f€", meal.getPrice()));

					list.addView(mealView);
				}
			}

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
			return (today == null) || today.getLines().isEmpty();
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
