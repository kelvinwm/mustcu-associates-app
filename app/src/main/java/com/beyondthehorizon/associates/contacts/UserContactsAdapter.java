package com.beyondthehorizon.associates.contacts;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.database.UserProfile;

import java.util.ArrayList;

public class UserContactsAdapter extends RecyclerView.Adapter<UserContactsAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<UserProfile> providerModelArrayList;
    private Context ctx;
    private ArrayList<UserProfile> providersListFiltered;
    private static final String TAG = "PAPA";
    public ArrayList<UserProfile> allTokens = new ArrayList<>();
    SparseBooleanArray itemStateArray = new SparseBooleanArray();

    public UserContactsAdapter(Context ctx, ArrayList<UserProfile> providerModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.providerModelArrayList = providerModelArrayList;
        this.providersListFiltered = providerModelArrayList;
        this.ctx = ctx;
    }

    @Override
    public UserContactsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.contacts_select_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final UserContactsAdapter.MyViewHolder holder, int position) {
        final UserProfile provider = providerModelArrayList.get(position);
//        holder.providePhoto.setImageResource(provider.getImage_drawable());
        holder.providerName.setText(provider.getUserName());
        holder.number.setText(provider.getPhoneNumber());
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return providerModelArrayList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView providerName;
        TextView number;
        CheckBox checkedContact;

        public MyViewHolder(View itemView) {
            super(itemView);
            providerName = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
            checkedContact = itemView.findViewById(R.id.checkedContact);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            // use the sparse boolean array to check
//            final UserProfile provider = providerModelArrayList.get(position);
            if (!itemStateArray.get(position, false)) {
                checkedContact.setChecked(false);
//                allTokens.remove(provider.getUserToken());
            } else {
                checkedContact.setChecked(true);
//                allTokens.add(provider.getUserToken());
            }
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            final UserProfile provider = providerModelArrayList.get(adapterPosition);
            if (!itemStateArray.get(adapterPosition, false)) {
                checkedContact.setChecked(true);
                itemStateArray.put(adapterPosition, true);
                allTokens.add(provider);
            } else {
                checkedContact.setChecked(false);
                itemStateArray.put(adapterPosition, false);
                allTokens.remove(provider);
            }
        }
    }
}