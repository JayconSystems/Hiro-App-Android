/**
 * Created by Jaycon Systems on 24/12/14.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */

package com.hiroapp.main;

import android.app.Application;

import com.hiroapp.dbhelper.DBHelper;

public class HeroApp_App extends Application {

	/**
	 * Database Helper class to Access the database.
	 */
	private DBHelper dbhelper;

	/**
	 * @return the dbhelper
	 */
	public DBHelper getDbhelper() {
		return dbhelper;
	}

	/**
	 * @param dbhelper
	 *            the dbhelper to set
	 */
	public void setDbhelper(DBHelper dbhelper) {
		this.dbhelper = dbhelper;
	}

	/**
	 * Called when the application class is called. and it create and open
	 * database.
	 */

	public void onCreate() {
		super.onCreate();
		dbhelper = new DBHelper(this);
		try {

			// create and open database.
			dbhelper.createDataBase();
			dbhelper.openDataBase();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method is called when terminate the Application class.
	 */
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		if (dbhelper != null)
			dbhelper.close();
	}

}
