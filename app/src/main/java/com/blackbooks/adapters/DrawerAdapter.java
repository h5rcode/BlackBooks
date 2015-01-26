package com.blackbooks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.adapters.DrawerAdapter.DrawerItem;

import java.util.List;

/**
 * Adapter used to render the items of the navigation drawer.
 */
public final class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    private final LayoutInflater mInflater;

    /**
     * Constructor.
     *
     * @param context Context.
     * @param objects List of drawer items.
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

    /**
     * Type of drawer item.
     */
    public static enum DrawerItemType {

        /**
         * Group.
         */
        GROUP,

        /**
         * Item.
         */
        ITEM

    }

    /**
     * A drawer item.
     */
    public static final class DrawerItem {

        private final int mId;
        private final String mTitle;
        private final Integer mIconId;
        private final DrawerItemType mDrawerItemType;

        /**
         * Constructor.
         *
         * @param id             Id of the drawer item.
         * @param title          Title.
         * @param iconId         Id of the drawable resource representing the icon of the
         *                       item.
         * @param drawerItemType Type of item.
         */
        public DrawerItem(int id, String title, Integer iconId, DrawerItemType drawerItemType) {
            this.mId = id;
            this.mTitle = title;
            this.mIconId = iconId;
            this.mDrawerItemType = drawerItemType;
        }

        /**
         * Return the id of the item.
         *
         * @return Id.
         */
        public int getId() {
            return mId;
        }

        /**
         * Return the tile of the item.
         *
         * @return Title.
         */
        public String getTitle() {
            return mTitle;
        }

        /**
         * Return the id of the draweble resource corresponding to the icon of
         * the item.
         *
         * @return Id of the drawable.
         */
        public Integer getIconId() {
            return mIconId;
        }

        /**
         * Return the type of item.
         *
         * @return DrawerItemType.
         */
        public DrawerItemType getDrawerItemType() {
            return mDrawerItemType;
        }
    }
}
