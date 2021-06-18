package com.example.pumpkin;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.pumpkin.databinding.ActivityScrollingBinding;
import com.example.pumpkin.databinding.ContentScrollingBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private TextView textExpense, textAmount,textSum, textMonth, expensePerDay, textViewDateSelect;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private RecyclerView entryRecyclerView;
    private ArrayList<String> uniqueTags = new ArrayList<String>();
    private long date2;
    private AutoCompleteTextView textTag;
    private DateFormat dateFormat;
    private MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //cbinding = ContentScrollingBinding.inflate(getLayoutInflater());

        textExpense = binding.textExpense;
        //textTag = binding.textTag;
        textTag = binding.textTag;
        textAmount = binding.textAmount;
        textSum = binding.textSum;
        textMonth = binding.textMonth;
        expensePerDay = binding.expensePerDay;
        appBar = binding.appBar;
        //bottomCardView = binding.bottomCardView;
        toolBarLayout = binding.toolbarLayout;
        fab = binding.fab;
        toolbarLayout = binding.toolbarLayout;
        textViewDateSelect = binding.textViewDateSelect;

        //initialize amount editText with 0.00
        textAmount.setText("0.00");
        //initialize date select in main activity
        textViewDateSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.date_icon, 0, 0, 0);
        date2 = System.currentTimeMillis();

        //exposed dropdown list
        //ArrayList<String> uniqueTags = new ArrayList<String>();
        /*uniqueTags = dataBaseHelper.getUniqueTags();
        ArrayAdapter utAdapter = new ArrayAdapter(this, R.layout.drop_down,uniqueTags);
        textTag.setAdapter(utAdapter);*/




        //getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //ViewGroup.LayoutParams layoutParams = bottomCardView.getLayoutParams();
        //layoutParams.height = displayMetrics.heightPixels;
        //bottomCardView.setLayoutParams(layoutParams);

        //Initialize for status bar color change
        //TypedValue typedValue = new TypedValue();
        //Resources.Theme theme = this.getTheme();
        //theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        //@ColorInt int col = typedValue.data;

        //Setting statusbar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#00000000"));
        //window.setNavigationBarColor(Color.parseColor("#00000000"));
        //window.setNavigationBarColor(Color.parseColor("#00000000"));


        //To hide FAB on AppBar collapse
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                verticalOffset = verticalOffset;
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

                //restoreDb();
                /*try {
                    backupDb();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

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

        //update textSum
        //textSum.setText(toString().valueOf(dataBaseHelper.getExpenseSinceMonthStart()));


    }

    @Override
    protected void onPause() {
        super.onPause();

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
        textSum.setText("$ "+toString().valueOf(expenseSinceMonthStart));
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

    public void backupDb(View v) throws IOException {

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

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(ScrollingActivity.this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String formattedDate (long date) {
        DateFormat dateFormat = new SimpleDateFormat("E, d/MMM/yy");
        String strDate = dateFormat.format(date);
        return strDate;
    }


}