package de.kitinfo.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class IOManager {

	public String queryTimeEvents() {
		String data = "";

		try {

			URL url = new URL("http://timers.kitinfo.de/timerapi.php");
			Log.d("IOManager", "Connecting to " + url.toString());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
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

			// Log.d("ServerIOManager","Result was "+dataCrap);
		} catch (Exception e) {
			Log.d("ServerIOManager", "Could not connect (" + e.toString() + ")");

		}

		return data;

	}
}
