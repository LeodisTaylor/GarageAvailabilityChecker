package com.example.Orlando_GarageApplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Orlando_GarageApplication.DialogFlowChatActivity;
import com.example.Orlando_GarageApplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

//Sets up button UI elements and their on click tasks
public class SelectMoreOptionsActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_more_options);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mainActivityMenu) {
            Intent intent = new Intent(SelectMoreOptionsActivity.this, DialogFlowChatActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void favoriteView(View view) {
        Intent intent = new Intent(this, FavoriteGarageActivity.class);
        startActivity(intent);
    }

    public void searchGarageView(View view) {
        Intent intent = new Intent(this, SearchGarageActivity.class);
        startActivity(intent);
    }

    public void reportView(View view) {
        Intent intent = new Intent(this, ReportActivity.class);
        startActivity(intent);
    }

    public void signOut(View view) {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(SelectMoreOptionsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                });
    }
}
