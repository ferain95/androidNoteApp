package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SecondActivity extends AppCompatActivity {


    private TableLayout logTable;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        logTable = findViewById(R.id.log_table);
        sharedPreferences = getSharedPreferences("ButtonData", Context.MODE_PRIVATE);

        // Display log of button clicks
        displayLog();
    }

    private void displayLog() {
        Map<String, ?> allEntries = sharedPreferences.getAll();

        List<String> logKeys = new ArrayList<>(allEntries.keySet());

        // Sort logKeys based on date in descending order
        Collections.sort(logKeys, new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                // Compare dates in reverse order
                return key2.compareTo(key1);
            }
        });

        // Clear existing rows before adding new ones
        logTable.removeAllViews();

        // Add rows to the table layout in the sorted order
        for (String key : logKeys) {
            if (!key.equals("buttons")) { // Skip the buttons entry
                String dateTime = key;
                String buttonName = allEntries.get(key).toString();
                addTableRow(buttonName, dateTime);
            }
        }

//        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            String dateTime = entry.getKey();
//            String buttonName = entry.getValue().toString();
//            if (!Objects.equals(dateTime, "buttons")) {
//                addTableRow(buttonName, dateTime);
//            }
//        }
    }

    private void addTableRow(String buttonName, String dateTime) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        row.setLayoutParams(layoutParams);

        TextView nameTextView = new TextView(this);
        nameTextView.setText(buttonName);
        nameTextView.setPadding(20, 10, 20, 10);

        TextView dateTimeTextView = new TextView(this);
        dateTimeTextView.setText(dateTime);
        dateTimeTextView.setPadding(20, 10, 20, 10);

        Button removeButton = new Button(this);
        removeButton.setText("-");
        removeButton.setLayoutParams(new TableRow.LayoutParams(50, 100)); // Example size
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the log entry associated with this row
                removeLogEntry(buttonName, dateTime);
                // Remove the row from the table layout
                logTable.removeView(row);
            }
        });

        row.addView(nameTextView);
        row.addView(dateTimeTextView);
        row.addView(removeButton);

        logTable.addView(row);
    }

    private void removeLogEntry(String buttonName, String dateTime) {
        // Construct a unique key for the log entry based on button name and date/time
//        String key = buttonName + "|" + dateTime;

        // Remove the log entry from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(dateTime);
        editor.apply();

        // Also remove the row from the table layout
        TableRow rowToRemove = findRowToRemove(buttonName, dateTime);
        if (rowToRemove != null) {
            logTable.removeView(rowToRemove);
            Log.d("RemoveLogEntry", "Row removed from table layout");
        } else {
            Log.e("RemoveLogEntry", "Row not found in table layout");
        }

        // Update the set of buttons in SharedPreferences
        Set<String> buttonSet = sharedPreferences.getStringSet("buttons", new HashSet<>());
        buttonSet.remove(buttonName);
        editor.putStringSet("buttons", buttonSet);
        editor.apply();

        Log.d("RemoveLogEntry", "Log entry removed successfully");
    }

    private TableRow findRowToRemove(String buttonName, String dateTime) {
        int rowCount = logTable.getChildCount();
        for (int i = 0; i < rowCount; i++) {
            View child = logTable.getChildAt(i);
            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;
                TextView nameTextView = (TextView) row.getChildAt(0);
                TextView dateTimeTextView = (TextView) row.getChildAt(1);
                String name = nameTextView.getText().toString();
                String date = dateTimeTextView.getText().toString();
                if (name.equals(buttonName) && date.equals(dateTime)) {
                    Log.d("FindRowToRemove", "Row found in table layout");
                    return row;
                }
            }
        }
        Log.e("FindRowToRemove", "Row not found in table layout");
        return null;
    }

}