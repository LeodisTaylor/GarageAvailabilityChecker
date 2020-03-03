package com.example.Orlando_GarageApplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Orlando_GarageApplication.activities.SelectMoreOptionsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;
import com.google.cloud.dialogflow.v2beta1.TextInput;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nullable;

public class DialogFlowChatActivity extends AppCompatActivity {

    //int variables to determine request/response layouts
    private static final int USER = 0;
    private static final int BOT = 1;

    private final String uuid = UUID.randomUUID().toString();
    private TextToSpeech textToSpeech;

    private LinearLayout chatLayout;
    private EditText userRequestEditText;

    //DialogFlow session variables
    private SessionsClient sessionsClient;
    private SessionName session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializes databases helper and retrieves the readable database
        FavoriteDBHelper favoriteDBHelper = new FavoriteDBHelper(this);
        SQLiteDatabase sqLiteDatabase = favoriteDBHelper.getReadableDatabase();

        //Assigns various UI elements and sets listeners to detect user gestures
        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));
        ImageView sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this::sendRequestMessage);
        chatLayout = findViewById(R.id.chatLayout);
        userRequestEditText = findViewById(R.id.userRequestEditText);
        userRequestEditText.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        sendRequestMessage(sendButton);
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });

        /* Configure sign-in to request the user's ID, email address, and basic
           profile. ID and basic profile are included in DEFAULT_SIGN_IN. */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Initializes Text To Speech
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.getDefault());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        }, "com.google.android.tts");


        //Activates DialogFlow
        initializeDialogFlow();

        //Get the currently signed in account from Google
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        //Get their Google ID
        String personId = acct.getId();

        //Get today's date and convert it into today's day in full name format
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String todaysDate = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());

        //SQLite database query that gets the current users favorite garages whose "Activation Date" is today
        String select = "SELECT * FROM " + FavoriteGarage.FavoriteGarageEntry.TABLE_NAME + " WHERE " + FavoriteGarage.FavoriteGarageEntry.COLUMN_PERSONID + " = ? AND " + FavoriteGarage.FavoriteGarageEntry.COLUMN_ACTIVATIONDATE + " = ?";
        Cursor cd = sqLiteDatabase.rawQuery(select, new String[]{personId, todaysDate});

        //Loops through the SQLite database result and sends a request to DialogFlow for those garages
        while (cd.moveToNext()) {
            String message = cd.getString(cd.getColumnIndex(FavoriteGarage.FavoriteGarageEntry.COLUMN_GARAGENAME));  //Gets the name of the favorite garage
            QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build(); //Generates DialogFlow query using the given garage name

            new ResponseRequest(DialogFlowChatActivity.this, session, sessionsClient, queryInput).execute();
        }

    }

    //Initializes DialogFlow using Google project information in R.raw.agent_cred, R.raw.agent_cred must have the JSON info of service account key you created through Google
    private void initializeDialogFlow() {
        try {
            InputStream stream = getResources().openRawResource(R.raw.agent_cred);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            session = SessionName.of(projectId, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Takes the user input in the userRequestEditText UI element and sends the request to DialogFlow
    private void sendRequestMessage(View view) {
        String message = userRequestEditText.getText().toString();

        //Checks if EditText field is empty and alerts the user to enter text
        if (message.trim().isEmpty()) {
            Toast.makeText(DialogFlowChatActivity.this, "You cannot send an empty query!", Toast.LENGTH_LONG).show();
        } else {
            showRequestResponseMessage(message, USER);
            //Resets the userRequestEditText field to be empty
            userRequestEditText.setText("");

            QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build();
            new ResponseRequest(DialogFlowChatActivity.this, session, sessionsClient, queryInput).execute();
        }
    }

    //Puts the DialogFlow response into a variable that is then read through TTS and sent to showRequestResponseMessage() to appear on screen
    public void dialogFlowResponse(DetectIntentResponse response) {
        if (response != null) {
            String dialogFlowResponse = response.getQueryResult().getFulfillmentText();

            //Reads DialogFlow's response
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(dialogFlowResponse, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                textToSpeech.speak(dialogFlowResponse, TextToSpeech.QUEUE_ADD, null, null);
            }

            showRequestResponseMessage(dialogFlowResponse, BOT);
        } else {
            showRequestResponseMessage("Something went wrong with DialogFlow, please alert the developer!", BOT);
        }
    }

    //Shows the Request/Response on screen based on who the message supplier is (Bot or User)
    private void showRequestResponseMessage(String message, int messageSupplier) {
        FrameLayout layout;
        switch (messageSupplier) {
            case USER:
                layout = getUserLayout();
                break;
            default:
                layout = getBotLayout();
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout);
        TextView tv = layout.findViewById(R.id.message);
        tv.setText(message);
        layout.requestFocus();
        userRequestEditText.requestFocus();
    }

    private FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(DialogFlowChatActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.user_response_layout, null);
    }

    private FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(DialogFlowChatActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_response_layout, null);
    }

    //Allows the user to talk to the app and have their request translated to text, activated when speechButton is clicked
    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your device is not supported", Toast.LENGTH_SHORT).show();
        }
    }

    //Takes user voice input and sends it to DialogFlow
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if ((resultCode == RESULT_OK) && (data != null)) {
                    ArrayList<String> resultStringList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Object[] resultObject = resultStringList.toArray();
                    String[] resultStringArray = Arrays.copyOf(resultObject, resultObject.length, String[].class);

                    if (resultStringArray.length == 0) {
                        Toast.makeText(DialogFlowChatActivity.this, "You cannot send an empty query!", Toast.LENGTH_LONG).show();
                    } else {
                        showRequestResponseMessage(resultStringArray[0], USER);
                        userRequestEditText.setText("");

                        QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(resultStringArray[0]).setLanguageCode("en-US")).build();
                        new ResponseRequest(DialogFlowChatActivity.this, session, sessionsClient, queryInput).execute();
                    }

                }
                break;
        }
    }

    //Creates a custom toolbar to be displayed which includes an icon which redirects you to the SelectMoreOptionsActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.moreitemsmenu, menu);
        return true;
    }

    //Activates when the user selects the car icon in the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.moreItemMenu) {
            Intent intent = new Intent(DialogFlowChatActivity.this, SelectMoreOptionsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
