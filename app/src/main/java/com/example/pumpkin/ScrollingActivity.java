package com.example.pumpkin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.FileUtils;
import android.os.health.SystemHealthManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.example.pumpkin.databinding.ActivityScrollingBinding;
import com.example.pumpkin.databinding.ContentScrollingBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity {

    private ActivityScrollingBinding binding;
    private ContentScrollingBinding cbinding;
    private DbHelper dataBaseHelper = new DbHelper(ScrollingActivity.this);

    //Views declaration
    private CardView bottomCardView;
    private CollapsingToolbarLayout toolBarLayout;
    private AppBarLayout appBar;
    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionButton fab;
    private TextView textExpense, textAmount,textSum, textMonth, expensePerDay;
    private Button textViewDateSelect;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private RecyclerView entryRecyclerView;
    private ArrayList<String> uniqueTags = new ArrayList<String>();
    private long date2;
    private AutoCompleteTextView textTag;
    private DateFormat dateFormat;
    private MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
    private boolean permissionStorage;
    private String pauseExpense, pauseTag, pauseAmount;
    private long pauseDate, timeSincePause=0;

    ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    permissionStorage = true;
                    Snackbar.make(getWindow().getDecorView(), "Permission Granted. Tap on the Pumpkin to create report", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } else {

                    permissionStorage = false;

                    Snackbar.make(getWindow().getDecorView(), "Storage permission is required to save report. Please grant the permission", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        textExpense = binding.textExpense;
        textTag = binding.textTag;
        textAmount = binding.textAmount;
        textSum = binding.textSum;
        textMonth = binding.textMonth;
        expensePerDay = binding.expensePerDay;
        appBar = binding.appBar;
        toolBarLayout = binding.toolbarLayout;
        fab = binding.fab;
        toolbarLayout = binding.toolbarLayout;
        textViewDateSelect = binding.textViewDateSelect;

        //initialize amount editText with 0.00
        textAmount.setText("0.00");
        //initialize date select in main activity
        date2 = System.currentTimeMillis();

        //initialize pause values
        pauseAmount = textAmount.getText().toString();
        pauseTag = textTag.getText().toString();
        pauseDate = date2;
        pauseExpense = textExpense.getText().toString();

        //Setting statusbar color
        /*TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorSurface, typedValue, true);
        @ColorInt int color = typedValue.data;*/

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#00000000"));
        /*window.setStatusBarColor(color);*/

        //To hide FAB on AppBar collapse
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                verticalOffset = verticalOffset-600;
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    // Collapsed
                    fab.hide();
                } else if (verticalOffset == 0) {
                    // Expanded
                    //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                } else {
                    // Somewhere in between
                    fab.show();
                   //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                }
            }
        });


        //Date picker in main activity
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();
        textViewDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick (Long selection) {
                //editRecyclerEditTextDate.setText(formattedDate(materialDatePicker.getHeaderText()));

                textViewDateSelect.setText(formattedDate(materialDatePicker.getSelection()));
                date2 = materialDatePicker.getSelection()+(System.currentTimeMillis()%86400000);
            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get values from textfields
                String expense, tag, amountString, date;
                expense = textExpense.getText().toString();
                tag = textTag.getText().toString();
                amountString = textAmount.getText().toString();
                DbStructure entry = new DbStructure();

                if (expense.equals("") || amountString.equals("0.00")) {
                    Snackbar.make(view, "Empty Fields", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    float amount = Float.parseFloat(amountString);
                    try {
                        entry = new DbStructure(1, expense, tag, amount, date2);
                    } catch (NumberFormatException ignore) {}

                    dataBaseHelper.addOne(entry);
                    recyclerViewDataUpdate();

                    //Clear Text Inputs (moved to recyclerViewDataUpdate
                    /*textExpense.setText(null);
                    textTag.setText(null);
                    textAmount.setText("0.00");
                    date2 = System.currentTimeMillis();*/

                    //Set focus to the expense text input
                    textExpense.requestFocus();

                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        recyclerViewDataUpdate();

        if (System.currentTimeMillis()-timeSincePause<120000) {

            Log.d("test", "onResume: "+toString().valueOf(System.currentTimeMillis()-timeSincePause));

            textExpense.setText(pauseExpense);
            textAmount.setText(pauseAmount);
            textTag.setText(pauseTag);
            textViewDateSelect.setText(formattedDate(pauseDate));
            timeSincePause = 0;

        }

        textExpense.requestFocus();

        //update textSum
        //textSum.setText(toString().valueOf(dataBaseHelper.getExpenseSinceMonthStart()));


    }

    @Override
    protected void onPause() {
        super.onPause();

        pauseDate = date2;
        pauseExpense = textExpense.getText().toString();
        pauseTag = textTag.getText().toString();
        pauseAmount = textAmount.getText().toString();
        timeSincePause = System.currentTimeMillis();
        //textAmount.requestFocus();

    }

    private void recyclerViewDataUpdate() {
        ArrayList<DbStructure> table1 = (ArrayList<DbStructure>) dataBaseHelper.getAll();
        entryRecyclerView = findViewById(R.id.entryRecyclerView);
        EntryRecyclerViewAdapter adapter = new EntryRecyclerViewAdapter(this);

        adapter.setEntry(table1);
        entryRecyclerView.setAdapter(adapter);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        //Updating Autocomplete view for unique tags
        uniqueTags = dataBaseHelper.getUniqueTags();
        ArrayAdapter utAdapter = new ArrayAdapter(ScrollingActivity.this, R.layout.drop_down,uniqueTags);
        textTag.setAdapter(utAdapter);

        //Updating expensetrend texts
        float expenseSinceMonthStart = dataBaseHelper.getExpenseSinceMonthStart();
        textSum.setText("₹ "+toString().valueOf(expenseSinceMonthStart));
        dateFormat = new SimpleDateFormat("MMMM");
        String strDate = dateFormat.format(System.currentTimeMillis());
        textMonth.setText(" spent in "+strDate);
        dateFormat = new SimpleDateFormat("d");
        strDate = dateFormat.format(System.currentTimeMillis());
        int dailyExpense = (int) (expenseSinceMonthStart / Integer.parseInt(strDate));
        expensePerDay.setText(toString().valueOf(dailyExpense)+" per day");

        //Clear inputs
        textExpense.setText(null);
        textTag.setText(null);
        textAmount.setText("0.00");
        date2 = System.currentTimeMillis();
        textViewDateSelect.setText(formattedDate(date2));

    }

    public void backupDb() throws IOException {

        final String inFileName = getApplicationContext().getApplicationInfo().dataDir + "/databases/entries.db";
        File dbFile = new File(inFileName);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(dbFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String outFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/entries_backup.db";

        // Open the empty db as the output stream
        OutputStream output = null;
        try {
            output = new FileOutputStream(outFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer))>0){
            try {
                output.write(buffer, 0, length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close the streams
        try {
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        output.close();
        fis.close();

    }

    public void restoreDb(View v) {
        String appDataPath = getApplicationContext().getApplicationInfo().dataDir;

        File dbFolder = new File(appDataPath + "/databases");//Make sure the /databases folder exists
        //dbFolder.mkdir();//This can be called multiple times.
        //dbFolder.delete();
        //EDITED
        File dbFilePath = new File(appDataPath + "/databases/"+"entries.db");

        try {
            //EDITED
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/entries_backup.db");
            FileInputStream inputStream = new FileInputStream(file); //use your database name
            OutputStream outputStream = new FileOutputStream(dbFilePath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer))>0)
            {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

        } catch (IOException e){
            //handle

            e.printStackTrace();
        }
        recyclerViewDataUpdate();
    }

    public String formattedDate (long date) {
        DateFormat dateFormat = new SimpleDateFormat("E, d/MMM/yy");
        return dateFormat.format(date);
    }

    public void exportReport (View v) throws IOException {

        Log.d("Test", "exportReport: exportReport started");

        if (Build.VERSION.SDK_INT > 29) {

            permissionStorage = Environment.isExternalStorageManager();

            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                return;
            }

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {

            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return;

        } else {
            permissionStorage = true;
        }

        if (permissionStorage) {

            File downloadsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"");
            boolean downloadsFolderError = downloadsFolder.mkdirs();
            Log.d("Folder Creation", "exportReport: Pumpkin Reports created "+toString().valueOf(downloadsFolderError)+" "+getPackageName());

            File reportFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Pumpkin Reports");
            /*if (!reportFolder.exists()) {
                boolean folderError = reportFolder.mkdirs();
                Log.d("Folder Creation", "exportReport: "+toString().valueOf(folderError));
            }*/

            boolean folderError = reportFolder.mkdirs();
            Log.d("Folder Creation", "exportReport: Pumpkin Reports created "+toString().valueOf(reportFolder));

            DateFormat dateFormat = new SimpleDateFormat("E_d-MMM-yy_HH-mm");
            String reportName = "pumpkin " + dateFormat.format(System.currentTimeMillis()) + ".csv";
            File file = new File(reportFolder, reportName);
            PrintWriter outFile = new PrintWriter(file);

            outFile.println("ID for Internal Use, Expense Name, Tag, Amount, Date");

            try {
                file.createNewFile();
                ArrayList<DbStructure> table1 = (ArrayList<DbStructure>) dataBaseHelper.getAll();

                int i = 0;
                while (i < table1.size()) {
                    outFile.println(table1.get(i).toCSVLine());
                    i++;
                }

                outFile.close();

                Snackbar.make(v, "Report \"" + reportName + "\" Created", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } catch (IOException e) {
                Snackbar.make(v, "Export Failed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else { Snackbar.make(v, "This is called", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show(); }
        }


    }