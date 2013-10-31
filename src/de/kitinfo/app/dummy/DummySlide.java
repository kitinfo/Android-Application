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
	}

	@Override
	public String getTitle() {
		return "Dummy";
	}

	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public int getID() {
		return 9001;
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
	public void setID(int id) {
	}

}
