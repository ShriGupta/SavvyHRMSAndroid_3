package com.savvy.hrmsnewapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.LoginActivity_1;
import com.savvy.hrmsnewapp.adapter.NavigationDrawerAdapter;
import com.savvy.hrmsnewapp.classes.RecyclerTouchListener;
import com.savvy.hrmsnewapp.customwidgets.CustomTextView;
import com.savvy.hrmsnewapp.interfaces.ClickListener;
import com.savvy.hrmsnewapp.interfaces.FragmentDrawerListener;
import com.savvy.hrmsnewapp.retrofit.APIServiceClass;
import com.savvy.hrmsnewapp.retrofit.ResultHandler;
import com.savvy.hrmsnewapp.retrofitModel.MenuModule;
import com.savvy.hrmsnewapp.room_database.DatabaseClient;
import com.savvy.hrmsnewapp.utils.Constants;
import com.savvy.hrmsnewapp.utils.ErrorConstants;
import com.savvy.hrmsnewapp.utils.Utilities;

import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class FragmentDrawer extends BaseFragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private FragmentDrawerListener drawerListener;
    RelativeLayout profileLayout;
    SharedPreferences sharedpreferencesIP;
    private CoordinatorLayout coordinatorLayout;
    private CustomTextView noData;
    final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences shared;

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferencesIP = getActivity().getSharedPreferences("IP_ADDRESS_CONSTANT", MODE_PRIVATE);
        Constants.IP_ADDRESS = sharedpreferencesIP.getString("IP_ADDRESS", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        recyclerView = getActivity().findViewById(R.id.drawerList);
        noData = getActivity().findViewById(R.id.no_data);
        profileLayout = getActivity().findViewById(R.id.nav_header_container);

        shared = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);

        try {
            if (Utilities.isNetworkAvailable(getActivity())) {
                String token = (shared.getString("Token", ""));
                String empoyeeId = (shared.getString("EmpoyeeId", ""));
                callMenuAsynTask(empoyeeId, token);
            } else {
                Utilities.showDialog(coordinatorLayout, ErrorConstants.NO_NETWORK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    private void callMenuAsynTask(String employeeId, String token) {
        showProgressDialog();
        APIServiceClass.getInstance().sendMenuDataRequest(employeeId, token, new ResultHandler<List<MenuModule>>() {
            @Override
            public void onSuccess(List<MenuModule> data) {
                dismissProgressDialog();
                Log.e(TAG, "onSuccess: " + data);
                try {
                    DisplayResult(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent intent = new Intent(getActivity(), LoginActivity_1.class);
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).finish();
                }
            }

            @Override
            public void onFailure(String message) {
                dismissProgressDialog();
                Log.e(TAG, "onFailure: " + message);
            }
        });
    }

    private void DisplayResult(List<MenuModule> menuList) {
        if (menuList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            adapter = new NavigationDrawerAdapter(getActivity(), menuList);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    MenuModule hmap = menuList.get(position);
                    String pos = hmap.getPrivilegeId();
                    String pName = hmap.getPrivilegeName();
                    drawerListener.onDrawerItemSelected(view, pos, pName);
                    mDrawerLayout.closeDrawer(containerView);
                }

                @Override
                public void onLongClick(View view, int position) {
                }
            }));
        } else {
            recyclerView.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
    }
}
