/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.kpi.campus.Activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import ua.kpi.campus.R;
import ua.kpi.campus.api.jsonparsers.Employee;
import ua.kpi.campus.api.jsonparsers.JSONUserDataParser;
import ua.kpi.campus.api.jsonparsers.User;
import ua.kpi.campus.api.jsonparsers.UserData;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    public final static String EXTRA_CURRENT_USER = "user";
    private final static int COUNT_TABS = 4;
    private UserData currentUser;
    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    private ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
      three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
      derivative, which will keep every loaded fragment in memory. If this becomes too memory
      intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        AppSectionsPagerAdapter mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        Intent intent = getIntent();
        String permissionsStr = intent.getStringExtra(EXTRA_CURRENT_USER);
        try {
            User currentUser = parseUser(permissionsStr);
            this.currentUser = currentUser.getData();
            Log.d(MainActivity.class.getName(), hashCode() + " parsed.");
            //showToastLong(permissionsStr);
        } catch (JSONException e) {
            Log.e(MainActivity.class.getName(), hashCode() + " parsing failed\n" + permissionsStr);
        }
    }


    private void showToastLong(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        Log.d(this.getClass().getName(), hashCode() + " ToastLong:\n" + text);
    }

    private User parseUser(String jsonUser) throws JSONException {
        Log.d(MainActivity.class.getName(), hashCode() + " parsing\n" + jsonUser);
        return JSONUserDataParser.parse(jsonUser);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class LaunchpadSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

            // Demonstration of a collection-browsing activity.
            rootView.findViewById(R.id.demo_collection_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), CollectionDemoActivity.class);
                            startActivity(intent);
                        }
                    });

            // Demonstration of navigating to external activities.
            rootView.findViewById(R.id.demo_external_activity)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create an intent that asks the user to pick a photo, but using
                            // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                            // the application from the device home screen does not return
                            // to the external activity.
                            Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
                            externalActivityIntent.setType("image/*");
                            externalActivityIntent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            startActivity(externalActivityIntent);
                        }
                    });

            return rootView;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new MyProfileSectionFragment();
                case 1:
                    return new DeskSectionFragment();
                case 2:
                    return new MessageSectionFragment();
                case 3:
                    return new StatSectionFragment();

                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return COUNT_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getResources().getString(R.string.section1_main_info);
                case 1:
                    return getResources().getString(R.string.section2_desk);
                case 2:
                    return getResources().getString(R.string.section3_message);
                case 3:
                    return getResources().getString(R.string.section4_stat);
                default:
                    return "Section " + (position + 1);
            }
        }
    }

    /**
     * Main profile for user
     */
    public class MyProfileSectionFragment extends Fragment {
        ImageView avatar;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_my_profile, container, false);
            avatar = (ImageView) findViewById(R.id.avatar);

            TextView tFullName = (TextView) rootView.findViewById(R.id.FullName);
            TextView tSubdivisionName = (TextView) rootView.findViewById(R.id.SubdivisionName);
            TextView tPosition = (TextView) rootView.findViewById(R.id.Position);
            TextView tAcademicDegree = (TextView) rootView.findViewById(R.id.AcademicDegree);
            TextView tAcademicStatus = (TextView) rootView.findViewById(R.id.AcademicStatus);

            Employee currentEmployee = currentUser.getEmployees().get(0);
            tFullName.setText(currentUser.getFullName());
            tSubdivisionName.setText(currentEmployee.getSubDivisionName());
            tPosition.setText(currentEmployee.getPosition());
            tAcademicDegree.setText(currentEmployee.getAcademicDegree());
            tAcademicStatus.setText(currentEmployee.getAcademicStatus());

            return rootView;
        }
    }

    /**
     * Дошка оголошень
     */
    public class DeskSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_desk, container, false);

            return rootView;
        }
    }

    /**
     * Messages
     */
    public class MessageSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_message, container, false);

            return rootView;
        }
    }

    /**
     * Statistics
     */
    public class StatSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_stat, container, false);

            return rootView;
        }
    }
}
