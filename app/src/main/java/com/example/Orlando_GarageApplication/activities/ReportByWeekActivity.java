package com.example.Orlando_GarageApplication.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReportByWeekActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "ReportByDayActivity";
    private RequestQueue requestQueue;
    private String selectedGarageFromSpinner;
    private LineChart lineChart;
    private EditText editTextYear;
    private EditText editTextWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_by_week);


        Spinner garageSelectSpinner = findViewById(R.id.garageSelectSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.garageArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        garageSelectSpinner.setAdapter(adapter);
        garageSelectSpinner.setOnItemSelectedListener(this);

        lineChart = findViewById(R.id.lineChart);

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);

        editTextWeek = findViewById(R.id.weekEditText);
        editTextYear = findViewById(R.id.yearEditText);
        Button showTableChartButton = findViewById(R.id.updateTableChartButton);


        showTableChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String weekNumber = editTextWeek.getText().toString();
                String yearNumber = editTextYear.getText().toString();

                LocalDate localDate = LocalDate.now();
                String currentUnformated = DateTimeFormatter.ofPattern("yyyy").format(localDate);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");


                try {


                    if ((weekNumber == null) || weekNumber.trim().isEmpty()) {
                        Toast.makeText(ReportByWeekActivity.this, "You have to enter something for the weeks", Toast.LENGTH_LONG).show();
                    } else if ((yearNumber == null) || yearNumber.trim().isEmpty()) {
                        Toast.makeText(ReportByWeekActivity.this, "You must have something for the year", Toast.LENGTH_LONG).show();
                    } else if ((Integer.parseInt(weekNumber) > 51) || (Integer.parseInt(weekNumber) < 1)) {
                        Toast.makeText(ReportByWeekActivity.this, "Weeks can only be 1-51", Toast.LENGTH_LONG).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(ReportByWeekActivity.this, "Please make sure your weeks and year are correct", Toast.LENGTH_LONG).show();
                }

                try {
                    Date date1 = sdf.parse(yearNumber);
                    Date date2 = sdf.parse(currentUnformated);
                    Date date3 = sdf.parse("2019");

                    if (date1.after(date2) || date1.before(date3) || (date1.equals(date2) && (Integer.parseInt(weekNumber) > 51) || (Integer.parseInt(weekNumber) > 51) || (Integer.parseInt(weekNumber) < 1))) {
                        Toast.makeText(ReportByWeekActivity.this, "You cannot have a date in the future or before January 2nd, 2019.  We do not collect data for the first week of every year.  Please fix your input to be a correct week and year.", Toast.LENGTH_LONG).show();
                    } else if (date1.before(date2) || date1.equals(date2)) {


                        requestQueue = Volley.newRequestQueue(ReportByWeekActivity.this);

                        jsonParse(weekNumber, yearNumber);
                    }
                } catch (ParseException | NumberFormatException e) {
                    Toast.makeText(ReportByWeekActivity.this, "You seem to have entered a week or year we cannot read, please correct it!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void jsonParse(String weekNumber, String yearNumber) {
        TableLayout tl = findViewById(R.id.garageTable);
        String url = "https://api.ucfgarages.com/week/" + weekNumber + "?year=" + yearNumber;
        TextView graphTitle = findViewById(R.id.graphTitle);
        graphTitle.setText("Week " + weekNumber + " of " + yearNumber + " Graph Report");
        TextView tableTitle = findViewById(R.id.tableTitle);
        tableTitle.setText("Week " + weekNumber + " of " + yearNumber + " Table Report");
        try {
            url = url.concat("&garages=" + selectedGarageFromSpinner);
        } catch (NullPointerException e) {
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT);
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //I am not even going to comment this spaghetti code, but all it does is loops through the JSON received by the garage API and finds the average for each week which then gets populated into the table and chart view
                try {
                    ArrayList<Integer> sundaySpots = new ArrayList<>();
                    ArrayList<Integer> mondaySpots = new ArrayList<>();
                    ArrayList<Integer> tuesdaySpots = new ArrayList<>();
                    ArrayList<Integer> wednesdaySpots = new ArrayList<>();
                    ArrayList<Integer> thursdaySpots = new ArrayList<>();
                    ArrayList<Integer> fridaySpots = new ArrayList<>();
                    ArrayList<Integer> saturdaySpots = new ArrayList<>();
                    String name = "";
                    int spacesLeft;
                    int spacesTaken;
                    JSONArray jsonArray = response.getJSONArray("data");
                    tl.removeAllViews();
                    lineChart.clear();

                    ArrayList<Entry> ySundayValues = new ArrayList<>();
                    ArrayList<Entry> yMondayValues = new ArrayList<>();
                    ArrayList<Entry> yTuesdayValues = new ArrayList<>();
                    ArrayList<Entry> yWednesdayValues = new ArrayList<>();
                    ArrayList<Entry> yThursdayValues = new ArrayList<>();
                    ArrayList<Entry> yFridayValues = new ArrayList<>();
                    ArrayList<Entry> ySaturdayValues = new ArrayList<>();

                    TablePopulate.setTableHeaderReports(tl, ReportByWeekActivity.this);
                    JSONObject jsonObjectOriginal = jsonArray.getJSONObject(0);
                    int originalDay = jsonObjectOriginal.getInt("day");
                    String dateTimeOriginal = jsonObjectOriginal.getString("date");
                    SimpleDateFormat sdfOriginal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssssss");
                    Date startDateOriginal = sdfOriginal.parse(dateTimeOriginal);
                    String formattedTime;
                    String formattedDate;
                    String finalDay;
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String dateTime = jsonObject.getString("date");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssssss");
                        Date startDate = sdf.parse(dateTime);
                        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat output2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        SimpleDateFormat time = new SimpleDateFormat("hh a");
                        SimpleDateFormat time2 = new SimpleDateFormat("HH");
                        DateFormat format2 = new SimpleDateFormat("EEEE");
                        String formattedTime2 = time2.format(startDate);
                        int testInt = Integer.parseInt(formattedTime2);
                        formattedTime = output2.format(startDate);
                        finalDay = format2.format(startDate);

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(startDateOriginal);
                        Calendar calMonday = Calendar.getInstance();
                        calMonday.setTime(startDateOriginal);
                        calMonday.add(Calendar.DATE, 1);
                        Calendar calTuesday = Calendar.getInstance();
                        calTuesday.setTime(startDateOriginal);
                        calTuesday.add(Calendar.DATE, 2);
                        Calendar calWednesday = Calendar.getInstance();
                        calWednesday.setTime(startDateOriginal);
                        calWednesday.add(Calendar.DATE, 3);
                        Calendar calThursday = Calendar.getInstance();
                        calThursday.setTime(startDateOriginal);
                        calThursday.add(Calendar.DATE, 4);
                        Calendar calFriday = Calendar.getInstance();
                        calFriday.setTime(startDateOriginal);
                        calFriday.add(Calendar.DATE, 5);
                        Calendar calSaturday = Calendar.getInstance();
                        calSaturday.setTime(startDateOriginal);
                        calSaturday.add(Calendar.DATE, 6);
                        System.out.println(calFriday.getTime());

                        JSONArray jsonArray1 = jsonObject.getJSONArray("garages");
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject garages = jsonArray1.getJSONObject(j);

                            name = garages.getString("name");
                            spacesLeft = garages.getInt("spaces_left");
                            spacesTaken = garages.getInt("spaces_filled");

                            if (output.format(cal.getTime()).equals(output.format(startDate))) {
                                sundaySpots.add(spacesLeft);
                                ySundayValues.add(new Entry(testInt, spacesTaken));
                            } else if (output.format(calMonday.getTime()).equals(output.format(startDate))) {
                                mondaySpots.add(spacesLeft);
                                yMondayValues.add(new Entry(testInt, spacesTaken));
                            } else if (output.format(calTuesday.getTime()).equals(output.format(startDate))) {
                                tuesdaySpots.add(spacesLeft);
                                yTuesdayValues.add(new Entry(testInt, spacesTaken));
                            } else if (output.format(calWednesday.getTime()).equals(output.format(startDate))) {
                                wednesdaySpots.add(spacesLeft);
                                yWednesdayValues.add(new Entry(testInt, spacesTaken));
                            } else if (output.format(calThursday.getTime()).equals(output.format(startDate))) {
                                thursdaySpots.add(spacesLeft);
                                yThursdayValues.add(new Entry(testInt, spacesTaken));
                            } else if (output.format(calFriday.getTime()).equals(output.format(startDate))) {
                                fridaySpots.add(spacesLeft);
                                yFridayValues.add(new Entry(testInt, spacesTaken));
                            } else if (output.format(calSaturday.getTime()).equals(output.format(startDate))) {
                                saturdaySpots.add(spacesLeft);
                                System.out.println(saturdaySpots);
                                ySaturdayValues.add(new Entry(testInt, spacesTaken));
                            }


                        }

                        if (sundaySpots.size() == 24) {
                            int sum = sundaySpots.stream().mapToInt(Integer::intValue).sum() / 24;
                            TablePopulate.setTableReports(name, finalDay, Integer.toString(sum), tl, formattedTime, ReportByWeekActivity.this);
                            sundaySpots.add(0);
                            LineDataSet set1 = new LineDataSet(ySundayValues, "Sunday Spots Taken");
                            set1.setFillAlpha(110);
                            dataSets.add(set1);
                        } else if (mondaySpots.size() == 24) {
                            int sum = mondaySpots.stream().mapToInt(Integer::intValue).sum() / 24;
                            TablePopulate.setTableReports(name, finalDay, Integer.toString(sum), tl, formattedTime, ReportByWeekActivity.this);
                            mondaySpots.add(0);
                            LineDataSet set1 = new LineDataSet(yMondayValues, "Monday Spots Taken");
                            set1.setFillAlpha(110);
                            set1.setColor(Color.RED);
                            set1.setCircleColor(Color.RED);
                            dataSets.add(set1);
                        } else if (tuesdaySpots.size() == 24) {
                            int sum = tuesdaySpots.stream().mapToInt(Integer::intValue).sum() / 24;
                            TablePopulate.setTableReports(name, finalDay, Integer.toString(sum), tl, formattedTime, ReportByWeekActivity.this);
                            tuesdaySpots.add(0);
                            LineDataSet set1 = new LineDataSet(yTuesdayValues, "Tuesday Spots Taken");
                            set1.setFillAlpha(110);
                            set1.setColor(Color.GREEN);
                            set1.setCircleColor(Color.GREEN);
                            dataSets.add(set1);
                        } else if (wednesdaySpots.size() == 24) {
                            int sum = wednesdaySpots.stream().mapToInt(Integer::intValue).sum() / 24;
                            TablePopulate.setTableReports(name, finalDay, Integer.toString(sum), tl, formattedTime, ReportByWeekActivity.this);
                            wednesdaySpots.add(0);
                            LineDataSet set1 = new LineDataSet(yWednesdayValues, "Wednesday Spots Taken");
                            set1.setFillAlpha(110);
                            set1.setColor(Color.YELLOW);
                            set1.setCircleColor(Color.YELLOW);
                            dataSets.add(set1);
                        } else if (thursdaySpots.size() == 24) {
                            int sum = thursdaySpots.stream().mapToInt(Integer::intValue).sum() / 24;
                            TablePopulate.setTableReports(name, finalDay, Integer.toString(sum), tl, formattedTime, ReportByWeekActivity.this);
                            thursdaySpots.add(0);
                            LineDataSet set1 = new LineDataSet(yThursdayValues, "Thursday Spots Taken");
                            set1.setFillAlpha(110);
                            set1.setColor(0xFFFFC0CB);
                            set1.setCircleColor(0xFFFFC0CB);
                            dataSets.add(set1);
                        } else if (fridaySpots.size() == 24) {
                            int sum = fridaySpots.stream().mapToInt(Integer::intValue).sum() / 24;
                            TablePopulate.setTableReports(name, finalDay, Integer.toString(sum), tl, formattedTime, ReportByWeekActivity.this);
                            fridaySpots.add(0);
                            LineDataSet set1 = new LineDataSet(yFridayValues, "Friday Spots Taken");
                            set1.setFillAlpha(110);
                            set1.setColor(0xFFFFA500);
                            set1.setCircleColor(0xFFFFA500);
                            dataSets.add(set1);
                        } else if (saturdaySpots.size() == 24) {
                            int sum = saturdaySpots.stream().mapToInt(Integer::intValue).sum() / 24;
                            TablePopulate.setTableReports(name, finalDay, Integer.toString(sum), tl, formattedTime, ReportByWeekActivity.this);
                            saturdaySpots.add(0);
                            LineDataSet set1 = new LineDataSet(ySaturdayValues, "Saturday Spots Taken");
                            set1.setFillAlpha(110);
                            set1.setColor(Color.BLUE);
                            set1.setCircleColor(Color.BLUE);
                            dataSets.add(set1);
                        }
                    }
                    LineData data = new LineData(dataSets);
                    lineChart.getDescription().setText("X-value = Hours of Day, Y-value = Spots Taken");
                    lineChart.getLegend().setWordWrapEnabled(true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsitemmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mainActivityMenu) {
            Intent intent = new Intent(ReportByWeekActivity.this, DialogFlowChatActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.moreItemMenu) {
            Intent intent = new Intent(ReportByWeekActivity.this, SelectMoreOptionsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ReportByWeekActivity.this.selectedGarageFromSpinner = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
