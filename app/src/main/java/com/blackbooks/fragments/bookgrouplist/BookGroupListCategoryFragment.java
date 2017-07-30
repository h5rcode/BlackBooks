package com.blackbooks.fragments.bookgrouplist;

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
import com.blackbooks.fragments.dialogs.CategoryDeleteFragment;
import com.blackbooks.fragments.dialogs.CategoryEditFragment;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.services.BookGroupServices;
import com.blackbooks.services.CategoryServices;
import com.blackbooks.services.SummaryServices;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

import static com.blackbooks.fragments.dialogs.CategoryDeleteFragment.CategoryDeleteListener;

/**
 * A fragment to display the categories in the library.
 */
public final class BookGroupListCategoryFragment extends AbstractBookGroupListFragment implements CategoryEditFragment.CategoryEditListener, CategoryDeleteListener {

    private static final int ITEM_CATEGORY_EDIT = 1;
    private static final int ITEM_CATEGORY_DELETE = 2;
    private static final String TAG_FRAGMENT_CATEGORY_EDIT = "TAG_FRAGMENT_CATEGORY_EDIT";
    private static final String TAG_FRAGMENT_CATEGORY_DELETE = "TAG_FRAGMENT_CATEGORY_DELETE";

    @Override
    protected BookGroup.BookGroupType getBookGroupType() {
        return BookGroup.BookGroupType.CATEGORY;
    }

    @Override
    protected int getBookGroupCount(SQLiteDatabase db) {
        return SummaryServices.getCategoryCount(db);
    }

    @Override
    protected List<BookGroup> loadBookGroupList(SQLiteDatabase db, int limit, int offset) {
        return BookGroupServices.getBookGroupListCategory(db, limit, offset);
    }

    @Override
    protected String getFooterText(int displayedBookGroupCount, int totalBookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.footer_fragment_book_groups_categories, displayedBookGroupCount, displayedBookGroupCount, totalBookGroupCount);
    }

    @Override
    protected String getMoreGroupsLoadedText(int bookGroupCount) {
        Resources res = getResources();
        return res.getQuantityString(R.plurals.message_book_groups_loaded_categories, bookGroupCount, bookGroupCount);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        BookGroup bookGroup = (BookGroup) getListView().getAdapter().getItem(info.position);
        menu.setHeaderTitle(bookGroup.name);
        menu.add(Menu.NONE, ITEM_CATEGORY_EDIT, Menu.NONE, R.string.action_edit_category);
        menu.add(Menu.NONE, ITEM_CATEGORY_DELETE, Menu.NONE, R.string.action_delete_category);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean result;

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final BookGroup bookGroup = (BookGroup) getListAdapter().getItem(info.position);

        switch (item.getItemId()) {
            case ITEM_CATEGORY_EDIT:
                CategoryEditFragment categoryEditFragment = CategoryEditFragment.newInstance(bookGroup);
                categoryEditFragment.setTargetFragment(this, 0);
                categoryEditFragment.show(getFragmentManager(), TAG_FRAGMENT_CATEGORY_EDIT);
                result = true;
                break;

            case ITEM_CATEGORY_DELETE:
                CategoryDeleteFragment categoryDeleteFragment = CategoryDeleteFragment.newInstance(bookGroup);
                categoryDeleteFragment.setTargetFragment(this, 0);
                categoryDeleteFragment.show(getFragmentManager(), TAG_FRAGMENT_CATEGORY_DELETE);
                result = true;
                break;

            default:
                result = super.onContextItemSelected(item);
        }
        return result;
    }

    @Override
    public void onCategoryEdit(BookGroup bookGroup, String newName) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        CategoryServices.updateCategory(db, (Long) bookGroup.id, newName);

        String message = getString(R.string.message_category_modified, bookGroup.name);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        super.reloadBookGroups();
    }

    @Override
    public void onCategoryDeleted(BookGroup bookGroup) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        CategoryServices.deleteCategory(db, (Long) bookGroup.id);

        VariableUtils.getInstance().setReloadBookList(true);
        String message = getString(R.string.message_category_deleted, bookGroup.name);
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();

        super.removeBookGroupFromAdapter(bookGroup);
    }
}
