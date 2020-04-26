package com.example.hacknow2.phase1.contactsview;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.hacknow2.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//View holder implementation for the Recyclerview
class RecyclerViewHolder extends RecyclerView.ViewHolder{

    private View mView;
    private boolean checked = false;
    private TextView mNameTV;
    private TextView mLastContactedTV;
    private CheckBox mCheckBox;
    private String mContactId;
    private String mPhoneNumber;
    RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        mNameTV = itemView.findViewById(R.id.nameTV);
        mLastContactedTV = itemView.findViewById(R.id.lastSpokenToTV);
        mCheckBox = itemView.findViewById(R.id.contactCheckBox);
    }

    void setPhone(String str){
        mPhoneNumber = str;
    }

    void setContactID(String id){
        mContactId = id;
    }

    String getContactId(){
        return mContactId;
    }

    //Checks to see if the checkbox state was changed when the recycler item view is touched
    void setOnCheckedChangedListener(View.OnClickListener listener){

        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCheckBox.isChecked() != checked){
                    checked = mCheckBox.isChecked();
                    listener.onClick(mCheckBox);
                }
            }
        });
    }

    void setName(String str){
        mNameTV.setText(str);
    }

    void setLastContacted(String str){
        mLastContactedTV.setText(str);
    }
}
