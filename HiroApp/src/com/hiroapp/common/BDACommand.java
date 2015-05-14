/**
 * Created by Jaycon Systems on 07/01/15.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */
package com.hiroapp.common;

import java.util.ArrayList;

import android.content.Context;

import com.hiroapp.dbhelper.DBHelper;
import com.hiroapp.main.HeroApp_App;
import com.hiroapp.model.OperationModel;

public class BDACommand {
	private DBHelper dbhelper;
	private HeroApp_App appStorage;
	public ArrayList<OperationModel> operationSequenceList;
	public String commandName;
	public String expectedValue;

	public BDACommand(Context mContext, String command) {
		commandName = command;
		appStorage = (HeroApp_App) mContext.getApplicationContext();
		dbhelper = appStorage.getDbhelper();
		operationSequenceList = new ArrayList<OperationModel>();
		operationQueue(command);
	}

	private void operationQueue(String command) {


		operationSequenceList = dbhelper.performOperation(command);

		for (int i = 0; i < operationSequenceList.size(); i++) {

			if (operationSequenceList.get(i).getExpectedValue() != null
					&& !operationSequenceList.get(i).getExpectedValue()
							.equalsIgnoreCase("")) {
				expectedValue = operationSequenceList.get(i).getExpectedValue();
			}

		}


	}

}
