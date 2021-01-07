package com.example.expensestrackerapp;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensestrackerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/** newInstance
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {
    private static final String TAG = "Dashboard Fragment: ";

    // Floating button
    private FloatingActionButton fab_main_btn, fab_income_btn, fab_expenses_btn;

    // Floating button textview
    private TextView fab_income_textView, fab_expenses_textView;

    private boolean isOpen = false;

    private Animation fadeOpen, fadeClose;

    // Dashboard income/expenses sum
    private TextView totalIncome, totalExpenses;

    private FirebaseAuth fAuth;
    private DatabaseReference mIncomeDatabase, mExpensesDatabase;

    // Recycler views
    private RecyclerView mRecyclerIncome, mRecyclerExpenses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = fAuth.getCurrentUser();
        String uid = fAuth.getUid();
        //Log.d(TAG, "UID: " + uid);

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpensesDatabase = FirebaseDatabase.getInstance().getReference().child("ExpensesData").child(uid);

        // Connect Floating button with layout
        fab_main_btn = myView.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myView.findViewById(R.id.income_ft_btn);
        fab_expenses_btn = myView.findViewById(R.id.expenses_ft_btn);

        fab_income_textView = myView.findViewById(R.id.income_ft_text);
        fab_expenses_textView = myView.findViewById(R.id.expenses_ft_text);

        totalIncome = myView.findViewById(R.id.income_set_data);
        totalExpenses = myView.findViewById(R.id.expenses_set_data);

        // Connecting animations
        fadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        fadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
                floatingButtonAnimation();
            }
        });

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int mTotalIncome = 0;
                for (DataSnapshot mysnap:snapshot.getChildren()){
                    Data data = mysnap.getValue(Data.class);
                    mTotalIncome += data.getAmount();
                }
                String incomeResult = String.valueOf(mTotalIncome);
                totalIncome.setText(incomeResult);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mExpensesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int mTotalExpenses = 0;
                for (DataSnapshot mysnap:snapshot.getChildren()){
                    Data data = mysnap.getValue(Data.class);
                    mTotalExpenses += data.getAmount();
                }
                String expensesResult = String.valueOf(mTotalExpenses);
                totalExpenses.setText(expensesResult);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Recycler income
        mRecyclerIncome = myView.findViewById(R.id.recycler_income);
        mRecyclerExpenses = myView.findViewById(R.id.recycler_expenses);

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpenses = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpenses.setStackFromEnd(true);
        layoutManagerExpenses.setReverseLayout(true);
        mRecyclerExpenses.setHasFixedSize(true);
        mRecyclerExpenses.setLayoutManager(layoutManagerExpenses);

        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(
                Data.class,
                R.layout.dashboard_income,
                IncomeViewHolder.class,
                mIncomeDatabase
        ) {
            @Override
            protected void populateViewHolder(IncomeViewHolder incomeViewHolder, Data data, int position) {
                incomeViewHolder.setIncomeType(data.getType());
                incomeViewHolder.setIncomeAmount(data.getAmount());
                incomeViewHolder.setIncomeDate(data.getDate());
            }
        };

        mRecyclerIncome.setAdapter(incomeAdapter);

        FirebaseRecyclerAdapter<Data, ExpensesViewHolder> expensesAdapter = new FirebaseRecyclerAdapter<Data, ExpensesViewHolder>(
                Data.class,
                R.layout.dashboard_expenses,
                ExpensesViewHolder.class,
                mExpensesDatabase
        ) {
            @Override
            protected void populateViewHolder(ExpensesViewHolder expensesViewHolder, Data data, int position) {
                expensesViewHolder.setExpensesType(data.getType());
                expensesViewHolder.setExpensesAmount(data.getAmount());
                expensesViewHolder.setExpensesDate(data.getDate());
            }
        };

        mRecyclerExpenses.setAdapter(expensesAdapter);
    }

    private void floatingButtonAnimation(){
        if (isOpen){
            fab_income_btn.startAnimation(fadeClose);
            fab_expenses_btn.startAnimation(fadeClose);
            fab_income_btn.setClickable(false);
            fab_expenses_btn.setClickable(false);

            fab_income_textView.startAnimation(fadeClose);
            fab_expenses_textView.startAnimation(fadeClose);
            fab_income_textView.setClickable(false);
            fab_expenses_textView.setClickable(false);
            isOpen = false;
        } else {
            fab_income_btn.startAnimation(fadeOpen);
            fab_expenses_btn.startAnimation(fadeOpen);
            fab_income_textView.setClickable(true);
            fab_expenses_textView.setClickable(true);

            fab_income_textView.startAnimation(fadeOpen);
            fab_expenses_textView.startAnimation(fadeOpen);
            fab_income_textView.setClickable(true);
            fab_expenses_textView.setClickable(true);
            isOpen = true;
        }
    }

    private void addData() {
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDataInsert();
            }
        });

        fab_expenses_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expensesDataInsert();
            }
        });

    }

    public void incomeDataInsert() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.custom_layout_for_insert_data, null);

        myDialog.setView(myView);

        AlertDialog dialog = myDialog.create();

        EditText editAmount = myView.findViewById(R.id.amount_edit_text);
        EditText editType = myView.findViewById(R.id.type_edit_text);
        EditText editNote = myView.findViewById(R.id.note_edit_text);

        Button btnSave = myView.findViewById(R.id.btnSubmit);
        Button btnCancel = myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)){
                    editType.setError("Required Field...");
                    return;
                }

                if (TextUtils.isEmpty(amount)){
                    editAmount.setError("Required Field...");
                    return;
                }

                int amountInt = Integer.parseInt(amount);

                if (TextUtils.isEmpty(note)){
                    editNote.setError("Required Field...");
                    return;
                }

                String id = mIncomeDatabase.push().getKey();
                //Log.d(TAG, "id: " + id);
                String mDate = DateFormat.getInstance().format(new Date());
                Data data = new Data(amountInt, type, note, id, mDate);
                mIncomeDatabase.child(id).setValue(data);
                //mIncomeDatabase.setValue(data);
                Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show();

                floatingButtonAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingButtonAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void expensesDataInsert(){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.custom_layout_for_insert_data, null);
        myDialog.setView(myView);

        AlertDialog dialog = myDialog.create();

        EditText editAmount = myView.findViewById(R.id.amount_edit_text);
        EditText editType = myView.findViewById(R.id.type_edit_text);
        EditText editNote = myView.findViewById(R.id.note_edit_text);

        Button btnSave = myView.findViewById(R.id.btnSubmit);
        Button btnCancel = myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)){
                    editType.setError("Required Field...");
                    return;
                }

                if (TextUtils.isEmpty(amount)){
                    editAmount.setError("Required Field...");
                    return;
                }

                int amountInt = Integer.parseInt(amount);

                if (TextUtils.isEmpty(note)){
                    editNote.setError("Required Field...");
                    return;
                }

                String id = mExpensesDatabase.push().getKey();
                //Log.d(TAG, "id: " + id);
                String mDate = DateFormat.getInstance().format(new Date());
                Data data = new Data(amountInt, type, note, id, mDate);
                mExpensesDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show();

                floatingButtonAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingButtonAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{
        View mIncomeView;

        public IncomeViewHolder(View itemView){
            super(itemView);
            mIncomeView = itemView;
        }

        public void setIncomeType(String type){
            TextView mType = mIncomeView.findViewById(R.id.income_dashboard_type);
            mType.setText(type);
        }

        public void setIncomeAmount(Integer amount){
            TextView mAmount = mIncomeView.findViewById(R.id.income_dashboard_amount);
            mAmount.setText(String.valueOf(amount));
        }

        public void setIncomeDate(String date){
            TextView mDate = mIncomeView.findViewById(R.id.income_dashboard_date);
            mDate.setText(date);
        }
    }

    public static class ExpensesViewHolder extends RecyclerView.ViewHolder{
        View mExpensesView;

        public ExpensesViewHolder(View itemView){
            super(itemView);
            mExpensesView = itemView;
        }

        public void setExpensesType(String type){
            TextView mType = mExpensesView.findViewById(R.id.expenses_dashboard_type);
            mType.setText(type);
        }

        public void setExpensesAmount(Integer amount){
            TextView mAmount = mExpensesView.findViewById(R.id.expenses_dashboard_amount);
            mAmount.setText(String.valueOf(amount));
        }

        public void setExpensesDate(String date){
            TextView mDate = mExpensesView.findViewById(R.id.expenses_dashboard_date);
            mDate.setText(date);
        }
    }
}