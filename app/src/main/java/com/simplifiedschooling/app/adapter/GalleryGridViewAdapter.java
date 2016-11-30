package com.simplifiedschooling.app.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.simplifiedschooling.app.R;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.Constant;

public class GalleryGridViewAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private List<String> movieItems;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public GalleryGridViewAdapter(Activity activity, List<String> movieItems) {
		this.activity = activity;
		this.movieItems = movieItems;
	}

	@Override
	public int getCount() {
		return movieItems.size();
	}

	@Override
	public Object getItem(int location) {
		return movieItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.grid_child, null);

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();
		   NetworkImageView thumbNail = (NetworkImageView) convertView
				.findViewById(R.id.thumbnail);
		   TextView title = (TextView) convertView
				.findViewById(R.id.textViewStudentName);

		// getting movie data for the row
		

		// thumbnail image
		  thumbNail.setImageUrl(AppConfig.CLIENT_URL+"../"+"gallery/"+Constant.ImagePath.get(position), imageLoader);

		// title
		  title.setText(movieItems.get(position));

		return convertView;
	}

}