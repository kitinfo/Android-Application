package de.kitinfo.app.mensa;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MensaDay implements Comparable<MensaDay> {

	private long dateTime;
	private List<MensaLine> lines;

	public MensaDay(long dateTime) {
		lines = new LinkedList<MensaLine>();
		this.dateTime = dateTime;
	}

	public void addLine(MensaLine line) {
		this.lines.add(line);
	}

	public long getDateTime() {
		return dateTime;
	}

	public List<MensaLine> getLines() {
		Collections.sort(lines);
		return lines;
	}

	@Override
	public int compareTo(MensaDay another) {
		return (dateTime - another.getDateTime() < 0) ? -1 : 1;
	}
}
