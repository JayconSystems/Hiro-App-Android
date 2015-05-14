/**
 * Created by Jaycon Systems on 28/01/15.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */

package com.hiroapp.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hiroapp.dbhelper.DBHelper;
import com.hiroapp.font.OpenSansLight;
import com.hiroapp.font.OpenSansLightItalic;
import com.hiroapp.font.OpenSansRegular;
import com.hiroapp.main.R;

public class WifissidAdapter extends BaseAdapter {
	private ArrayList<String> listssid;
	private DBHelper dbhelper;

	private LayoutInflater layoutInflater;

	public WifissidAdapter(Context context, ArrayList<String> ssid, DBHelper db) {
		this.listssid = ssid;
		dbhelper = db;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listssid.size();
	}

	@Override
	public Object getItem(int position) {
		return listssid.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {

			convertView = layoutInflater.inflate(R.layout.custom_wifi_view,
					null);
			holder = new ViewHolder();
			holder.ssid = (OpenSansLightItalic) convertView
					.findViewById(R.id.wifisafezones_txt_currentwifi);
			holder.img = (ImageView) convertView
					.findViewById(R.id.wifisafezones_img_addwifi);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (listssid.get(position) != null
				&& !listssid.get(position).equalsIgnoreCase(""))
			holder.ssid.setText(listssid.get(position));

		holder.img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				dbhelper.removeSSID(listssid.get(position));
				listssid.remove(position);
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

	static class ViewHolder {
		OpenSansLightItalic ssid;
		ImageView img;
	}

}
