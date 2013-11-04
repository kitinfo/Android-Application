package de.kitinfo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * 
 * IO class, all stuff associated with io should be in here. If something is
 * missing, feel free to complete this class
 * 
 * @author Indidev
 * 
 */
public class IOManager {

	/**
	 * method to querry json data from an url
	 * 
	 * @return json data (you should post process this data!)
	 */
	public String queryJSON(URL url) {
		String data = "";
		Log.d("IOManager", "Connecting to " + url.toString());
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) url.openConnection();
			Log.d("IOManager", "connected");

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			Log.d("IOManager", "input recieved");
			data = in.readLine();
			Log.d("IOManager", "read line complete");
			if (data == null) {
				// HACK
				Log.d("IOManager", "data is null");
				data = "";
			}

			in.close();
			con.disconnect();
		} catch (IOException e) {
			Log.e("IOManager|queryJSON", e.toString());
		}

		// Log.d("ServerIOManager", "Result was " + data);

		return data;

	}
}
