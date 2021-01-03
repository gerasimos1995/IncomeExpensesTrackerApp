package com.example.expensestrackerapp;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expenses_btn;

    // Floating button textview
    private TextView fab_income_textView;
    private TextView fab_expenses_textView;

    private boolean isOpen = false;

    private Animation fadeOpen, fadeClose;

    private FirebaseAuth fAuth;
    private DatabaseReference mIncomeDatabase, mExpensesDatabase;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public DashboardFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment DashboardFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static DashboardFragment newInstance(String param1, String param2) {
//        DashboardFragment fragment = new DashboardFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

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

        return myView;
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
}