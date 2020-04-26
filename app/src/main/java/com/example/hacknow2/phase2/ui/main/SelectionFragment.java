package com.example.hacknow2.phase2.ui.main;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hacknow2.R;
import com.example.hacknow2.phase2.Message;
import com.example.hacknow2.phase3.SendDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A placeholder fragment containing a simple view.
 */
public class SelectionFragment extends Fragment {

    private RecyclerView mRV;
    private RecyclerMessageAdapter mAdapter;
    private static final String TAG = "SelectionFragment";
    private DatabaseReference mDatabase;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String CONTACTS = "DATA";
    private int mIndex = -1;
    private ArrayList<Message> data;
    private HashMap<String, String> mContacts;

    public static SelectionFragment newInstance(int index, HashMap<String, String> contacts) {
        SelectionFragment fragment = new SelectionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CONTACTS, contacts);
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIndex = getArguments().getInt(ARG_SECTION_NUMBER);
            try {
                mContacts = (HashMap<String, String>) getArguments().getSerializable(CONTACTS);
            }
            catch(Exception e){
                mContacts = null;
            }
        }
    }
    private void getPublicPosts(){
        mDatabase.child("kits").child("tests").child("public").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message m = snapshot.getValue(Message.class);
                    data.add(m);
                }

                mAdapter.updateData(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });
    }

    private void getPrivatePosts(){
        String userId = FirebaseAuth.getInstance().getUid();
        mDatabase.child("kits").child("tests").child("private").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message m = snapshot.getValue(Message.class);
                    data.add(m);
                }

                mAdapter.updateData(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_selection, container, false);
        data = new ArrayList<>();
        mAdapter = new RecyclerMessageAdapter(data);
        mRV = root.findViewById(R.id.rv);
        mRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRV.setAdapter(mAdapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(mIndex==1)
            getPublicPosts();
        else if(mIndex==2)
            getPrivatePosts();

        return root;
    }

    class TextHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mMainText;
        private TextView mShares;
        private TextView mMedia;
        public TextHolder(@NonNull View itemView) {
            super(itemView);
            mMainText = itemView.findViewById(R.id.textview);
            mShares = itemView.findViewById(R.id.shares);
            mMedia = itemView.findViewById(R.id.media);
            itemView.setOnClickListener(this);
        }

        void setMainText(String str){
            mMainText.setText(str);
        }

        void setShares(String str){
            mShares.setText(str);
        }

        void setMediaType(String str){
            mMedia.setText(str);
        }

        @Override
        public void onClick(View view) {

            if(data!=null && data.size()>0) {

                if(mContacts == null || mContacts.size() <= 0){
                    Snackbar.make(getView(), R.string.NoContactsSelected, Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                Message m = data.get(getAdapterPosition());

                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ArrayList<String> numbers = new ArrayList<>(mContacts.keySet());
                SendDialog sd = new SendDialog(numbers, m, new SendDialog.OnFinished() {
                    @Override
                    public void complete(boolean success) {
                        view.setBackgroundColor(getResources().getColor(R.color.tw__composer_white));
                    }
                });
                sd.show(fm, getString(R.string.SENDTEXT));
            }
        }
    }

    class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private String mImgSrc;
        private ImageView mIV;
        private TextView mShares;
        private TextView mMedia;
        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            mShares = itemView.findViewById(R.id.shares);
            mMedia = itemView.findViewById(R.id.media);
            mIV = itemView.findViewById(R.id.imageview);
            itemView.setOnClickListener(this);
        }

        ImageView getImageView(){
            return mIV;
        }

        void setShares(String str){
            mShares.setText(str);
        }

        void setMediaType(String str){
            mMedia.setText(str);
        }

        @Override
        public void onClick(View view) {

            if(data!=null && data.size()>0) {
                Message m = data.get(getAdapterPosition());

                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ArrayList<String> numbers = mContacts != null && mContacts.size() > 0 ? new ArrayList<>(mContacts.keySet()) : null;
                SendDialog sd = new SendDialog(numbers, m, new SendDialog.OnFinished() {
                    @Override
                    public void complete(boolean success) {
                        view.setBackgroundColor(getResources().getColor(R.color.tw__composer_white));
                    }
                });
                sd.show(fm, getString(R.string.SENDTEXT));
            }
        }
    }

    class RecyclerMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int TEXT_VIEWTYPE = 0;
        private static final int IMAGE_VIEWTYPE = 1;
        private ArrayList<Message> data;

        void updateData(ArrayList<Message> data){
            this.data = data;
            notifyDataSetChanged();
        }

        RecyclerMessageAdapter(ArrayList<Message> data){
            this.data = data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if(viewType==TEXT_VIEWTYPE) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_selection_text_item, parent, false);
                return new TextHolder(v);
            }
            else if(viewType==IMAGE_VIEWTYPE){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_selection_image_item, parent, false);
                return new ImageHolder(v);
            }

            return null;
        }

        @Override
        public int getItemViewType(int position) {
            if(data.size()>0){
                return data.get(position).isTextOnly ? TEXT_VIEWTYPE : IMAGE_VIEWTYPE;
            }
            return super.getItemViewType(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            Message m = data.get(position);

            switch (holder.getItemViewType()){
                case TEXT_VIEWTYPE:
                    TextHolder th = (TextHolder)holder;
                    th.setMainText(m.body);
                    th.setMediaType(m.isTextOnly ? "Text" : "Image");
                    th.setShares(String.valueOf(m.shares));
                    break;
                case IMAGE_VIEWTYPE:
                    ImageHolder ih = (ImageHolder)holder;
                    if(URLUtil.isValidUrl(m.body)) {
                        ImageView target = ih.getImageView();
                        Picasso.get().load(m.body).into(target);
                    }
                    ih.setMediaType(m.isTextOnly ? "Text" : "Image");
                    ih.setShares(String.valueOf(m.shares));
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}