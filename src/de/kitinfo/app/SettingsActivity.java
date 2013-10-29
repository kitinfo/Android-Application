/**
 * 
 */
package de.kitinfo.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * @author mpease
 *
 */
public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();
	}
}
