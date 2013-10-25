package de.kitinfo.app;

import android.support.v4.app.Fragment;

public interface Slide {
	public String getTitle();

	public Fragment getFragment();

	public int getID();
}
