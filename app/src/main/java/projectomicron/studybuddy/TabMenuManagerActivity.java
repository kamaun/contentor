package projectomicron.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * This class is an AppCompatActivity class facilitates the different tab menus a user can have access to.
 * Created by Whitney Andrews on 4/9/2021.
 */
public class TabMenuManagerActivity extends AppCompatActivity {
    /**
     * Instance variables visible only in the TabMenuManagerActivity class.
     */
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    /**
     * Overrides the onCreate method inherited from AppCompatActivity.
     * @param savedInstanceState an instance of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_menu_manager);

        //Get references to the toolbar, viewPager and tabLayout
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Sets the viewpager.
     * @param viewPager a viewPager object
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ProfileFragmentActivity(), "PROFILE");
        adapter.addFrag(new TextMessagesFragmentActivity(), "TEXTS");
        adapter.addFrag(new ContentManagerMenuFragmentActivity(), "FITNESS");
        viewPager.setAdapter(adapter);
    }

    /**
     * Redirects a user to a new activity.
     * @param intent a new activity to start
     */
    public void onSwitch(Intent intent) {
        startActivity(intent);
    }

    /**
     * This is an adapter class that will allow the user to swipe between menus.
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        /**
         * Instance variables only visible in the ViewPagerAdapter class.
         */
        private final ArrayList<Fragment> FragmentList = new ArrayList<>();
        private final ArrayList<String> FragmentTitleList = new ArrayList<>();

        /**
         * Constructs a ViewPagerAdapter object.
         * @param manager a FragmentManager object
         */
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        /**
         * Gets a fragment from the FragmentList.
         * @param position the index in which the fragment is located
         * @return a Fragment object
         */
        @Override
        public Fragment getItem(int position) {
            return FragmentList.get(position);
        }

        /**
         * Gets the size of the FragmentList.
         * @return returns the size of the FragmentList
         */
        @Override
        public int getCount() {
            return FragmentList.size();
        }

        /**
         * Adds a Fragment object and its title to the FragmentList and FragmentTitleList.
         * @param fragment a Fragment object
         * @param title the title of the Fragment object
         */
        public void addFrag(Fragment fragment, String title) {
            FragmentList.add(fragment);
            FragmentTitleList.add(title);
        }

        /**
         * Gets the title of the Fragment.
         * @param position the location of the title in the FragmentTittleList
         *
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return FragmentTitleList.get(position);
        }
    }

    /**
     * Overrides the onCreateOptionsMenu method from AppCompatActivity to add the logout menu in the
     * action bar
     * @param menu a Menu object in the actionbar
     * @return a boolean value representing if the menu was added
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Overrides the onOptionsItemSelected method in AppCompatActivity to handle the event when the
     * user clicks on the logout button
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TabMenuManagerActivity.this.getIntent().removeExtra("userID");
                        TabMenuManagerActivity.this.getIntent().removeExtra("userRole");
                        //Set an intent to redirect the user to the LoginActivity activity
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        //Start the next activity
                        onSwitch(intent);
                    }
                }).start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
