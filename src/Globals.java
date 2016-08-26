package com.abhinav.qcards;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

public class Globals extends Application {

	private String loadedRoster;
	private String loadedQSet;
	private List<QSetCards> loadedQList;
	private String loadedAttendance;

	public String getLoadedRoster() {
		return loadedRoster;
	}

	public String getLoadedQSet() {
		return loadedQSet;
	}

	public String getLoadedAttendance() {
		return loadedAttendance;
	}
	
	public List<QSetCards> getLoadedQList() {
		return loadedQList;
	}
	
	public void setLoadedQList(List<QSetCards> qSetList) {
		loadedQList = qSetList;
	}

	public void setLoadedRoster(String rosterName) {
		this.loadedRoster = rosterName;
	}

	public void setLoadedQSet(String qSetName) {
		this.loadedQSet = qSetName;
	}

	public void setLoadedAttendance(String attendanceName) {
		this.loadedAttendance = attendanceName;
	}
}
