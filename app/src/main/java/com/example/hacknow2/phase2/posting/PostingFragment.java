package com.example.hacknow2.phase2.posting;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hacknow2.R;
import com.example.hacknow2.phase2.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference mDatabase;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostingFragment newInstance(String param1, String param2) {
        PostingFragment fragment = new PostingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_posting, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Most of the code involving the spinner below just allows for a "tooltip". Actual options are "text" or "image"
        Spinner spinner = v.findViewById(R.id.postType);
        String[] options = getResources().getStringArray(R.array.postoptions);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, options){

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

        };

        spinner.setAdapter(adapter);
        return v;
    }

    /**
     * For testing only
     * @return
     */
    private Message getTestMessage(){
        String user = FirebaseAuth.getInstance().getUid();
        return new Message(user, 0, 0, false, true, DateTime.now().toString(), "testtesttest");
    }

    private void createErrorToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    private Message validateForm(){
        ////Begin by validating form
        boolean validationError = false;
        String valErrorText = null;

        View v = getView();

        CheckBox cb = v.findViewById(R.id.publicCheckBox);
        boolean isPublic = cb.isChecked();

        String postType = null;
        Spinner spinner = ((Spinner)v.findViewById(R.id.postType));

        if(spinner.getSelectedItemPosition() > 0) {
            postType = (String) spinner.getSelectedItem();
        }
        else {
            validationError = true;
            createErrorToast(getString(R.string.EmptyFields));
        }

        String postBody = null;
        EditText et = v.findViewById(R.id.postbody);
        if(et.getText()==null || et.getText().length()==0) {
            validationError = true;
            createErrorToast(getString(R.string.EmptyFields));
        }
        else {
            postBody = et.getText().toString();

            //If the user tries to post an image but does not provide a valid url
            //an error will be thrown
            if(!postType.equals("Text")){
                if(!URLUtil.isValidUrl(postBody)) {
                    validationError = true;
                    createErrorToast(getString(R.string.BadURLMessage));
                }
            }
        }

        if(validationError)
            return null;

        String user = FirebaseAuth.getInstance().getUid();
        return new Message(user,0,0,postType.equals("Text"), isPublic, DateTime.now().toString(), postBody);

    }

    private void postMessage(){

        Message m = validateForm();
        if(m!=null){
            String userID = FirebaseAuth.getInstance().getUid();
            DatabaseReference q = m.isPublic ? mDatabase.child("kits").child("tests").child("public").child(m.uid) :
                    mDatabase.child("kits").child("tests").child("private").child(userID).child(m.uid);

            q.setValue(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getActivity(), "Succesfully added content.", Toast.LENGTH_LONG).show();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    createErrorToast(e.getMessage());
                    getActivity().setResult(Activity.RESULT_CANCELED);
                    getActivity().finish();
                }
            });
        }

    }

    //Add menu to actionbar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.postingmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.post:
                postMessage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
