package pt.ulisboa.tecnico.cmov.p2photo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import data.AppDatabase;

public class MainEmptyActivity extends AppCompatActivity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Intent activityIntent;

            // go straight to main if a token is stored
            //TODO have a token to login mechanism
            //if (Util.getToken() != null) {
            //     activityIntent = new Intent(this, MainActivityAlbunsActivity.class);
            //} else {
                activityIntent = new Intent(this, LoginInActivity.class);
            //}

            AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
            //AppDatabase.clearAll(db);

            startActivity(activityIntent);
            finish();
        }
    }
