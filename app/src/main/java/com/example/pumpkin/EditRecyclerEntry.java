package com.example.pumpkin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EditRecyclerEntry extends AppCompatActivity {

    private EditText editRecyclerEditTextExpense, editRecyclerEditTextTag, editRecyclerEditTextAmount, editRecyclerEditTextID;
    private TextView editRecyclerEditTextDate;
    private View view;
    private String expense, tag;
    private int id;
    private long date, date2;
    private float amount;
    private DbStructure book2 = new DbStructure();
    private DbHelper dataBaseHelper = new DbHelper(EditRecyclerEntry.this);
    private MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_recycler_activity);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#00000000"));

        //Hide ID box
        editRecyclerEditTextID = findViewById(R.id.editRecyclerEditTextID);
        editRecyclerEditTextID.setVisibility(View.GONE);

        intent = getIntent();
        editRecyclerEditTextDate = findViewById(R.id.editRecyclerEditTextDate);

        materialDateBuilder.setTitleText("");
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();

        editRecyclerEditTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick (Long selection) {
                //editRecyclerEditTextDate.setText(formattedDate(materialDatePicker.getHeaderText()));

                    editRecyclerEditTextDate.setText(formattedDate(materialDatePicker.getSelection()));
                    date2 = materialDatePicker.getSelection()+(System.currentTimeMillis()%86400000);
            }
        });



        editRecyclerEditTextExpense = findViewById(R.id.editRecyclerEditTextExpense);
        editRecyclerEditTextTag = findViewById(R.id.editRecyclerEditTextTag);
        editRecyclerEditTextAmount = findViewById(R.id.editRecyclerEditTextAmount);
        editRecyclerEditTextDate = findViewById(R.id.editRecyclerEditTextDate);
        editRecyclerEditTextID = findViewById(R.id.editRecyclerEditTextID);


        expense = intent.getStringExtra("Expense");
        tag = intent.getStringExtra("Tag");
        //date = intent.getStringExtra("Date");
        date = intent.getLongExtra("Date",0);
        date2 = date;
        //amount = intent.getStringExtra("Amount");
        amount = intent.getFloatExtra("Amount", (float) 0.0);
        //id = intent.getStringExtra("Id");
        id = intent.getIntExtra("Id",-1);


        editRecyclerEditTextExpense.setText(expense);
        editRecyclerEditTextTag.setText(tag);
        editRecyclerEditTextAmount.setText(toString().valueOf(amount));
        editRecyclerEditTextDate.setText(formattedDate(date));
        editRecyclerEditTextID.setText(toString().valueOf(id));

    }

    public String formattedDate (long date) {
        DateFormat dateFormat = new SimpleDateFormat("E, d/MMM/yy");
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public void onClickUpdate(View v){

        editRecyclerEditTextExpense = findViewById(R.id.editRecyclerEditTextExpense);
        editRecyclerEditTextTag = findViewById(R.id.editRecyclerEditTextTag);
        editRecyclerEditTextAmount = findViewById(R.id.editRecyclerEditTextAmount);
        editRecyclerEditTextDate = findViewById(R.id.editRecyclerEditTextDate);


        expense = editRecyclerEditTextExpense.getText().toString();
        tag = editRecyclerEditTextTag.getText().toString();
        amount = Float.parseFloat(editRecyclerEditTextAmount.getText().toString());
        id = Integer.parseInt(toString().valueOf(intent.getIntExtra("Id",-1)));

        book2 = new DbStructure(id, expense, tag, amount, date2);

        dataBaseHelper.updateOne(book2);

        finish();

    }

    public void onClickDelete (View v) {

        dataBaseHelper.deleteOne(toString().valueOf(intent.getIntExtra("Id",-1)));
        finish();

    }



    /*
    @Override
    public void onResume(){
        super.onResume();
        //for hiding status bar
        view = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        view.setSystemUiVisibility(uiOptions);

    }
    */
}
