package com.beyondthehorizon.associates.contacts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.chats.ChatActivity;
import com.beyondthehorizon.associates.database.UserProfile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.MyViewHolder>
        implements Filterable {

    private LayoutInflater inflater;
    private ArrayList<UserProfile> providerModelArrayList;
    private Context ctx;
    private ArrayList<UserProfile> providersListFiltered;
    private static final String TAG = "PAPA";

    public FindFriendAdapter(Context ctx, ArrayList<UserProfile> providerModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.providerModelArrayList = providerModelArrayList;
        this.providersListFiltered = providerModelArrayList;
        this.ctx = ctx;
    }

    @Override
    public FindFriendAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.user_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(FindFriendAdapter.MyViewHolder holder, int position) {
        final UserProfile provider = providerModelArrayList.get(position);
//        holder.providePhoto.setImageResource(provider.getImage_drawable());
        holder.providerName.setText(provider.getUserName());
        holder.userMessage.setText(provider.getPhoneNumber());
        Picasso.get().load(provider.getImageUrl()).fit().placeholder(R.drawable.account)
                .into(holder.imgProfile);
        holder.userMessageTime.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent providerDetails = new Intent(ctx, ChatActivity.class);
                providerDetails.putExtra("myFriendName", provider.getUserName());
                providerDetails.putExtra("friendUID", provider.getUserUid());
                providerDetails.putExtra("chatTypeFromChatsFragment", "Single");
                providerDetails.putExtra("imageUrlFromChatsFragment", provider.getImageUrl());
//                providerDetails.putExtra("Name", provider.getName());
                ctx.startActivity(providerDetails);
                ((Activity) ctx).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return providerModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView providerName;
        TextView userMessage, userMessageTime;
        CircleImageView imgProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            providerName = (TextView) itemView.findViewById(R.id.userNAme);
            userMessage = (TextView) itemView.findViewById(R.id.userMessage);
            userMessageTime = (TextView) itemView.findViewById(R.id.userMessageTime);
            imgProfile = (CircleImageView) itemView.findViewById(R.id.imgProfile);
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
                    if (item.getUserName().toLowerCase().contains(filterPattern) ||
                            item.getPhoneNumber().toLowerCase().contains(filterPattern)

                    ) {
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