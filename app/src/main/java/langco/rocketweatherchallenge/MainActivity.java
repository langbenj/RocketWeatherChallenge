package langco.rocketweatherchallenge;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.otto.Subscribe;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    private String default_url_to_pass = "http://forecast.weather.gov/MapClick.php?lat=41.885575&lon=-87.644408&FcstType=json";
    private static ArrayList<String> output_array = new ArrayList<String>(asList("", "", "", "", ""));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Network queries to determine the location or return to the default if needed.
        LocationManager location_manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Check to see if the user has disabled their location services for this app.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                //If permissions are shut off ask for them to be turned back on each time they are accessed.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
        else {

            //Pull the location object from the Network Provider. I decided not to use the GPS because of the notes regarding low cell
            //signal on Lower Wacker Drive. Network Provider has a better chance of pulling the data in that case.
            Location location = location_manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //Check one last time to make sure the data is good. If not the default location will stay.
            if (location != null) {
                float latitude = (float) (location.getLatitude());
                float longitude = (float) (location.getLongitude());
                default_url_to_pass = "http://forecast.weather.gov/MapClick.php?lat=" + latitude + "&lon=" + longitude + "&FcstType=json";
            } else {
                //Can be triggered on the desktop Android Studio where there isn't a network to pull from.
                Toast.makeText(getApplicationContext(), "Problem with loading location", Toast.LENGTH_SHORT).show();
            }
        }

        //Register on the OTTO bus
        App.bus.register(this);

        //Call JSON reader to load data and launch the views once complete
        new WeatherJSONReader().execute(default_url_to_pass);
    }

    //Using the Otto library to pass items across a bus. Implemented in App
    @Subscribe
    public void catchJSONReadCompletion(BusEventHandler event) {
        output_array=event.getParameter();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter  mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Set up the top tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public static class PlaceholderFragment extends Fragment {

        //Variable that specifies the view page you are currently on.
        private static int section_number;

        public PlaceholderFragment() {
        }

        //Returns a new instance of this fragment for the given section
        //number.

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            //Pass the contents of the view along
            Bundle args = new Bundle();
            args.putString("item_text", output_array.get(section_number));
            section_number=sectionNumber;
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            //Set the contents of the view
            TextView textView = (TextView) rootView.findViewById(R.id.display);
            textView.setText(getArguments().getString("item_text"));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //Display the top tabs
            switch (position) {
                case 0:
                    return return_date(0,"MM/dd");
                case 1:
                    return return_date(1,"MM/dd");
                case 2:
                    return return_date(2,"MM/dd");
                case 3:
                    return return_date(3,"MM/dd");
                case 4:
                    return return_date(4,"MM/dd");
            }
            return null;
        }

        public String return_date (int offset, String format) {
            //Generates a properly formatted String of the date for display
            Calendar todays_date = Calendar.getInstance();
            todays_date.add(Calendar.DATE,offset);
            SimpleDateFormat formatted_date=new SimpleDateFormat(format);
            return formatted_date.format(todays_date.getTime());
        }
    }
}
