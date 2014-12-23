package com.blackbooks.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackbooks.R;

/**
 * Adapter used to render the items of the navigation drawer.
 */
public final class DrawerAdapter extends ArrayAdapter<DrawerItem> {

	private final LayoutInflater mInflater;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context.
	 * @param objects
	 *            List of drawer items.
	 */
	public DrawerAdapter(Context context, List<DrawerItem> objects) {
		super(context, 0, objects);
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		DrawerItem drawerItem = getItem(position);
		DrawerItemType drawerItemType = drawerItem.getDrawerItemType();

		if (drawerItemType == DrawerItemType.GROUP) {
			view = mInflater.inflate(R.layout.drawer_group, parent, false);
			TextView textTitle = (TextView) view.findViewById(R.id.drawerGroup_title);
			textTitle.setText(drawerItem.getTitle());
		} else if (drawerItemType == DrawerItemType.ITEM) {
			view = mInflater.inflate(R.layout.drawer_item, parent, false);

			ImageView imageIcon = (ImageView) view.findViewById(R.id.drawerItem_icon);
			Integer iconId = drawerItem.getIconId();
			if (iconId != null) {
				imageIcon.setImageResource(iconId);
			}
			TextView textTitle = (TextView) view.findViewById(R.id.drawerItem_title);
			textTitle.setText(drawerItem.getTitle());
		}

		return view;
	}
}
