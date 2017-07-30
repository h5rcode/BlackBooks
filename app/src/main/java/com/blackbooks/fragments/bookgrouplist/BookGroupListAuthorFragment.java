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
import com.blackbooks.fragments.dialogs.AuthorDeleteFragment;
import com.blackbooks.fragments.dialogs.AuthorEditFragment;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.repositories.AuthorRepository;
import com.blackbooks.services.AuthorService;
import com.blackbooks.services.BookGroupService;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A fragment to display the authors in the library.
 */
public final class BookGroupListAuthorFragment extends AbstractBookGroupListFragment implements AuthorEditFragment.AuthorEditListener, AuthorDeleteFragment.AuthorDeleteListener {

    private static final int ITEM_AUTHOR_EDIT = 1;
    private static final int ITEM_AUTHOR_DELETE = 2;
    private static final String TAG_FRAGMENT_AUTHOR_DELETE = "TAG_FRAGMENT_AUTHOR_DELETE";
    private static final String TAG_FRAGMENT_AUTHOR_EDIT = "TAG_FRAGMENT_AUTHOR_EDIT";

    @Inject
    AuthorService authorService;

    @Inject
    BookGroupService bookGroupService;

    @Inject
    AuthorRepository summaryService;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    protected BookGroup.BookGroupType getBookGroupType() {
        return BookGroup.BookGroupType.AUTHOR;
    }

    @Override
    protected int getBookGroupCount() {
        return summaryService.getAuthorCount();
    }

    @Override
    protected List<BookGroup> loadBookGroupList(int limit, int offset) {
        return bookGroupService.getBookGroupListAuthor(limit, offset);
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
        authorService.updateAuthor((Long) bookGroup.id, newName);

        String message = getString(R.string.message_author_modified, bookGroup.name);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        super.reloadBookGroups();
    }

    @Override
    public void onAuthorDeleted(BookGroup bookGroup) {
        authorService.deleteAuthor((Long) bookGroup.id);

        VariableUtils.getInstance().setReloadBookList(true);
        String message = getString(R.string.message_author_deleted, bookGroup.name);
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();

        super.removeBookGroupFromAdapter(bookGroup);
    }
}
