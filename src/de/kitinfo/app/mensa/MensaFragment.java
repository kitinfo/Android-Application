package de.kitinfo.app.mensa;

import android.content.Context;
import android.support.v4.app.Fragment;
import de.kitinfo.app.Slide;

public class MensaFragment extends Fragment implements Slide {

	private static final String API_URL = "http://www.studentenwerk-karlsruhe.de/de/json_interface/canteen/?mensa[]=adenauerring";
	private static final String TITLE = "Mensa am Adenauerring";

	private int id;

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
		// TODO Automatisch generierter Methodenstub

	}

	@Override
	public void querryData(Context context) {
		// TODO Automatisch generierter Methodenstub

	}
}
