package com.beyondthehorizon.associates.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.chats.ChatActivity;
import com.beyondthehorizon.associates.database.GroupDetailsModel;
import com.beyondthehorizon.associates.database.GroupDetailsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class GroupInfoAdapter extends RecyclerView.Adapter<GroupInfoAdapter.MyViewHolder>
        implements Filterable {

    private LayoutInflater inflater;
    private ArrayList<GroupDetailsModel> providerModelArrayList;
    private Context ctx;
    private ArrayList<GroupDetailsModel> providersListFiltered;
    private static final String TAG = "PAPA";

    public GroupInfoAdapter(Context ctx, ArrayList<GroupDetailsModel> providerModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.providerModelArrayList = providerModelArrayList;
        this.providersListFiltered = providerModelArrayList;
        this.ctx = ctx;
    }

    @Override
    public GroupInfoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.group_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(GroupInfoAdapter.MyViewHolder holder, int position) {
        final GroupDetailsModel provider = providerModelArrayList.get(position);
//        holder.providePhoto.setImageResource(provider.getImage_drawable());
        holder.the_name.setText(provider.getUserName());
        holder.the_tagLine.setText(provider.getUserTagline());
        if (provider.getUserRole().contains("Admin")) {
            holder.the_role.setVisibility(View.VISIBLE);
        }
//        holder.userMessageTime.setText(provider.getTime());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + providerModelArrayList.size());
        return providerModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView the_name, the_tagLine, the_role;

        public MyViewHolder(View itemView) {
            super(itemView);
            the_name = (TextView) itemView.findViewById(R.id.the_name);
            the_tagLine = itemView.findViewById(R.id.the_tagLine);
            the_role = (TextView) itemView.findViewById(R.id.the_role);
        }

    }

    @Override
    public Filter getFilter() {
        return filteredProvidersList;
    }

    private Filter filteredProvidersList = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<GroupDetailsModel> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList = providersListFiltered;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (GroupDetailsModel item : providersListFiltered) {
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
            providerModelArrayList = (ArrayList<GroupDetailsModel>) filterResults.values;
            notifyDataSetChanged();
        }
    };
}