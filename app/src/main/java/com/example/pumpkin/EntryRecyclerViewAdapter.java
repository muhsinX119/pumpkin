package com.example.pumpkin;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class EntryRecyclerViewAdapter extends RecyclerView.Adapter<EntryRecyclerViewAdapter.ViewHolder>{

    private ArrayList<DbStructure> entry = new ArrayList<>();
    private Context context;

    public EntryRecyclerViewAdapter (Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //Change card background based on Date-Month value
        /*TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        @ColorInt int col = typedValue.data;
        @ColorInt int col1 = Color.parseColor("#FFFFFF");
        @ColorInt int col2 = Color.parseColor("#00d0ff");

        DateFormat dateFormat2 = new SimpleDateFormat("MMMM");
        String strDate2 = dateFormat2.format(entry.get(position).getDate());
        String[] months = {"January","March","May","July","September","November"};
        boolean contains = Arrays.stream(months).anyMatch(strDate2::equals);
        if (contains) {
            //holder.parent.setBackgroundResource(R.color.teal_200);
            theme.resolveAttribute(R.attr.colorSecondary, typedValue, true);
            col = typedValue.data;
            holder.entryCard.setCardBackgroundColor(col1);
        } else {
            theme.resolveAttribute(R.attr.colorSurface, typedValue, true);
            col = typedValue.data;
            holder.entryCard.setCardBackgroundColor(col1);
        }*/

        holder.textViewExpenseName.setText(entry.get(position).getExpense());
        holder.textViewDate.setText(formattedDate(entry.get(position).getDate()));
        holder.textViewTag.setText(entry.get(position).getTag());
        holder.textViewAmount.setText(String.valueOf(entry.get(position).getAmount()));

    }

    @Override
    public int getItemCount() {
        return entry.size();
    }

    public void setEntry(ArrayList<DbStructure> entry) {
        this.entry = entry;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewExpenseName;
        private TextView textViewTag, textViewAmount;
        private TextView textViewDate;
        private MaterialCardView entryCard;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewExpenseName = itemView.findViewById(R.id.textViewExpenseName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTag = itemView.findViewById(R.id.textViewTag);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            entryCard = itemView.findViewById(R.id.entryCard);
            //parent = itemView.findViewById(R.id.parent2);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int pos = getAdapterPosition();
            Intent myIntent = new Intent(context, com.example.pumpkin.EditRecyclerEntry.class);
            //myIntent.putExtra("key", value); //Optional parameters
            myIntent.putExtra("Expense",entry.get(pos).getExpense());
            myIntent.putExtra("Tag", entry.get(pos).getTag());
            myIntent.putExtra("Amount", entry.get(pos).getAmount());
            //myIntent.putExtra("Date", toString().valueOf(entry.get(pos).getDate()));
            myIntent.putExtra("Date", entry.get(pos).getDate());
            //myIntent.putExtra("Id", toString().valueOf(entry.get(getAdapterPosition()).getId()));
            myIntent.putExtra("Id", entry.get(pos).getId());
            context.startActivity(myIntent);


        }
    }

    public String formattedDate (long date) {
        DateFormat dateFormat = new SimpleDateFormat("E, d/MMM/yy");
        String strDate = dateFormat.format(date);
        return strDate;
    }

}
