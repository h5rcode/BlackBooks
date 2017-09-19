package com.blackbooks.fragments.bookgrouplist;

import android.content.Context;
import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.fragments.dialogs.BookLocationDeleteFragment;
import com.blackbooks.fragments.dialogs.BookLocationEditFragment;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.repositories.BookLocationRepository;
import com.blackbooks.services.BookGroupService;
import com.blackbooks.services.BookLocationService;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A fragment to display the book locations in the library.
 */
public final class BookGroupListBookLocationFragment extends AbstractBookGroupListFragment implements BookLocationEditFragment.BookLocationEditListener, BookLocationDeleteFragment.BookLocationDeleteListener {

    private static final int ITEM_BOOK_LOCATION_EDIT = 1;
    private static final int ITEM_BOOK_LOCATION_DELETE = 2;

    private static final String TAG_FRAGMENT_BOOK_LOCATION_EDIT = "TAG_FRAGMENT_BOOK_LOCATION_EDIT";
    private static final String TAG_FRAGMENT_BOOK_LOCATION_DELETE = "TAG_FRAGMENT_BOOK_LOCATION_DELETE";

    @Inject
    BookGroupService bookGroupService;

    @Inject
    BookLocationService bookLocationService;

    @Inject
    BookLocationRepository summaryService;

    @Override
    protected BookGroup.BookGroupType getBookGroupType() {
        return BookGroup.BookGroupType.BOOK_LOCATION;
    }

    @Override
    protected int getBookGroupCount() {
        return summaryService.getBookLocationCount();
    }

    @Override
    protected List<BookGroup> loadBookGroupList(int limit, int offset) {
        return bookGroupService.getBookGroupListBookLocation(limit, offset);
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
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
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
        bookLocationService.updateBookLocation((Long) bookGroup.id, newName);

        String message = getString(R.string.message_author_modified, bookGroup.name);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        super.reloadBookGroups();
    }

    @Override
    public void onBookLocationDeleted(BookGroup bookGroup) {
        bookLocationService.deleteBookLocation((Long) bookGroup.id);

        VariableUtils.getInstance().setReloadBookList(true);
        String message = getString(R.string.message_book_location_deleted, bookGroup.name);
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();

        super.removeBookGroupFromAdapter(bookGroup);
    }
}
