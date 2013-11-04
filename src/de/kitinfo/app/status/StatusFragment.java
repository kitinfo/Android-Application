package de.kitinfo.app.status;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.kitinfo.app.IOManager;
import de.kitinfo.app.R;
import de.kitinfo.app.ReferenceManager;
import de.kitinfo.app.Slide;

public class StatusFragment extends Fragment implements Slide {

	private static final String NICK_LIST_URL = "http://api.kitinfo.de/channel/users/index.php";
	private static final String LAST_MESSAGE_URL = "http://api.kitinfo.de/channel/last/index.php";
	private static final String NICK_COUNT_URL = "http://api.kitinfo.de/channel/count/index.php";
	private static final String TITLE = "Status";
	private int id;
	private boolean invalidated;
	private String nickList;
	private String lastMessage;
	private String nickCount;

	public StatusFragment() {
		invalidated = true;
		this.id = -2;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ReferenceManager.updateSlide(this);

		if (savedInstanceState != null)
			id = savedInstanceState.getInt("id", -2);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.status, null);
		((TextView) v.findViewById(R.id.status_user_online)).setText(nickList);
		((TextView) v.findViewById(R.id.status_activity)).setText(nickCount);
		((TextView) v.findViewById(R.id.status_last_message))
				.setText(lastMessage);
		invalidated = false;
		return v;
	}

	@Override
	public void onDestroyView() {
		invalidated = true;
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		invalidated = true;
		super.onDestroy();
		ReferenceManager.TVF = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("id", id);
	}

	@Override
	public void update() {
		if (!invalidated) {
			View v = getView();

			((TextView) v.findViewById(R.id.status_user_online))
					.setText(nickList);
			((TextView) v.findViewById(R.id.status_activity))
					.setText(nickCount);
			((TextView) v.findViewById(R.id.status_last_message))
					.setText(lastMessage);

			v.invalidate();
		}
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
	}

	@Override
	public void querryData(Context context) {
		try {
			nickCount = new IOManager().queryJSON(new URL(NICK_COUNT_URL));
			lastMessage = new IOManager().queryJSON(new URL(LAST_MESSAGE_URL));
			String nicks = new IOManager().queryJSON(new URL(NICK_LIST_URL));

			nickList = "";
			for (String nick : new JsonParser_Status().parseNames(nicks)) {
				nickList += "\n" + nick;
			}

			nickList = nickList.trim();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
