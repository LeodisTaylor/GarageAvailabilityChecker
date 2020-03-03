package com.example.Orlando_GarageApplication.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Orlando_GarageApplication.DialogFlowChatActivity;
import com.example.Orlando_GarageApplication.FavoriteDBHelper;
import com.example.Orlando_GarageApplication.FavoriteGarage;
import com.example.Orlando_GarageApplication.FavoriteGarageAdapter;
import com.example.Orlando_GarageApplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;

public class FavoriteGarageActivity extends AppCompatActivity {

    public static final int ADD_FAVORITEGARAGE_REQUEST = 1;
    private static final int EDIT_FAVORITEGARAGE_REQUEST = 2;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(("MM/dd/yyyy"));
    private SQLiteDatabase sqLiteDatabase;
    private FavoriteGarageAdapter favoriteGarageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_garage);

        //Initializes databases helper and retrieves the readable database
        FavoriteDBHelper favoriteDBHelper = new FavoriteDBHelper(this);
        sqLiteDatabase = favoriteDBHelper.getWritableDatabase();

        //Sets up the RecyclerViewer to show the current user's favorite garages in the database as well as updating them when it changes
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteGarageAdapter = new FavoriteGarageAdapter(this, getAllItems());
        favoriteGarageAdapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(favoriteGarageAdapter);

        //Floating Action Button that takes you to the AddEditFavoriteGarageActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FavoriteGarageActivity.this, AddEditFavoriteGarageActivity.class);
                startActivityForResult(intent, EDIT_FAVORITEGARAGE_REQUEST);
                favoriteGarageAdapter.swapCursor(getAllItems());
            }
        });

        /* Configure sign-in to request the user's ID, email address, and basic
           profile. ID and basic profile are included in DEFAULT_SIGN_IN. */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Takes the swipe right gesture when done on a RecyclerView item and sends it to the edit favorite garage activity
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Intent intent = new Intent(FavoriteGarageActivity.this, AddEditFavoriteGarageActivity.class);
                intent.putExtra("EXTRA_FAVORITETITLE", (String) viewHolder.itemView.getTag(R.id.keyFavoriteTitle));
                intent.putExtra("EXTRA_DAYWANTED", (String) viewHolder.itemView.getTag(R.id.keyDayWanted));
                intent.putExtra("EXTRA_GARAGENAME", (String) viewHolder.itemView.getTag(R.id.keyGarageName));
                intent.putExtra("EXTRA_ITEMID", (long) viewHolder.itemView.getTag(R.id.keyItemID));
                startActivityForResult(intent, EDIT_FAVORITEGARAGE_REQUEST);
                favoriteGarageAdapter.swapCursor(getAllItems());
            }
        }).attachToRecyclerView(recyclerView);

        //Takes the swipe left gesture when done on a RecyclerView item and deletes it from the database and page
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((long) viewHolder.itemView.getTag(R.id.keyItemID));
            }
        }).attachToRecyclerView(recyclerView);
    }

    //When the user exits the add/edit favorite garage activity it will automatically update the RecyclerView to show the changes
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        favoriteGarageAdapter.swapCursor(getAllItems());
    }

    //Creates a custom toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsitemmenu, menu);
        return true;
    }

    //Gets the icon the user selected from the toolbar and performs that action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mainActivityMenu) {
            Intent intent = new Intent(FavoriteGarageActivity.this, DialogFlowChatActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.moreItemMenu) {
            Intent intent = new Intent(FavoriteGarageActivity.this, SelectMoreOptionsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Retrieves all items in the database that match the current users ID
    private Cursor getAllItems() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        String[] personId = {acct.getId()};
        String selection = FavoriteGarage.FavoriteGarageEntry.COLUMN_PERSONID + "=?";
        return sqLiteDatabase.query(FavoriteGarage.FavoriteGarageEntry.TABLE_NAME, null, selection, personId, null, null, FavoriteGarage.FavoriteGarageEntry.COLUMN_FAVORITETITLE + " ASC");

    }

    //Removes an item from the database
    private void removeItem(long id) {
        sqLiteDatabase.delete(FavoriteGarage.FavoriteGarageEntry.TABLE_NAME, FavoriteGarage.FavoriteGarageEntry._ID + "=" + id, null);
        favoriteGarageAdapter.swapCursor(getAllItems());
    }
}
