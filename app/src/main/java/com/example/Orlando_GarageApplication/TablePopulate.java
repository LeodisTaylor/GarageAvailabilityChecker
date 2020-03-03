package com.example.Orlando_GarageApplication;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.Orlando_GarageApplication.activities.ReportByDayActivity;

//This class creates and populates the tables used in SearchGarageActivity, ReportByDayActivity, and ReportByWeekActivity
//This is spaghetti please excuse how bad it is, I was in a time crunch
public class TablePopulate {
    public static void setTableHeaderSearch(TableLayout tl, Context context) {

        TableRow.LayoutParams params2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params2.setMargins(3, 1, 3, 2);
        params2.weight = 1;

        TableRow trHeader = new TableRow(context);
        trHeader.setGravity(Gravity.CENTER);
        trHeader.setLayoutParams(params2);
        trHeader.setBackgroundColor(Color.parseColor("#147efb"));

        TextView DateTimeHeader = new TextView(context);
        DateTimeHeader.setGravity(Gravity.CENTER);
        DateTimeHeader.setText("Date/Time");
        DateTimeHeader.setBackgroundColor(Color.parseColor("#147efb"));
        DateTimeHeader.setLayoutParams(params2);
        trHeader.addView(DateTimeHeader);

        TextView garageNameHeader = new TextView(context);
        garageNameHeader.setGravity(Gravity.CENTER);
        garageNameHeader.setText("Garage");
        garageNameHeader.setBackgroundColor(Color.parseColor("#147efb"));
        garageNameHeader.setLayoutParams(params2);
        trHeader.addView(garageNameHeader);

        TextView spotsLeftHeader = new TextView(context);
        spotsLeftHeader.setGravity(Gravity.CENTER);
        spotsLeftHeader.setText("Spots Left");
        spotsLeftHeader.setBackgroundColor(Color.parseColor("#147efb"));
        spotsLeftHeader.setLayoutParams(params2);
        trHeader.addView(spotsLeftHeader);

        TextView percentFullHeader = new TextView(context);
        percentFullHeader.setGravity(Gravity.CENTER);
        percentFullHeader.setText("% Filled");
        percentFullHeader.setBackgroundColor(Color.parseColor("#147efb"));
        percentFullHeader.setLayoutParams(params2);
        trHeader.addView(percentFullHeader);
        tl.addView(trHeader, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public static void setTableSearch(String garageNameFill, String spotsLeftFill, String percentFullFill, TableLayout tl, String dateTime, Context context) {

        TableRow.LayoutParams params2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params2.setMargins(3, 1, 3, 2);
        params2.weight = 1;

        TableRow tr = new TableRow(context);
        tr.setGravity(Gravity.CENTER);
        tr.setLayoutParams(params2);
        tr.setBackgroundColor(Color.parseColor("#147efb"));


        TextView DateTime = new TextView(context);
        DateTime.setGravity(Gravity.CENTER);
        DateTime.setText(dateTime);
        DateTime.setTextColor(Color.parseColor("#147efb"));
        DateTime.setBackgroundColor(Color.parseColor("#ffffff"));
        DateTime.setLayoutParams(params2);
        tr.addView(DateTime);

        TextView garageName = new TextView(context);
        garageName.setGravity(Gravity.CENTER);
        garageName.setText(garageNameFill);
        garageName.setTextColor(Color.parseColor("#147efb"));
        garageName.setBackgroundColor(Color.parseColor("#ffffff"));
        garageName.setLayoutParams(params2);
        tr.addView(garageName);

        TextView spotsLeft = new TextView(context);
        spotsLeft.setGravity(Gravity.CENTER);
        spotsLeft.setText(spotsLeftFill);
        spotsLeft.setTextColor(Color.parseColor("#147efb"));
        spotsLeft.setBackgroundColor(Color.parseColor("#ffffff"));
        spotsLeft.setLayoutParams(params2);
        tr.addView(spotsLeft);

        TextView percentFull = new TextView(context);
        percentFull.setGravity(Gravity.CENTER);
        percentFull.setText(percentFullFill + "%");
        percentFull.setTextColor(Color.parseColor("#147efb"));
        percentFull.setBackgroundColor(Color.parseColor("#ffffff"));
        percentFull.setLayoutParams(params2);
        tr.addView(percentFull);

        tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public static void setTableHeaderReports(TableLayout tl, Context context) {

        TableRow.LayoutParams params2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params2.setMargins(3, 1, 3, 2);
        params2.weight = 1;

        TableRow trHeader = new TableRow(context);
        trHeader.setGravity(Gravity.CENTER);
        trHeader.setLayoutParams(params2);
        trHeader.setBackgroundColor(Color.parseColor("#147efb"));

        TextView DateTimeHeader = new TextView(context);
        DateTimeHeader.setGravity(Gravity.CENTER);
        DateTimeHeader.setText("Date/Time");
        DateTimeHeader.setBackgroundColor(Color.parseColor("#147efb"));
        DateTimeHeader.setLayoutParams(params2);
        trHeader.addView(DateTimeHeader);

        TextView garageNameHeader = new TextView(context);
        garageNameHeader.setGravity(Gravity.CENTER);
        garageNameHeader.setText("Garage");
        garageNameHeader.setBackgroundColor(Color.parseColor("#147efb"));
        garageNameHeader.setLayoutParams(params2);
        trHeader.addView(garageNameHeader);

        TextView spotsLeftHeader = new TextView(context);
        if (context instanceof ReportByDayActivity) {
            spotsLeftHeader.setText("Hour of Day");
        } else {
            spotsLeftHeader.setText("Day of Week");
        }
        spotsLeftHeader.setGravity(Gravity.CENTER);
        spotsLeftHeader.setBackgroundColor(Color.parseColor("#147efb"));
        spotsLeftHeader.setLayoutParams(params2);
        trHeader.addView(spotsLeftHeader);

        TextView percentFullHeader = new TextView(context);
        percentFullHeader.setGravity(Gravity.CENTER);
        percentFullHeader.setText("Average Left");
        percentFullHeader.setBackgroundColor(Color.parseColor("#147efb"));
        percentFullHeader.setLayoutParams(params2);
        trHeader.addView(percentFullHeader);
        tl.addView(trHeader, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public static void setTableReports(String garageNameFill, String dayOfWeekFill, String averageSpotsLeftFill, TableLayout tl, String dateTime, Context context) {

        TableRow.LayoutParams params2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params2.setMargins(3, 1, 3, 2);
        params2.weight = 1;

        TableRow tr = new TableRow(context);
        tr.setGravity(Gravity.CENTER);
        tr.setLayoutParams(params2);
        tr.setBackgroundColor(Color.parseColor("#147efb"));


        TextView DateTime = new TextView(context);
        DateTime.setGravity(Gravity.CENTER);
        DateTime.setText(dateTime);
        DateTime.setTextColor(Color.parseColor("#147efb"));
        DateTime.setBackgroundColor(Color.parseColor("#ffffff"));
        DateTime.setLayoutParams(params2);
        tr.addView(DateTime);

        TextView garageName = new TextView(context);
        garageName.setGravity(Gravity.CENTER);
        garageName.setText(garageNameFill);
        garageName.setTextColor(Color.parseColor("#147efb"));
        garageName.setBackgroundColor(Color.parseColor("#ffffff"));
        garageName.setLayoutParams(params2);
        tr.addView(garageName);

        TextView DayOfWeek = new TextView(context);
        DayOfWeek.setGravity(Gravity.CENTER);
        DayOfWeek.setText(dayOfWeekFill);
        DayOfWeek.setTextColor(Color.parseColor("#147efb"));
        DayOfWeek.setBackgroundColor(Color.parseColor("#ffffff"));
        DayOfWeek.setLayoutParams(params2);
        tr.addView(DayOfWeek);

        TextView AverageSpotsLeft = new TextView(context);
        AverageSpotsLeft.setGravity(Gravity.CENTER);
        AverageSpotsLeft.setText(averageSpotsLeftFill);
        AverageSpotsLeft.setTextColor(Color.parseColor("#147efb"));
        AverageSpotsLeft.setBackgroundColor(Color.parseColor("#ffffff"));
        AverageSpotsLeft.setLayoutParams(params2);
        tr.addView(AverageSpotsLeft);

        tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }
}
