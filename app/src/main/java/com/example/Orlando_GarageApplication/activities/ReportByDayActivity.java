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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReportByDayActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "ReportByDayActivity";
    private TextView datePickerClick;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private RequestQueue requestQueue;
    private String selectedGarageFromSpinner;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_by_day);

        //Sets up garageSelectSpinner and sets it to the gargae names
        Spinner garageSelectSpinner = findViewById(R.id.garageSelectSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.garageArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        garageSelectSpinner.setAdapter(adapter);
        garageSelectSpinner.setOnItemSelectedListener(this);

        //Sets up the lineChart to be populated later
        lineChart = findViewById(R.id.lineChart);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);

        //Sets up the datePicker so the user can select the date to retrieve data from
        Calendar cal = Calendar.getInstance();
        datePickerClick = findViewById(R.id.datePickerClick);
        datePickerClick.setText(Integer.toString(cal.get(Calendar.YEAR)));
        datePickerClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ReportByDayActivity.this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                dialog.show();
            }
        });

        //Sends the request with the selected date to the JSON parser who will display it in a table and chart view
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
                        Toast.makeText(ReportByDayActivity.this, "You cannot have a date in the future or before January 2nd, 2019", Toast.LENGTH_LONG).show();
                    } else if (date1.before(date2) || date1.equals(date2)) {
                        datePickerClick.setText(date);

                        TextView graphTitle = findViewById(R.id.graphTitle);
                        graphTitle.setText(month + "/" + dayOfMonth + "/" + year + " Graph Report");
                        TextView tableTitle = findViewById(R.id.tableTitle);
                        tableTitle.setText(month + "/" + dayOfMonth + "/" + year + " Table Report");

                        requestQueue = Volley.newRequestQueue(ReportByDayActivity.this);

                        jsonParse(Integer.toString(month), Integer.toString(dayOfMonth), Integer.toString(year));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    //Retrieves data from the API based on the users input and creates/populates a table and line chart
    private void jsonParse(String month, String day, String year) {
        TableLayout tl = findViewById(R.id.garageTable);
        String url = "https://api.ucfgarages.com/month/" + month + "/day/" + day + "?year=" + year;
        try {
            url = url.concat("&garages=" + selectedGarageFromSpinner);
        } catch (NullPointerException e) {
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT);
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Loops through the response received by the garage API to get required data
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    tl.removeAllViews();
                    lineChart.clear();
                    ArrayList<com.github.mikephil.charting.data.Entry> yValues = new ArrayList<>();
                    TablePopulate.setTableHeaderReports(tl, ReportByDayActivity.this);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String dateTime = jsonObject.getString("date");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssssss");
                        Date startDate = sdf.parse(dateTime);
                        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat time = new SimpleDateFormat("hh a");
                        SimpleDateFormat time2 = new SimpleDateFormat("HH");
                        String formattedTime2 = time2.format(startDate);
                        int testInt = Integer.parseInt(formattedTime2);
                        String formattedTime = time.format(startDate);
                        String formattedDate = output.format(startDate);

                        JSONArray jsonArray1 = jsonObject.getJSONArray("garages");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject garages = jsonArray1.getJSONObject(j);

                            String name = garages.getString("name");
                            int spacesLeft = garages.getInt("spaces_left");
                            int spacesTaken = garages.getInt("spaces_filled");

                            yValues.add(new Entry(testInt, spacesTaken));

                            TablePopulate.setTableReports(name, formattedTime, Integer.toString(spacesLeft), tl, formattedDate, ReportByDayActivity.this);
                        }
                    }
                    LineDataSet set1 = new LineDataSet(yValues, "Average Spots Taken");
                    set1.setFillAlpha(110);
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);
                    LineData data = new LineData(dataSets);
                    lineChart.getDescription().setText("X-value = Hours of Day, Y-value = Spots Taken");
                    lineChart.setData(data);
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
        ReportByDayActivity.this.selectedGarageFromSpinner = parent.getItemAtPosition(position).toString();
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
            Intent intent = new Intent(ReportByDayActivity.this, DialogFlowChatActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.moreItemMenu) {
            Intent intent = new Intent(ReportByDayActivity.this, SelectMoreOptionsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
