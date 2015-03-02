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
import com.blackbooks.fragments.dialogs.AuthorDeleteFragment;
import com.blackbooks.fragments.dialogs.AuthorEditFragment;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.services.AuthorServices;
import com.blackbooks.services.BookGroupServices;
import com.blackbooks.services.SummaryServices;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

/**
 * A fragment to display the authors in the library.
 */
public final class BookGroupListAuthorFragment extends AbstractBookGroupListFragment implements AuthorEditFragment.AuthorEditListener, AuthorDeleteFragment.AuthorDeleteListener {

    private static final int ITEM_AUTHOR_EDIT = 1;
    private static final int ITEM_AUTHOR_DELETE = 2;
    private static final String TAG_FRAGMENT_AUTHOR_DELETE = "TAG_FRAGMENT_AUTHOR_DELETE";
    private static final String TAG_FRAGMENT_AUTHOR_EDIT = "TAG_FRAGMENT_AUTHOR_EDIT";

    @Override
    protected BookGroup.BookGroupType getBookGroupType() {
        return BookGroup.BookGroupType.AUTHOR;
    }

    @Override
    protected int getBookGroupCount(SQLiteDatabase db) {
        return SummaryServices.getAuthorCount(db);
    }

    @Override
    protected List<BookGroup> loadBookGroupList(SQLiteDatabase db, int limit, int offset) {
        return BookGroupServices.getBookGroupListAuthor(db, limit, offset);
    }

    @Override
    protected String getFooterText(int displayedBookGroupCount, int totalBookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.footer_fragment_book_groups_authors, displayedBookGroupCount, displayedBookGroupCount, totalBookGroupCount);
    }

    @Override
    protected String getMoreGroupsLoadedText(int bookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.message_book_groups_loaded_authors, bookGroupCount, bookGroupCount);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        BookGroup bookGroup = (BookGroup) getListView().getAdapter().getItem(info.position);
        menu.setHeaderTitle(bookGroup.name);
        menu.add(Menu.NONE, ITEM_AUTHOR_EDIT, Menu.NONE, R.string.action_edit_author);
        menu.add(Menu.NONE, ITEM_AUTHOR_DELETE, Menu.NONE, R.string.action_delete_author);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean result;

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final BookGroup bookGroup = (BookGroup) getListAdapter().getItem(info.position);

        switch (item.getItemId()) {
            case ITEM_AUTHOR_EDIT:
                AuthorEditFragment authorEditFragment = AuthorEditFragment.newInstance(bookGroup);
                authorEditFragment.setTargetFragment(this, 0);
                authorEditFragment.show(getFragmentManager(), TAG_FRAGMENT_AUTHOR_EDIT);
                result = true;
                break;

            case ITEM_AUTHOR_DELETE:
                AuthorDeleteFragment authorDeleteFragment = AuthorDeleteFragment.newInstance(bookGroup);
                authorDeleteFragment.setTargetFragment(this, 0);
                authorDeleteFragment.show(getFragmentManager(), TAG_FRAGMENT_AUTHOR_DELETE);
                result = true;
                break;

            default:
                result = super.onContextItemSelected(item);
        }
        return result;
    }

    @Override
    public void onAuthorEdit(BookGroup bookGroup, String newName) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        AuthorServices.updateAuthor(db, (Long) bookGroup.id, newName);

        String message = getString(R.string.message_author_modifed, bookGroup.name);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        super.reloadBookGroups();
    }

    @Override
    public void onAuthorDeleted(BookGroup bookGroup) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        AuthorServices.deleteAuthor(db, (Long) bookGroup.id);

        VariableUtils.getInstance().setReloadBookList(true);
        String message = getString(R.string.message_author_deleted, bookGroup.name);
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();

        super.removeBookGroupFromAdapter(bookGroup);
    }
}
