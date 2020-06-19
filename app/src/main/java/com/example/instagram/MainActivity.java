package com.example.instagram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.instagram.Fragment.HomeFragment;
import com.example.instagram.Fragment.NotificationFragment;
import com.example.instagram.Fragment.ProfileFragment;
import com.example.instagram.Fragment.SearchFragment;
import com.example.instagram.Model.Post;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectFragment=null;

    @Override
    protected void onCreate(Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        //change kr lyi if bottom navigation sahi kam na kre to reselected
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectListener);

        Bundle intent=getIntent().getExtras();

        if (intent!=null){
            String publisher=intent.getString("publisherid");
            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",publisher);
            editor.apply();
           //samjh nhi aya

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
        }else {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }


    }
    private  BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_home:
                    selectFragment=new HomeFragment();
                    break;
                case R.id.nav_search:
                    selectFragment=new SearchFragment();

                    break;
                case R.id.nav_add:
                    selectFragment=null;
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                    break;
                case R.id.nav_heart:
                    selectFragment=new NotificationFragment();
                    break;
                case R.id.nav_profile:
                    SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectFragment=new ProfileFragment();
                    break;
            }
            if (selectFragment!=null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectFragment).commit();
            }


            return true;
        }
    };
}