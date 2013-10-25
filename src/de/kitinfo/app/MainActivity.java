package de.kitinfo.app;

import java.util.Locale;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import de.kitinfo.app.TimeManager.TimeListener;
import de.kitinfo.app.timers.TimerViewFragment;

public class MainActivity extends FragmentActivity implements TimeListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	private TimerViewFragment timerView;

	private static final int MSG_UPDATE = 0;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private final PauseHandler guiHandler = new PauseHandler() {

		public void processMessage(Message msg) {

			switch (msg.what) {
			case MSG_UPDATE:
				Log.d("MainActivity | processMessage", "Counter: "
						+ ReferenceManager.SLIDES_TO_UPDATE.size());
				for (TimeListener event : ReferenceManager.SLIDES_TO_UPDATE) {
					if (event != null) {
						event.update();
						Log.d("MainActivity|processMessage",
								"updated some shit: " + event.toString());
					}
				}

				break;
			default:
				Log.w("MainActivity/Handler", "Unknown message received");
			}
		}

		@Override
		protected boolean storeMessage(Message message) {
			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ReferenceManager.MA = this;

		setContentView(R.layout.activity_main);

		timerView = new TimerViewFragment();
		ReferenceManager.addSlide(timerView);
		new UpdateTask().execute();

		// refresh each second
		ReferenceManager.TM = new TimeManager(1000);
		ReferenceManager.TM.register(this);
		ReferenceManager.TM.startTimer();

		// Create the adapter that will return a fragment for each of sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		// mViewPager = new ViewPager(this);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		Log.d("MainActivity", "Main Activity created");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ReferenceManager.TM.stopTimer();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			this.notifyDataSetChanged();
		}

		@Override
		public Fragment getItem(int position) {
			Log.d("MainActivity | push item on screen", ReferenceManager.SLIDES
					.get(position).toString());
			return ReferenceManager.SLIDES.get(position).getFragment();
		}

		@Override
		public int getCount() {
			return ReferenceManager.SLIDES.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			return ReferenceManager.SLIDES.get(position).getTitle()
					.toUpperCase(l);
		}
	}

	public void update() {
		guiHandler.sendEmptyMessage(MSG_UPDATE);
		Log.d("MainActivity|update", "throw update message");
	}

	@Override
	public void onPause() {
		super.onPause();
		guiHandler.pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		this.update();
		guiHandler.resume();
	}

	private class UpdateTask extends AsyncTask<Void, Void, Void> {

		String timeEvents;

		@Override
		protected Void doInBackground(Void... params) {

			timeEvents = new IOManager().queryTimeEvents();
			return null;
		}

		@Override
		protected void onPostExecute(Void post) {
			timerView.setEvents(timeEvents);
		}

		@Override
		protected void onPreExecute() {
			// Nothing TO DO
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// Nothing TO DO
		}

	}
}
