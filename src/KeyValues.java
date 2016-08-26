package com.abhinav.qcards;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

// TODO: Auto-generated Javadoc
/**
 * The Class KeyValues.
 */
public class KeyValues {

	/** The app context. */
	private Context appContext;
	
	/** The pref. */
	private SharedPreferences pref;
	
	/** The editor. */
	private Editor editor;

	/**
	 * Instantiates a new key values.
	 *
	 * @param context the context
	 * @param prefName the SharedPreference name
	 */
	public KeyValues(Context context, String prefName) {
		this.appContext = context;
		this.pref = appContext.getSharedPreferences(prefName, 0);
		this.editor = pref.edit();
	}
	
	/**
	 * Clear all the preference values
	 */
	public void clearAll(){
		editor.clear();
		editor.commit();
	}

	
	/**
	 * Gets the attendance roster.
	 *
	 * @return the attendance roster
	 */
	public String getAttendanceRoster() {
		return pref.getString("attendance_current", null);
	}

	/**
	 * Gets the attendance bool.
	 *
	 * @return the attendance bool
	 */
	public boolean getAttendanceBool() {
		return pref.getBoolean("attendance_bool", false);
	}

	/**
	 * Sets the attendance bool.
	 *
	 * @param bool the new attendance bool
	 */
	public void setAttendanceBool(Boolean bool) {
		editor.putBoolean("attendance_bool", bool);
		editor.commit();
	}

	/**
	 * Sets the attendance roster.
	 *
	 * @param attRoster the new attendance roster
	 */
	public void setAttendanceRoster(String attRoster) {
		editor.putString("attendance_current", attRoster);
		editor.commit();
	}

	// /////////////////////////////////////////////////////////////////////////////////////

	// ////////////////// getters and setters for Roster
	/**
	 * Gets the roster.
	 *
	 * @return the roster
	 */
	public String getRoster() {
		return pref.getString("roster_current", null);
	}

	/**
	 * Gets the roster bool.
	 *
	 * @return the roster bool
	 */
	public boolean getRosterBool() {
		return pref.getBoolean("roster_bool", false);
	}

	/**
	 * Sets the roster.
	 *
	 * @param roster the new roster
	 */
	public void setRoster(String roster) {
		editor.putString("roster_current", roster);
		editor.commit();
	}

	/**
	 * Sets the roster bool.
	 *
	 * @param bool the new roster bool
	 */
	public void setRosterBool(Boolean bool) {
		editor.putBoolean("roster_bool", bool);
		editor.commit();
	}

	// /////////////////////////////////////////////////////////////////////////////////////


	// //////////////// getters and setters for Attendance Choice
	/**
	 * Gets the current qset name.
	 *
	 * @return the qset
	 */
	public String getQSet() {
		return pref.getString("qset_current", null);
	}

	/**
	 * Gets the qset_bool.
	 *
	 * @return the qset_bool
	 */
	public boolean getQSetBool() {
		return pref.getBoolean("qset_bool", false);
	}

	/**
	 * Sets the current qset.
	 *
	 * @param qset the new qset
	 */
	public void setQSet(String qset) {
		editor.putString("qset_current", qset);
		editor.commit();
	}

	/**
	 * Sets the qset_bool.
	 *
	 * @param bool the new qset_bool
	 */
	public void setQSetBool(Boolean bool) {
		editor.putBoolean("qset_bool", bool);
		editor.commit();
	}
	// /////////////////////////////////////////////////////////////////////////////////////

	// //////////////// getters and setters for ManageRoster
	/**
	 * Gets the manage_roster bool.
	 *
	 * @return the manage_roster bool
	 */
	public Boolean getManageBool() {
		return pref.getBoolean("manage_roster", false);
	}

	/**
	 * Sets the manage_roster bool.
	 *
	 * @param bool the new manage_roster bool
	 */
	public void setManageBool(Boolean bool) {
		editor.putBoolean("manage_roster", bool);
		editor.commit();
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the roster_attendance bool.
	 *
	 * @param bool the new roster_attendance bool
	 */
	public void setRosterAttendanceBool(Boolean bool){
		editor.putBoolean("roster_attendance", bool);
		editor.commit();
	}
	
	/**
	 * Gets the roster_attendance bool.
	 *
	 * @return the roster_attendance bool
	 */
	public Boolean getRosterAttendanceBool(){
		return pref.getBoolean("roster_attendance", false);
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the roster_quiz bool.
	 *
	 * @param bool the new roster_quiz bool
	 */
	public void setRosterQuizBool(Boolean bool){
		editor.putBoolean("roster_quiz", bool);
		editor.commit();
	}
	
	/**
	 * Gets the roster_quiz bool
	 *
	 * @return the roster_quiz bool
	 */
	public Boolean getRosterQuizBool(){
		return pref.getBoolean("roster_quiz", false);
	}
}
