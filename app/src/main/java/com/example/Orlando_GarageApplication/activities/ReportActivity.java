package com.example.Orlando_GarageApplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Orlando_GarageApplication.DialogFlowChatActivity;
import com.example.Orlando_GarageApplication.R;

//Sets up button UI elements and their on click tasks
public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsitemmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mainActivityMenu) {
            Intent intent = new Intent(ReportActivity.this, DialogFlowChatActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.moreItemMenu) {
            Intent intent = new Intent(ReportActivity.this, SelectMoreOptionsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void reportDay(View view) {
        Intent intent = new Intent(this, ReportByDayActivity.class);
        startActivity(intent);
    }

    public void reportWeek(View view) {
        Intent intent = new Intent(this, ReportByWeekActivity.class);
        startActivity(intent);
    }
}
