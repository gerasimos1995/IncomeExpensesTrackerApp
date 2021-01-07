package com.example.expensestrackerapp;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensestrackerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class IncomeFragment extends Fragment {

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter recyclerAdapter;

    // Total Income TextView
    private TextView incomeTotalTextView;

    // Update TextView
    private EditText editAmount;
    private EditText editType;
    private EditText editNote;

    // Button for update and delete
    private Button btnUpdate, btnDelete;

    // Data item
    private String type, note;
    private int amount;
    private String post_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_income, container, false);

        // Get current user and connection to IncomeData collection
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        incomeTotalTextView = myView.findViewById(R.id.income_text_result);

        // create the recycler view for adding all the items of income
        recyclerView = myView.findViewById(R.id.recycler_id_income);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalValue = 0;

                for (DataSnapshot my_snapshot:snapshot.getChildren()){
                    Data data = my_snapshot.getValue(Data.class);
                    totalValue += data.getAmount();
                    String stTotalValue = String.valueOf(totalValue);
                    incomeTotalTextView.setText(stTotalValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class, R.layout.income_recycler_data, MyViewHolder.class, mIncomeDatabase
        ){
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, Data model, int position) {
                myViewHolder.setType(model.getType());
                myViewHolder.setNote(model.getNote());
                myViewHolder.setDate(model.getDate());
                myViewHolder.setAmount(model.getAmount());

                myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();

                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();

                        updateDataItem();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public MyViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        private void setType(String type){
            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        private void setNote(String note){
            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        private void setAmount(int amount){
            TextView mAmount = mView.findViewById(R.id.amount_txt_income);
            String sAmount = String.valueOf(amount);
            mAmount.setText(sAmount);
        }
    }

    private void updateDataItem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.update_data_item, null);

        myDialog.setView(myView);

        editAmount = myView.findViewById(R.id.amount_edit_delete_text);
        editType = myView.findViewById(R.id.type_edit_delete_text);
        editNote = myView.findViewById(R.id.note_edit_delete_text);

        // Set data to editText for existing list item
        editType.setText(type);
        editType.setSelection(type.length());
        editNote.setText(note);
        editNote.setSelection(note.length());
        editAmount.setText(String.valueOf(amount));
        editAmount.setSelection(String.valueOf(amount).length());

        btnDelete = myView.findViewById(R.id.btnDelete);
        btnUpdate = myView.findViewById(R.id.btnUpdate);

        AlertDialog dialog = myDialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = editType.getText().toString().trim();
                note = editNote.getText().toString().trim();
                amount = Integer.parseInt(editAmount.getText().toString().trim());
                String date = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(amount, type, note, post_key, date);

                mIncomeDatabase.child(post_key).setValue(data);
                dialog.dismiss();
                Toast.makeText(getActivity(), "Item updated successfully", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}