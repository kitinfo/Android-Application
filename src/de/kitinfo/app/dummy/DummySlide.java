package de.kitinfo.app.dummy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import de.kitinfo.app.ReferenceManager;
import de.kitinfo.app.Slide;

public class DummySlide extends Fragment implements Slide {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ReferenceManager.updateSlide(this);
	}

	@Override
	public void update() {
		// TODO Automatisch generierter Methodenstub

	}

	@Override
	public String getTitle() {
		// TODO Automatisch generierter Methodenstub
		return "Dummy";
	}

	@Override
	public Fragment getFragment() {
		// TODO Automatisch generierter Methodenstub
		return this;
	}

	@Override
	public int getID() {
		// TODO Automatisch generierter Methodenstub
		return 9001;
	}

	@Override
	public boolean isExpandable() {
		// TODO Automatisch generierter Methodenstub
		return false;
	}

	@Override
	public void addElement(Context context) {
		// TODO Automatisch generierter Methodenstub

	}

}
