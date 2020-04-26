package com.example.hacknow2.phase1.contactsview;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.hacknow2.R;
import com.example.hacknow2.phase2.SelectionActivity;

import java.util.ArrayList;
import java.util.Hashtable;


/**
* The following helps with obtaining contacts from phone:
 * https://developer.android.com/training/contacts-provider/retrieve-names
 *
 * The following is a SimpleCursorAdapter but modified so that it works with a Recyclerview
 * instead of a Listview (the class is at the bottom of this file)
 * https://gist.github.com/skyfishjy/443b7448f59be978bc59
 */
public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACTS_RETRIEVAL = 1000;
    private RecyclerView mRecyclerView;
    private CursorRecyclerViewAdapter<RecyclerViewHolder> mCursorAdapter;
    private Hashtable<String, String> mSelectedItems; //Maps phone number to display name

    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        return new ContactsFragment();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        final String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Phone._ID,
                Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY :
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED
        };


        return new CursorLoader(
                getActivity(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //Creates an adapter that binds data from the cursor to the recyclerview item
        setupAdapter(data);
        //Reset Recyclerview adapter with newly loaded cursor
        mRecyclerView.setAdapter(mCursorAdapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
        mSelectedItems.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Creates a thread that gets contact info from phone
        LoaderManager.getInstance(this).initLoader(CONTACTS_RETRIEVAL, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        mSelectedItems = new Hashtable<>();
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Gets the ListView from the View list of the parent activity
        mRecyclerView =
                (RecyclerView) getActivity().findViewById(android.R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //cursorAdapter has no data until onLoadFinished is called
        mRecyclerView.setAdapter(mCursorAdapter);
    }

    private void setupAdapter(Cursor data){
        mCursorAdapter = new CursorRecyclerViewAdapter<RecyclerViewHolder>(getActivity(), data) {

            @Override
            public void onBindViewHolder(RecyclerViewHolder viewHolder, Cursor cursor) {
                String contactId = cursor.getString(0);
                String contactName = cursor.getString(1);
                String phoneNumber = cursor.getString(2);
                String lastContacted = cursor.getString(3);

                viewHolder.setContactID(contactId);
                viewHolder.setName(contactName);
                viewHolder.setPhone(phoneNumber);
                //TODO: Not able to find "last contacted" info easily from content provider...need to code another way.
                //Apparently phone manufacturers dont always implement the feature.

                //viewHolder.setLastContacted(lastContacted);

                //Need to add user to selected contacts list
                viewHolder.setOnCheckedChangedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(mSelectedItems.contains(phoneNumber)){
                            mSelectedItems.remove(phoneNumber);
                        }
                        else{
                            mSelectedItems.put(phoneNumber, contactName);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_contacts_listitem, parent, false);
                return new RecyclerViewHolder(itemView);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }
        };
    }

    //Add menu to actionbar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_contacts_toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.proceed:

            Intent i = new Intent(getActivity(), SelectionActivity.class);
            Bundle data = new Bundle();
            data.putSerializable("DATA", mSelectedItems);
            i.putExtras(data);
            startActivity(i);

                return true;
            case R.id.sort:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
