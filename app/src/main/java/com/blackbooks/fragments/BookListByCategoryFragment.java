package com.blackbooks.fragments;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.BooksByCategoryAdapter;
import com.blackbooks.adapters.BooksByCategoryAdapter.CategoryItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.adapters.ListItemType;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.dialogs.CategoryDeleteFragment;
import com.blackbooks.fragments.dialogs.CategoryDeleteFragment.CategoryDeleteListener;
import com.blackbooks.fragments.dialogs.CategoryEditFragment;
import com.blackbooks.fragments.dialogs.CategoryEditFragment.CategoryEditListener;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.CategoryInfo;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.services.CategoryServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements {@link AbstractBookListFragment}. A fragment that lists books by
 * categories.
 *
 * @deprecated Replaced by BookListByCategoryFragment2.
 */
@Deprecated
public class BookListByCategoryFragment extends AbstractBookListFragment implements CategoryEditListener, CategoryDeleteListener {

    private static final String CATEGORY_DELETE_FRAGMENT_TAG = "CATEGORY_DELETE_FRAGMENT_TAG";
    private static final String CATEGORY_EDIT_FRAGMENT_TAG = "CATEGORY_EDIT_FRAGMENT_TAG";

    private static final int ITEM_CATEGORY_EDIT = 0x11;
    private static final int ITEM_CATEGORY_DELETE = 0x12;

    private String mFooterText;

    @Override
    protected String getActionBarSubtitle() {
        return getString(R.string.action_sort_by_category);
    }

    @Override
    protected String getFooterText() {
        return mFooterText;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        ListItem listItem = (ListItem) getListView().getAdapter().getItem(info.position);
        if (listItem.getListItemType() == ListItemType.HEADER) {
            CategoryItem categoryItem = (CategoryItem) listItem;
            Category category = categoryItem.getCategory();
            if (category.id != null) {
                menu.setHeaderTitle(category.name);
                menu.add(Menu.NONE, ITEM_CATEGORY_EDIT, Menu.NONE, R.string.action_edit_category);
                menu.add(Menu.NONE, ITEM_CATEGORY_DELETE, Menu.NONE, R.string.action_delete_category);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        boolean result = true;

        CategoryItem categoryItem;
        Category category;

        switch (item.getItemId()) {
            case ITEM_CATEGORY_EDIT:
                categoryItem = (CategoryItem) getListAdapter().getItem(info.position);
                category = categoryItem.getCategory();
                CategoryEditFragment editFragment = CategoryEditFragment.newInstance(category, false);
                editFragment.setTargetFragment(this, 0);
                editFragment.show(getFragmentManager(), CATEGORY_EDIT_FRAGMENT_TAG);
                break;

            case ITEM_CATEGORY_DELETE:
                categoryItem = (CategoryItem) getListAdapter().getItem(info.position);
                category = categoryItem.getCategory();
                CategoryDeleteFragment deleteFragment = CategoryDeleteFragment.newInstance(category);
                deleteFragment.setTargetFragment(this, 0);
                deleteFragment.show(getFragmentManager(), CATEGORY_DELETE_FRAGMENT_TAG);
                break;

            default:
                result = super.onContextItemSelected(item);
        }
        return result;
    }

    @Override
    public String checkNewName(Category category, String newName) {
        String errorMessage = null;
        String oldName = category.name;
        newName = newName.trim();
        if (newName.length() == 0) {
            errorMessage = getString(R.string.message_category_missing);
        } else if (oldName.equals(newName)) {
            errorMessage = getString(R.string.message_category_identical_name);
        } else {

            Category criteria = new Category();
            criteria.name = newName;

            SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
            Category categoryDb = CategoryServices.getCategoryByCriteria(db, criteria);

            if (categoryDb != null) {
                errorMessage = getString(R.string.message_category_already_exists);
            }

            super.loadData();
        }
        return errorMessage;
    }

    @Override
    public void onCategoryEdit(Category category, String newName) {
        String oldName = category.name;
        if (!oldName.equals(newName)) {
            category.name = newName;

            SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();

            CategoryServices.saveCategory(db, category);

            String message = getString(R.string.message_category_modified);
            message = String.format(message, oldName, newName);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

            super.loadData();
        }
    }

    @Override
    public void onCategoryDeleted(Category category) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        CategoryServices.deleteCategory(db, category.id);
        String text = getString(R.string.message_category_deleted);
        text = String.format(text, category.name);
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();

        super.loadData();
    }

    @Override
    protected ArrayAdapter<ListItem> getBookListAdapter() {
        return new BooksByCategoryAdapter(getActivity());
    }

    @Override
    protected List<ListItem> loadBookList() {

        SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
        List<CategoryInfo> categoryList = CategoryServices.getCategoryInfoList(db);

        int categoryCount = 0;
        List<Long> bookIdList = new ArrayList<Long>();
        List<ListItem> listItems = new ArrayList<ListItem>();
        for (CategoryInfo categoryInfo : categoryList) {
            if (categoryInfo.id == null) {
                categoryInfo.name = getString(R.string.label_unspecified_category);
            } else {
                categoryCount++;
            }
            CategoryItem categoryItem = new CategoryItem(categoryInfo);
            listItems.add(categoryItem);
            for (BookInfo book : categoryInfo.books) {
                BookItem bookItem = new BookItem(book);
                listItems.add(bookItem);

                if (!bookIdList.contains(book.id)) {
                    bookIdList.add(book.id);
                }
            }
        }

        Resources res = getResources();
        int bookCount = bookIdList.size();
        String categories = res.getQuantityString(R.plurals.label_footer_categories, categoryCount, categoryCount);
        String books = res.getQuantityString(R.plurals.label_footer_books, bookCount, bookCount);

        mFooterText = getString(R.string.footer_fragment_books_by_category, categories, books);

        return listItems;
    }
}
