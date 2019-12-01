package com.beyondthehorizon.testfirebasefunctionstest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.MyViewHolder>
        implements Filterable {

    private LayoutInflater inflater;
    private ArrayList<UserProfile> providerModelArrayList;
    private Context ctx;
    private ArrayList<UserProfile> providersListFiltered;
    private static final String TAG = "PAPA";

    public AllUsersAdapter(Context ctx, ArrayList<UserProfile> providerModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.providerModelArrayList = providerModelArrayList;
        this.providersListFiltered = providerModelArrayList;
        this.ctx = ctx;
    }

    @Override
    public AllUsersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.user_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(AllUsersAdapter.MyViewHolder holder, int position) {
        final UserProfile provider = providerModelArrayList.get(position);
//        holder.providePhoto.setImageResource(provider.getImage_drawable());
        holder.providerName.setText(provider.getUserName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent providerDetails = new Intent(ctx, ChatActivity.class);
                providerDetails.putExtra("myFriend", provider.getUserName());
                providerDetails.putExtra("friendUID", provider.getUserUid());
//                providerDetails.putExtra("Name", provider.getName());
                ctx.startActivity(providerDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return providerModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView providerName;
        TextView phone_number;
        TextView numberOfComments;

        public MyViewHolder(View itemView) {
            super(itemView);
            providerName = (TextView) itemView.findViewById(R.id.userNAme);
        }

    }

    @Override
    public Filter getFilter() {
        return filteredProvidersList;
    }

    private Filter filteredProvidersList = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<UserProfile> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList = providersListFiltered;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (UserProfile item : providersListFiltered) {
                    if (item.getUserName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            providerModelArrayList = (ArrayList<UserProfile>) filterResults.values;
            notifyDataSetChanged();
        }
    };
}