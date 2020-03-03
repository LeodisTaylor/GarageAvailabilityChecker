package com.example.Orlando_GarageApplication.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Orlando_GarageApplication.FavoriteDBHelper;
import com.example.Orlando_GarageApplication.FavoriteGarage;
import com.example.Orlando_GarageApplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class AddEditFavoriteGarageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SQLiteDatabase sqLiteDatabase;

    private EditText editTextTitle;
    private Spinner daySpinner;
    private Spinner garageNameSpinner;
    private String selectedGarageFromSpinner;
    private String selectedDayFromSpinner;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_favorite_garage);

        //Initializes databases helper and retrieves the writable database
        FavoriteDBHelper favoriteDBHelper = new FavoriteDBHelper(this);
        sqLiteDatabase = favoriteDBHelper.getWritableDatabase();

        //Assigns various UI elements and sets listeners to detect user gestures
        editTextTitle = findViewById(R.id.edit_text_title);
        daySpinner = findViewById(R.id.daySpinner);
        garageNameSpinner = findViewById(R.id.editSpinnerGarageName);

        //Creates an adapter that populates the daySpinner with the days of the week, it also creates a listener for when it is changed
        ArrayAdapter<CharSequence> adapterDay = ArrayAdapter.createFromResource(this, R.array.daysOfWeek, android.R.layout.simple_spinner_item);
        adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setOnItemSelectedListener(this);
        daySpinner.setAdapter(adapterDay);

        //Creates an adapter that populates the garageNameSpinner with all the garage names, it also creates a listener for when it is changed
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.garageArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        garageNameSpinner.setOnItemSelectedListener(this);
        garageNameSpinner.setAdapter(adapter);

        /* Configure sign-in to request the user's ID, email address, and basic
           profile. ID and basic profile are included in DEFAULT_SIGN_IN. */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Gets the Intent that started this activity and assigns each EXTRA to a new variable
        Intent intent = getIntent();
        String EXTRA_FAVORITETITLE = intent.getStringExtra("EXTRA_FAVORITETITLE");
        String EXTRA_DAYWANTED = intent.getStringExtra("EXTRA_DAYWANTED");
        String EXTRA_GARAGENAME = intent.getStringExtra("EXTRA_GARAGENAME");
        long EXTRA_ITEMID = intent.getLongExtra("EXTRA_ITEMID", 0);

        //What you are about to see is complete spaghetti code, please excuse this as I was in a time crunch
        //Checks to see if the user is editing a favorite or if EXTRA_ITEMID is 0 then they are adding a new one, if they are editing set the Title, daySpinner, and garageNameSpinner to their correct values
        if (EXTRA_ITEMID != 0) {
            editTextTitle.setText(EXTRA_FAVORITETITLE);

            //Sets daySpinner to its correct value
            if (EXTRA_DAYWANTED.equalsIgnoreCase("Sunday")) {
                daySpinner.setSelection(0);
            } else if (EXTRA_DAYWANTED.equalsIgnoreCase("Monday")) {
                daySpinner.setSelection(1);
            } else if (EXTRA_DAYWANTED.equalsIgnoreCase("Tuesday")) {
                daySpinner.setSelection(2);
            } else if (EXTRA_DAYWANTED.equalsIgnoreCase("Wednesday")) {
                daySpinner.setSelection(3);
            } else if (EXTRA_DAYWANTED.equalsIgnoreCase("Thursday")) {
                daySpinner.setSelection(4);
            } else if (EXTRA_DAYWANTED.equalsIgnoreCase("Friday")) {
                daySpinner.setSelection(5);
            } else if (EXTRA_DAYWANTED.equalsIgnoreCase("Saturday")) {
                daySpinner.setSelection(6);
            }

            //Sets garageNameSpinner to its correct value
            if (EXTRA_GARAGENAME.equalsIgnoreCase("A")) {
                garageNameSpinner.setSelection(0);
            } else if (EXTRA_GARAGENAME.equalsIgnoreCase("B")) {
                garageNameSpinner.setSelection(1);
            } else if (EXTRA_GARAGENAME.equalsIgnoreCase("C")) {
                garageNameSpinner.setSelection(2);
            } else if (EXTRA_GARAGENAME.equalsIgnoreCase("D")) {
                garageNameSpinner.setSelection(3);
            } else if (EXTRA_GARAGENAME.equalsIgnoreCase("H")) {
                garageNameSpinner.setSelection(4);
            } else if (EXTRA_GARAGENAME.equalsIgnoreCase("I")) {
                garageNameSpinner.setSelection(5);
            } else if (EXTRA_GARAGENAME.equalsIgnoreCase("Libra")) {
                garageNameSpinner.setSelection(6);
            }
        }
    }

    //Either adds or updates a favorite garage in the database
    private void saveFavoriteGarage() {
        //Get the user input from the changeable fields
        String title = editTextTitle.getText().toString();
        String garageDay = selectedDayFromSpinner;
        String garageName = selectedGarageFromSpinner;

        //Makes sure the title field is not empty
        if (title.trim().isEmpty()) {
            Toast.makeText(this, "Please make sure every field is filled in", Toast.LENGTH_SHORT).show();
            return;
        }

        //Gets the current logged in users ID from Google, and get the EXTRA_ITEMID if the user is editing a favorite garage
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        String personId = acct.getId();
        Intent intent = getIntent();
        long EXTRA_ITEMID = intent.getLongExtra("EXTRA_ITEMID", 0);

        //Sets the column values that are to be placed in the SQLite database
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteGarage.FavoriteGarageEntry.COLUMN_PERSONID, personId);
        contentValues.put(FavoriteGarage.FavoriteGarageEntry.COLUMN_FAVORITETITLE, title);
        contentValues.put(FavoriteGarage.FavoriteGarageEntry.COLUMN_ACTIVATIONDATE, garageDay);
        contentValues.put(FavoriteGarage.FavoriteGarageEntry.COLUMN_GARAGENAME, garageName);

        //If the user is editing an item update it, if they are adding a new one insert it in the database
        if (EXTRA_ITEMID != 0) {
            sqLiteDatabase.update(FavoriteGarage.FavoriteGarageEntry.TABLE_NAME, contentValues, "_id=" + EXTRA_ITEMID, null);
            setResult(RESULT_OK, intent);
        } else {
            sqLiteDatabase.insert(FavoriteGarage.FavoriteGarageEntry.TABLE_NAME, null, contentValues);
            setResult(RESULT_OK, intent);
        }
        setResult(RESULT_OK, intent);
        finish();

    }

    //Creates a toolbar with save and close icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_modify_favorite, menu);
        return true;
    }

    //Gets the icon the user selects on the toolbar and does its action
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveFavoriteGarage();
                return true;
            case R.id.close:
                Intent intent = new Intent(AddEditFavoriteGarageActivity.this, FavoriteGarageActivity.class);
                startActivity(intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Sets the garageSpinner value and daySpinner value every time the user changes the spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.editSpinnerGarageName) {
            selectedGarageFromSpinner = garageNameSpinner.getItemAtPosition(position).toString();
        } else if (parent.getId() == R.id.daySpinner) {
            selectedDayFromSpinner = daySpinner.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
