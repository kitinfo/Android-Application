package de.kitinfo.app;

import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import de.kitinfo.app.TimeManager.Updatable;
import de.kitinfo.app.dummy.DummySlide;
import de.kitinfo.app.mensa.MensaFragment;
import de.kitinfo.app.status.StatusFragment;
import de.kitinfo.app.timers.TimerViewFragment;

/**
 * The MainActivity, in charge of handling the layout of the app and stuff like
 * that,...
 * 
 * @author Indidev
 * 
 */
public class MainActivity extends FragmentActivity implements Updatable {

	private static final String TAG_INITIALIZED = "initialized";
	private static final String TAG_CURRENT_SLIDE = "currentSlide";

	boolean addVisible;

	public void setAddVisibility(boolean visible) {
		addVisible = visible;
		invalidateOptionsMenu();
	}

	public void openSettings() {
		Intent settingsIntent = new Intent(this, SettingsActivity.class);
		startActivity(settingsIntent);
	}

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	private boolean initialized;

	private static final int MSG_UPDATE = 0;
	private static final int MSG_RELOAD_CONTENT = 1;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	/**
	 * PauseHandler to handle all kind of events,... well at the moment only
	 * update events
	 */
	private final PauseHandler guiHandler = new PauseHandler() {

		public void processMessage(Message msg) {

			switch (msg.what) {
			case MSG_UPDATE:
				for (Updatable event : ReferenceManager.SLIDES) {
					if (event != null) {
						event.update();
						// Log.d("MainActivity|processMessage",
						// "updated some shit: " + event.toString());
					}
				}

				break;
			case MSG_RELOAD_CONTENT:
				for (Slide slide : ReferenceManager.SLIDES) {
					slide.updateContent(MainActivity.this);
				}
			default:
				Log.w("MainActivity/Handler", "Unknown message received: "
						+ msg.what);
			}
		}

		@Override
		protected boolean storeMessage(Message message) {
			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		addVisible = false;
		super.onCreate(savedInstanceState);
		ReferenceManager.MA = this;

		initialized = false;
		if (savedInstanceState != null)
			initialized = savedInstanceState.getBoolean(TAG_INITIALIZED, false);

		setContentView(R.layout.activity_main);

		if (!initialized) {
			addSlides();
			new UpdateTask().execute();
			initialized = true;
		}

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

		if (savedInstanceState != null)
			mViewPager.setCurrentItem(savedInstanceState.getInt(
					TAG_CURRENT_SLIDE, 0));

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				Slide curSlide = (Slide) mSectionsPagerAdapter
						.getItem(position);
				setAddVisibility(curSlide.isExpandable());
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		Slide curSlide = (Slide) mSectionsPagerAdapter.getItem(mViewPager
				.getCurrentItem());

		setAddVisibility(curSlide.isExpandable());

		Log.d("MainActivity", "Main Activity created");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ReferenceManager.TM.stopTimer();
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(TAG_INITIALIZED, initialized);
		outState.putInt(TAG_CURRENT_SLIDE, mViewPager.getCurrentItem());
	}

	private void addSlides() {

		Slide s = new TimerViewFragment();
		s.setID(1);
		ReferenceManager.addSlide(s);

		s = new StatusFragment();
		s.setID(2);
		ReferenceManager.addSlide(s);

		s = new MensaFragment();
		s.setID(3);
		ReferenceManager.addSlide(s);

		ReferenceManager.addSlide(new DummySlide());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.action_settings);
		item.setActionProvider(new ActionProvider(null) {

			@Override
			public View onCreateActionView() {
				return null;
			}

			@Override
			public boolean onPerformDefaultAction() {
				Log.d("Action", "open Settings");
				openSettings();
				return true;
			}
		});

		item = menu.findItem(R.id.action_update);
		item.setActionProvider(new ActionProvider(null) {

			@Override
			public View onCreateActionView() {
				return null;
			}

			@Override
			public boolean onPerformDefaultAction() {
				new UpdateTask().execute();
				return true;
			}
		});

		item = menu.findItem(R.id.action_add);
		item.setVisible(addVisible);
		item.setActionProvider(new ActionProvider(null) {

			@Override
			public View onCreateActionView() {
				return null;
			}

			@Override
			public boolean onPerformDefaultAction() {
				((Slide) mSectionsPagerAdapter.getItem(mViewPager
						.getCurrentItem())).addElement(MainActivity.this);
				return true;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		item.getActionProvider().onPerformDefaultAction();

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

	/**
	 * update method of this class, only notifys the gui handler, to handle the
	 * update
	 */
	public void update() {
		guiHandler.sendEmptyMessage(MSG_UPDATE);
		Log.d("MainActivity|update", "throw update message");
	}

	@Override
	public void onPause() {
		super.onPause();
		ReferenceManager.TM.stopTimer();
		guiHandler.pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		ReferenceManager.TM.startTimer();
		this.update();
		guiHandler.resume();
	}

	private class UpdateTask extends AsyncTask<Void, Void, Object> {

		@Override
		protected Object doInBackground(Void... params) {

			for (int i = 0; i < ReferenceManager.SLIDES.size(); i++) {
				ReferenceManager.SLIDES.get(i).querryData(MainActivity.this);
				publishProgress();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object x) {
			guiHandler.sendEmptyMessage(MSG_RELOAD_CONTENT);
			update();
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
