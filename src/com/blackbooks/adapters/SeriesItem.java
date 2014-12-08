package com.blackbooks.adapters;

import com.blackbooks.model.persistent.Series;

public class SeriesItem implements ListItem {
	
	private final Series mSeries;
	
	public SeriesItem(Series series) {
		mSeries = series;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.Header2;
	}
	
	public Series getSeries() {
		return mSeries;
	}
}
