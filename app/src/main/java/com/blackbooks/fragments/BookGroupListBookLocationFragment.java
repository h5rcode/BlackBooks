package com.blackbooks.fragments;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.dialogs.BookLocationDeleteFragment;
import com.blackbooks.fragments.dialogs.BookLocationEditFragment;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.services.BookGroupServices;
import com.blackbooks.services.BookLocationServices;
import com.blackbooks.services.SummaryServices;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

/**
 * A fragment to display the book locations in the library.
 */
public final class BookGroupListBookLocationFragment extends AbstractBookGroupListFragment implements BookLocationEditFragment.BookLocationEditListener, BookLocationDeleteFragment.BookLocationDeleteListener {

    private static final int ITEM_BOOK_LOCATION_EDIT = 1;
    private static final int ITEM_BOOK_LOCATION_DELETE = 2;

    private static final String TAG_FRAGMENT_BOOK_LOCATION_EDIT = "TAG_FRAGMENT_BOOK_LOCATION_EDIT";
    private static final String TAG_FRAGMENT_BOOK_LOCATION_DELETE = "TAG_FRAGMENT_BOOK_LOCATION_DELETE";

    @Override
    protected BookGroup.BookGroupType getBookGroupType() {
        return BookGroup.BookGroupType.BOOK_LOCATION;
    }

    @Override
    protected int getBookGroupCount(SQLiteDatabase db) {
        return SummaryServices.getBookLocationCount(db);
    }

    @Override
    protected List<BookGroup> loadBookGroupList(SQLiteDatabase db, int limit, int offset) {
        return BookGroupServices.getBookGroupListBookLocation(db, limit, offset);
    }

    @Override
    protected String getFooterText(int displayedBookGroupCount, int totalBookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.footer_fragment_book_groups_book_locations, displayedBookGroupCount, displayedBookGroupCount, totalBookGroupCount);
    }

    @Override
    protected String getMoreGroupsLoadedText(int bookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.message_book_groups_loaded_book_locations, bookGroupCount, bookGroupCount);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        BookGroup bookGroup = (BookGroup) getListView().getAdapter().getItem(info.position);
        menu.setHeaderTitle(bookGroup.name);
        menu.add(Menu.NONE, ITEM_BOOK_LOCATION_EDIT, Menu.NONE, R.string.action_edit_book_location);
        menu.add(Menu.NONE, ITEM_BOOK_LOCATION_DELETE, Menu.NONE, R.string.action_delete_book_location);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean result;

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final BookGroup bookGroup = (BookGroup) getListAdapter().getItem(info.position);

        switch (item.getItemId()) {
            case ITEM_BOOK_LOCATION_EDIT:
                BookLocationEditFragment bookLocationEditFragment = BookLocationEditFragment.newInstance(bookGroup);
                bookLocationEditFragment.setTargetFragment(this, 0);
                bookLocationEditFragment.show(getFragmentManager(), TAG_FRAGMENT_BOOK_LOCATION_EDIT);
                result = true;
                break;

            case ITEM_BOOK_LOCATION_DELETE:
                BookLocationDeleteFragment bookLocationDeleteFragment = BookLocationDeleteFragment.newInstance(bookGroup);
                bookLocationDeleteFragment.setTargetFragment(this, 0);
                bookLocationDeleteFragment.show(getFragmentManager(), TAG_FRAGMENT_BOOK_LOCATION_DELETE);
                result = true;
                break;

            default:
                result = super.onContextItemSelected(item);
        }
        return result;
    }

    @Override
    public void onBookLocationEdit(BookGroup bookGroup, String newName) {

        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        BookLocationServices.updateBookLocation(db, (Long) bookGroup.id, newName);

        String message = getString(R.string.message_author_modifed, bookGroup.name);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        super.reloadBookGroups();
    }

    @Override
    public void onBookLocationDeleted(BookGroup bookGroup) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        BookLocationServices.deleteBookLocation(db, (Long) bookGroup.id);

        VariableUtils.getInstance().setReloadBookList(true);
        String message = getString(R.string.message_book_location_deleted, bookGroup.name);
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();

        super.removeBookGroupFromAdapter(bookGroup);
    }
}
