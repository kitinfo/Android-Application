package de.kitinfo.app.mensa;

import java.util.List;

public class MensaDay {

	private long dateTime;
	private List<MensaLine> lines;

	public MensaDay(long dateTime) {
		this.dateTime = dateTime;
	}

	public void addLine(MensaLine line) {
		this.lines.add(line);
	}

	public long getDateTime() {
		return dateTime;
	}

	public List<MensaLine> getLines() {
		return lines;
	}
}
