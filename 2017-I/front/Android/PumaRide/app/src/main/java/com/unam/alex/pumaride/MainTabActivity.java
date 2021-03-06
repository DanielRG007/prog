package com.unam.alex.pumaride;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.unam.alex.pumaride.Tasks.GetGcmTokenTask;
import com.unam.alex.pumaride.fragments.MatchFragment;
import com.unam.alex.pumaride.fragments.MyMapFragment;
import com.unam.alex.pumaride.fragments.RouteFragment;
import com.unam.alex.pumaride.fragments.listeners.OnFragmentInteractionListener;
import com.unam.alex.pumaride.models.Match;
import com.unam.alex.pumaride.models.Message;
import com.unam.alex.pumaride.models.MessageResult;
import com.unam.alex.pumaride.models.User;
import com.unam.alex.pumaride.retrofit.WebServices;
import com.unam.alex.pumaride.services.MessageService;
import com.unam.alex.pumaride.services.SocketServiceProvider;
import com.unam.alex.pumaride.utils.Statics;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainTabActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static boolean active = false;
    BroadcastReceiver receiver;
    RouteFragment rFragment = null;
    MatchFragment mFragment = null;
    //array for fragments
    int fragments[] = {R.layout.fragment_route,R.layout.fragment_match};
    public boolean mIsBound = false;
    String TAG = getClass().getName();
    private GoogleCloudMessaging gcm;
    public Activity activity;
    String tokedId = null;
    private void doBindService() {
        if (mBoundService != null) {
            bindService(new Intent(MainTabActivity.this, SocketServiceProvider.class), socketConnection, Context.BIND_AUTO_CREATE);
            mIsBound = true;
            mBoundService.IsBendable();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        activity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rFragment = new RouteFragment();
        mFragment = new MatchFragment();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setTitle("PumaRide");
        init();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                reloadFragment(mFragment);
                reloadFragment(rFragment);
            }
        };
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

    }
    public void createToken(){
        new Thread(new Runnable() {
            public void run() {
                SharedPreferences sp = getSharedPreferences("pumaride", Activity.MODE_PRIVATE);
                final int id = sp.getInt("userid", 1);
                tokedId = sp.getString("gcmtoken", "");

                if(tokedId.equals("")) {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(activity);
                    }
                    InstanceID instanceID = InstanceID.getInstance(activity);
                    try {
                        tokedId = instanceID.getToken(Statics.GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("gcmtoken", tokedId);
                        editor.commit();
                        Log.d(TAG, "GCM token is " + tokedId);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Error in fetching GCM token due to " + e);
                    }
                    if (tokedId != null) {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(Statics.GCM_SERVER_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        WebServices webServices = retrofit.create(WebServices.class);

                        Call<MessageResult> call = webServices.register(Statics.getDeviceGmail(getApplicationContext()),String.valueOf(id),tokedId);
                        call.enqueue(new Callback<MessageResult>() {
                            @Override
                            public void onResponse(Call<MessageResult> call, Response<MessageResult> response) {
                                Toast.makeText(getApplicationContext(),"Usuario registrado o actualizado"+response.body(),Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<MessageResult> call, Throwable t) {
                                Toast.makeText(getApplicationContext(),"ando fallando",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }).start();


    }
    public void init() {
        SharedPreferences sp = getSharedPreferences("pumaride", Activity.MODE_PRIVATE);
        String token = sp.getString("token", "");
        String email = sp.getString("email","");
        String first_name = sp.getString("first_name","");
        String last_name = sp.getString("last_name","");


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Statics.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WebServices webServices = retrofit.create(WebServices.class);

        Call<User> call = webServices.getUserMe("token "+token);
        //Toast.makeText(getApplicationContext(),token,Toast.LENGTH_SHORT).show();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User u = response.body();
                //Toast.makeText(getApplicationContext(),new Gson().toJson(u),Toast.LENGTH_SHORT).show();
                SharedPreferences sp = getSharedPreferences("pumaride", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("userid",u.getId());
                editor.putString("first_name", u.getFirst_name());
                editor.putString("last_name", u.getLast_name());
                editor.putString("image", u.getImage());
                editor.putString("aboutme", u.getAboutme());
                editor.commit();
                createToken();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"ando fallando",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_route) {
            Intent i = new Intent(getApplicationContext(),MapsActivity.class);
            startActivityForResult(i,100);
        }else if (id == R.id.profile){
            Intent i = new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(i);
        }else if (id == R.id.about){
            Intent i = new Intent(getApplicationContext(),AboutActivity.class);
            startActivity(i);
        }else if (id == R.id.logout){

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        reloadFragment(rFragment);
        reloadFragment(mFragment);
    }
    public void reloadFragment(Fragment fragment){
        FragmentTransaction mf = fragment.getFragmentManager().beginTransaction();
        mf.detach(fragment).attach(fragment).commit();
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main_tab, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment newFragment = null;
            if(position == 0){
                newFragment = rFragment;
            }else{
                newFragment = mFragment;
            }
            return newFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "RUTAS";
                case 1:
                    return "AMIGOS";
            }
            return null;
        }
    }

    @Override
    public void onFragmentInteraction(String title) {
        getSupportActionBar().setTitle(title);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    SocketServiceProvider mBoundService;
    protected ServiceConnection socketConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((SocketServiceProvider.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }
    };
    @Override
    public void onStart() {
        super.onStart();
        active = true;
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),new IntentFilter(MessageService.MESSAGE_RESULT));
    }

    @Override
    public void onStop() {
        active = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }
}
