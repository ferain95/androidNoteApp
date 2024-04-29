package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

// MainActivity.java
public class MainActivity extends AppCompatActivity {

    private LinearLayout buttonContainer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonContainer = findViewById(R.id.button_container);
        sharedPreferences = getSharedPreferences("ButtonData", Context.MODE_PRIVATE);

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddButtonDialog();
            }
        });

        // Load and display previously saved buttons
        loadButtons();
    }

    // Method to handle Show Logs button click
    public void showLogs(View view) {
        // Create an Intent to navigate to SecondActivity
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }

    private void showAddButtonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_button, null);
        builder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.edit_text);

        builder.setTitle("Add New Button")
                .setPositiveButton("Add", (dialog, id) -> {
                    String buttonName = editText.getText().toString();
                    addButton(buttonName);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void addButton(String buttonName) {
        Button newButton = new Button(this);
        newButton.setText(buttonName);
        newButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newButton.setOnClickListener(v -> {
            // Handle click event of the newly created button
            logButtonClick(buttonName);
            Log.i("created button", buttonName + " pressed this");
            Toast.makeText(MainActivity.this, "Added " + buttonName + " !", Toast.LENGTH_SHORT).show();
        });
        buttonContainer.addView(newButton);

        // Save the button name in SharedPreferences
        saveButton(buttonName);
    }

    private void saveButton(String buttonName) {
        Set<String> buttonSet = sharedPreferences.getStringSet("buttons", new HashSet<>());
        buttonSet.add(buttonName);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("buttons", buttonSet);
        editor.apply();
    }

    private void loadButtons() {
        Set<String> buttonSet = sharedPreferences.getStringSet("buttons", new HashSet<>());
        for (String buttonName : buttonSet) {
            addButton(buttonName);
        }
    }

    private void logButtonClick(String buttonName) {
        // Get current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateTime = sdf.format(new Date());

        // Save button click event in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(dateTime, buttonName);
        editor.apply();
    }
    private String getCurrentDateTime() {
        // Get current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}