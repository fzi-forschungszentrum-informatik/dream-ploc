package de.fzi.dream.ploc.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationView;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.Collection;
import de.fzi.dream.ploc.data.structure.entity.ExpertProfile;
import de.fzi.dream.ploc.databinding.NavDrawerMainHeaderBinding;
import de.fzi.dream.ploc.ui.dialog.AddCollectionDialog;
import de.fzi.dream.ploc.ui.dialog.EditCollectionDialog;
import de.fzi.dream.ploc.ui.fragment.ExpertPreviewFragment;
import de.fzi.dream.ploc.ui.fragment.FeedbackSignInFragment;
import de.fzi.dream.ploc.ui.fragment.FeedbackPreviewFragment;
import de.fzi.dream.ploc.ui.fragment.RecordPreviewFragment;
import de.fzi.dream.ploc.viewmodel.ProfileViewModel;

import static de.fzi.dream.ploc.utility.Constants.EXTRA_FILTER_ID;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_FILTER_NAME;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_ID;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_IS_BOOKMARK;

/**
 * Main Activity, responsible for handling the fragment callbacks, the navigation of
 * of the bottom navigation bar and the navigation drawer.
 *
 * @author Felix Melcher
 */
public class MainActivity extends AppCompatActivity
        implements
        EditCollectionDialog.OnInteractionListener,
        AddCollectionDialog.OnInteractionListener,
        RecordPreviewFragment.OnInteractionListener,
        ExpertPreviewFragment.OnInteractionListener,
        FeedbackPreviewFragment.OnInteractionListener,
        FeedbackSignInFragment.OnInteractionListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    /**
     * Public class identifier tag
     */
    public static final String TAG = MainActivity.class.getSimpleName();

    // Toolbar
    private Toolbar mToolbar;
    private ActionBar mActionBar;

    // Navigation
    private NavigationView mNavigationView;
    private BottomNavigationView mBottomNavigationView;
    private Menu mCollectionSubMenu;
    private int mLastVisitedMainView;

    // ViewModel and DataBinding
    private ProfileViewModel mViewModel;
    private boolean mIsExpert = false;
    private NavDrawerMainHeaderBinding mBinding;

    /*
     * Android Lifecycle Methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_main_container);
        setUpToolbar();
        setUpNavigationDrawer();
        setUpViewModel();
        setUpRecordFeedFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_toolbar, menu);
        setUpSearchView(menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mNavigationView.setCheckedItem(mLastVisitedMainView);
    }

    /*
     * Class Methods
     */

    /**
     * Initialize the {@link ActionBar}  as a {@link Toolbar} and reference a class variable to the
     * ActionBar.
     */
    public void setUpToolbar() {
        mToolbar = findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
    }

    /**
     * Initialize the {@link NavigationView}  containing a NavigationDrawer and a
     * {@link BottomNavigationView}, the method also triggers the creation of the Collection submenu
     * within the NavigationDrawer.
     */
    public void setUpNavigationDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView = findViewById(R.id.navigation_view_main);
        mCollectionSubMenu = mNavigationView.getMenu().getItem(2).getSubMenu();
        mBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.nav_drawer_main_header, mNavigationView, false);
        mNavigationView.addHeaderView(mBinding.getRoot());
        mNavigationView.setNavigationItemSelectedListener(this);
        mBottomNavigationView = findViewById(R.id.bottom_navigation_main_activity);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        addCreateCollectionItemToDrawer();
    }

    /**
     * Initialize the expandable {@link SearchView} and add it to the OptionsMenu within in the
     * {@link ActionBar}.
     *
     * @param menu the inflated menu where the search view should be embedded.
     */
    public void setUpSearchView(Menu menu) {
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo((((SearchManager) getSystemService(Context.SEARCH_SERVICE)).getSearchableInfo(getComponentName())));
        searchView.setIconifiedByDefault(false);
    }

    /**
     * Initialize and add the create collection button to the NavigationDrawer and register
     * ClickListener to open the corresponding {@link AddCollectionDialog} view.
     */
    public void addCreateCollectionItemToDrawer() {
        MenuItem addCollectionItem = mCollectionSubMenu.add(R.id.collections_group,
                Menu.NONE, Menu.FLAG_APPEND_TO_GROUP, null).setActionView(R.layout.action_view_add_collection);
        addCollectionItem.setEnabled(false);
        addCollectionItem.getActionView().findViewById(R.id.button_add_collection_nav_drawer)
                .setOnClickListener(menuItem -> {
                    FragmentManager manager = getSupportFragmentManager();
                    Fragment fragment = manager.findFragmentByTag(AddCollectionDialog.TAG);
                    if (fragment != null) {
                        manager.beginTransaction().remove(fragment).commit();
                    }
                    AddCollectionDialog addCollectionDialog = new AddCollectionDialog(this);
                    addCollectionDialog.show(manager, AddCollectionDialog.TAG);
                });
    }

    /**
     * Initialize the user related collection menu items within the NavigationDrawer and register
     * ClickListener to open the corresponding collection filter view. This function is called for
     * each collection in the observeCollections SUCCESS case.
     */
    public void addCollectionItemToDrawer(Menu subMenu, Collection collection) {
        MenuItem collectionItem = subMenu
                .add(R.id.collections_group, Menu.NONE, Menu.FLAG_APPEND_TO_GROUP, null)
                .setActionView(R.layout.action_view_collection_chip)
                .setEnabled(false);
        View newActionView = collectionItem.getActionView();
        ImageView iconButton = newActionView.findViewById(R.id.image_view_edit_collection_nav_drawer);
        iconButton.setClickable(true);
        iconButton.setOnClickListener(v -> {
            FragmentManager manager = getSupportFragmentManager();
            Fragment frag = manager.findFragmentByTag("fragment_edit_collection");
            if (frag != null) {
                manager.beginTransaction().remove(frag).commit();
            }
            EditCollectionDialog editCollectionDialog = new EditCollectionDialog();
            editCollectionDialog.setCollection(collection);
            editCollectionDialog.setListener(this);
            editCollectionDialog.show(manager, "fragment_edit_collection");
        });
        Chip chip = newActionView.findViewById(R.id.chip_collection_nav_drawer);
        chip.setOnClickListener(v -> startActivity(new Intent(v.getContext(), RecordBookmarkActivity.class)
                .putExtra(EXTRA_FILTER_ID, collection.getId())
                .putExtra(EXTRA_FILTER_NAME, collection.getName())));
        chip.setText(collection.getName());
    }

    /**
     * Initialize the view model related to this view and Start observing the necessary data.
     */
    public void setUpViewModel() {
        mViewModel = obtainViewModel();
        observeCollections();
        observeExpertProfile();
    }

    /**
     * Observe changes to the user related collection data set, and create the view after a change
     * occurs.
     */
    public void observeCollections() {
        mViewModel.getCollections().observe(this, collections -> {
            if (collections != null) {
                Log.d(TAG, "onChanged: status: " + collections.status);
                if (collections.data != null) {
                    switch (collections.status) {
                        case LOADING: {
                            break;
                        }
                        case ERROR: {
                            Log.e(TAG, "onChanged: cannot refresh the cache.");
                            Log.e(TAG, "onChanged: ERROR message: " + collections.message);
                            Log.e(TAG, "onChanged: status: ERROR,: " + collections.data.size());
//                              if(collections.message.equals(QUERY_EXHAUSTED)){
//                                    mAdapter.setQueryExhausted();
//                              }
                            break;
                        }
                        case SUCCESS: {
                            mCollectionSubMenu.clear();
                            addCreateCollectionItemToDrawer();
                            if (collections.data.size() > 0) {
                                collections.data.forEach(collection -> addCollectionItemToDrawer(mCollectionSubMenu, collection));
                            }
                            Log.d(TAG, "onChanged: cache has been refreshed.");
                            Log.d(TAG, "onChanged: status: SUCCESS, : " + collections.data.size());
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Observe changes to the user related expert profile and update the data binding of the view
     * after a change occurs.
     */
    public void observeExpertProfile() {
        mViewModel.readExpertProfile().observe(this, expertProfile -> {
            if (expertProfile != null) {
                mIsExpert = true;
                mBinding.setExpertProfile(expertProfile);
            } else {
                mBinding.setExpertProfile(new ExpertProfile("None"));
            }
        });
    }

    /**
     * Helper method to change the fragment after a menu item in the NavigationDrawer
     * or the BottomNavigationView is clicked.
     */
    public boolean switchView(int itemID) {
        switch (itemID) {
            case R.id.nav_drawer_publications:
            case R.id.nav_bottom_publications:
                if (itemID == R.id.nav_bottom_publications)
                    setActiveDrawerItem(R.id.nav_drawer_publications);
                if (itemID == R.id.nav_drawer_publications)
                    setActiveBottomItem(R.id.nav_bottom_publications);
                setUpRecordFeedFragment();
                break;
            case R.id.nav_bottom_reviews:
            case R.id.nav_drawer_reviews:
                if (itemID == R.id.nav_bottom_reviews) setActiveDrawerItem(R.id.nav_drawer_reviews);
                if (itemID == R.id.nav_drawer_reviews) setActiveBottomItem(R.id.nav_bottom_reviews);
                if (mIsExpert) {
                    mActionBar.setTitle(R.string.title_activity_feedback);
                    commitFragment(new FeedbackPreviewFragment(), FeedbackPreviewFragment.TAG);
                } else {
                    mActionBar.setTitle(R.string.title_activity_orcid);
                    commitFragment(FeedbackSignInFragment.newInstance(), FeedbackSignInFragment.TAG);
                }
                break;
            case R.id.nav_bottom_experts:
            case R.id.nav_drawer_experts:
                mActionBar.setTitle(R.string.title_activity_experts);
                if (itemID == R.id.nav_bottom_experts) setActiveDrawerItem(R.id.nav_drawer_experts);
                if (itemID == R.id.nav_drawer_experts) setActiveBottomItem(R.id.nav_bottom_experts);
                commitFragment(new ExpertPreviewFragment(), ExpertPreviewFragment.TAG);
                break;
            case R.id.nav_drawer_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.nav_drawer_interests:
                startActivity(new Intent(this, InterestsActivity.class));
                break;
            case R.id.nav_drawer_saved_experts:
                startActivity(new Intent(this, ExpertBookmarkActivity.class));
                break;
            case R.id.nav_drawer_saved_publications:
                startActivity(new Intent(this, RecordBookmarkActivity.class));
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Helper method to create the standard start screen with the publication feed.
     */
    private void setUpRecordFeedFragment() {
        setActiveDrawerItem(R.id.nav_drawer_publications);
        mActionBar.setTitle(R.string.nav_item_publications);
        commitFragment(RecordPreviewFragment.newInstance(), RecordPreviewFragment.TAG);
    }

    /**
     * Helper method to commit fragments to the main view container.
     */
    public void commitFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_main_activity, fragment, tag).commit();
    }

    /**
     * Helper method to highlight the current active navigation drawer item.
     */
    public void setActiveDrawerItem(int drawerItem) {
        mLastVisitedMainView = drawerItem;
        mNavigationView.setCheckedItem(drawerItem);
    }

    /**
     * Helper method to highlight the current active bottom navigation view item.
     */
    public void setActiveBottomItem(int bottomItem) {
        mBottomNavigationView.setSelectedItemId(bottomItem);
    }

    /**
     * Helper method to get the lifecycle bound {@link androidx.lifecycle.ViewModel}, for data
     * retrieval and manipulation.
     */
    private ProfileViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    /*
     * Interface Methods
     */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_refresh:
            case R.id.action_filter:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return switchView(item.getItemId());
    }

    @Override
    public void onFinishAddCollectionDialog(String collectionName) {
        mViewModel.setCollection(collectionName);
    }

    @Override
    public void onFinishEditCollectionDialog(Collection collection) {
        mViewModel.editCollection(collection);
    }

    @Override
    public void onDeleteCollection(Collection collection) {
        mViewModel.deleteCollection(collection);
    }

    @Override
    public void onRecordPreviewClick(int id, boolean isBookmarked) {
        startActivity(new Intent(this, RecordDetailActivity.class)
                .putExtra(EXTRA_ID, id)
                .putExtra(EXTRA_IS_BOOKMARK, isBookmarked));
    }

    @Override
    public void onExpertPreviewClicked(int id, boolean isBookmarked) {
        startActivity(new Intent(this, ExpertDetailActivity.class)
                .putExtra(EXTRA_ID, id)
                .putExtra(EXTRA_IS_BOOKMARK, isBookmarked));
    }

    @Override
    public void onFeedbackButtonClick(int id, boolean isBookmarked) {
        startActivity(new Intent(this, FeedbackActivity.class)
                .putExtra(EXTRA_ID, id)
                .putExtra(EXTRA_IS_BOOKMARK, isBookmarked));
    }

    @Override
    public void onDetailButtonClick(int id, boolean isBookmarked) {
        startActivity(new Intent(this, RecordDetailActivity.class)
                .putExtra(EXTRA_ID, id)
                .putExtra(EXTRA_IS_BOOKMARK, isBookmarked));
    }

    @Override
    public void onLoginSuccessful() {
        commitFragment(new FeedbackPreviewFragment(), FeedbackPreviewFragment.TAG);
        mActionBar.setTitle(R.string.title_activity_feedback);
    }


}
