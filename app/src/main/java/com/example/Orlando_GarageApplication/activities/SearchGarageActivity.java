package com.example.Orlando_GarageApplication.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.Orlando_GarageApplication.DialogFlowChatActivity;
import com.example.Orlando_GarageApplication.R;
import com.example.Orlando_GarageApplication.TablePopulate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class SearchGarageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView datePickerClick;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private RequestQueue requestQueue;
    private String selectedGarageFromSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_garage);
        Spinner garageSelectSpinner = findViewById(R.id.garageSelectSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.garageSearchArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        garageSelectSpinner.setAdapter(adapter);
        garageSelectSpinner.setOnItemSelectedListener(this);


        Calendar cal = Calendar.getInstance();
        datePickerClick = findViewById(R.id.datePickerClick);
        datePickerClick.setText(Integer.toString(cal.get(Calendar.YEAR)));
        datePickerClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SearchGarageActivity.this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                month += 1;

                String date4 = month + "/" + dayOfMonth + "/" + year;
                LocalDate localDate = LocalDate.now();
                String currentUnformated = DateTimeFormatter.ofPattern("MM/dd/yyyy").format(localDate);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");


                String date = month + "/" + dayOfMonth + "/" + year;

                try {
                    Date date1 = sdf.parse(date4);
                    Date date2 = sdf.parse(currentUnformated);
                    Date date3 = sdf.parse("01/02/2019");

                    if (date1.after(date2) || date1.before(date3)) {
                        Toast.makeText(SearchGarageActivity.this, "You cannot have a date in the future or before January 2nd, 2019", Toast.LENGTH_LONG).show();
                    } else if (date1.before(date2) || date1.equals(date2)) {
                        datePickerClick.setText(date);

                        requestQueue = Volley.newRequestQueue(SearchGarageActivity.this);

                        jsonParse(Integer.toString(month), Integer.toString(dayOfMonth), Integer.toString(year));
                    }
                } catch (ParseException e) {
                    System.out.println("nope");
                    e.printStackTrace();
                }


            }
        };
    }

    private void jsonParse(String month, String day, String year) {
        TableLayout tl = findViewById(R.id.garageTable);
        String url = "https://api.ucfgarages.com/month/" + month + "/day/" + day + "?year=" + year;
        TextView tableTitle = findViewById(R.id.tableTitle);
        tableTitle.setText(month + "/" + day + "/" + year + " Raw Data Table");
        try {
            if (selectedGarageFromSpinner.equals("All")) {

            } else {
                url = url.concat("&garages=" + selectedGarageFromSpinner);
            }
        } catch (NullPointerException e) {
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    tl.removeAllViews();

                    TablePopulate.setTableHeaderSearch(tl, SearchGarageActivity.this);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String dateTime = jsonObject.getString("date");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssssss");
                        Date startDate = sdf.parse(dateTime);
                        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String formattedTime = output.format(startDate);

                        JSONArray jsonArray1 = jsonObject.getJSONArray("garages");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject garages = jsonArray1.getJSONObject(j);

                            String name = garages.getString("name");
                            int spacesLeft = garages.getInt("spaces_left");
                            double percentFullParse = garages.getDouble("percent_full");
                            TablePopulate.setTableSearch(name, Integer.toString(spacesLeft), Double.toString(percentFullParse), tl, formattedTime, SearchGarageActivity.this);
                        }
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
            }
        });
        requestQueue.add(request);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SearchGarageActivity.this.selectedGarageFromSpinner = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            Intent intent = new Intent(SearchGarageActivity.this, DialogFlowChatActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.moreItemMenu) {
            Intent intent = new Intent(SearchGarageActivity.this, SelectMoreOptionsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
