package com.example.hacknow2.phase3;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.hacknow2.R;
import com.example.hacknow2.phase2.Message;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class SendDialog extends DialogFragment {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1000;
    private ArrayList<String> mPhoneNumbers;
    private Message mMessage;
    private OnFinished mCompletionListener;
    public SendDialog(ArrayList<String> phoneNumbers, Message m, OnFinished listener){
        mPhoneNumbers = phoneNumbers;
        mMessage = m;
        mCompletionListener = listener;
    }

    public interface OnFinished{
        void complete(boolean success);
    }

    private String getCautionMessage(){
        int numContacts = 0;
        if(mPhoneNumbers!=null && mPhoneNumbers.size()>0) {
            for (String number :
                    mPhoneNumbers) {
                numContacts++;
            }
        }
        return "Are you sure you'd like to send the highlighted post to " + numContacts + " people?";
    }

    private void requestPermissionsAndSend(){
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        else{
            sendBulkSMS();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getCautionMessage())
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermissionsAndSend();
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCompletionListener.complete(false);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void sendBulkSMS(){
        //TODO: Make sharing possible for images as well.
        //currently only sms possible

        if(!mMessage.isTextOnly){
            Toast.makeText(getActivity(), "Sending MMS not possible at the moment", Toast.LENGTH_LONG).show();
            mCompletionListener.complete(false);
            return;
        }

        for (String number :
                mPhoneNumbers) {
            try {
                sendSMS(number, mMessage.body);
            }
            catch(Exception e){
                Toast.makeText(getActivity(), "Could not send text to " + number + ".", Toast.LENGTH_LONG).show();
                mCompletionListener.complete(false);
            }
        }
        Toast.makeText(getActivity(), "Finished sending texts.", Toast.LENGTH_LONG).show();
        mCompletionListener.complete(true);
    }

    private void sendSMS(String phoneNumber, String message) throws Exception{
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendBulkSMS();
                } else {
                    Toast.makeText(getActivity(),
                            "Permission for sending SMSs denied.", Toast.LENGTH_LONG).show();
                    mCompletionListener.complete(false);
                    return;
                }
            }
        }
    }
}
