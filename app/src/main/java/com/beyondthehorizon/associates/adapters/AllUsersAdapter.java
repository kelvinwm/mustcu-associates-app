package com.beyondthehorizon.associates.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.chats.ChatActivity;
import com.beyondthehorizon.associates.database.RecentChatModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static com.beyondthehorizon.associates.MainActivity.CHAT_PREFS;
import static com.beyondthehorizon.associates.MainActivity.ChatTypeFromChatsFragment;
import static com.beyondthehorizon.associates.MainActivity.FriendUID;
import static com.beyondthehorizon.associates.MainActivity.MyFriendName;
import static com.beyondthehorizon.associates.MainActivity.ProfileUrlFromChatsFragment;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.MyViewHolder>
        implements Filterable {

    private LayoutInflater inflater;
    private ArrayList<RecentChatModel> providerModelArrayList;
    private Context ctx;
    private ArrayList<RecentChatModel> providersListFiltered;
    private static final String TAG = "PAPA";

    public AllUsersAdapter(Context ctx, ArrayList<RecentChatModel> providerModelArrayList) {
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
        final RecentChatModel provider = providerModelArrayList.get(position);
        SharedPreferences pref = ctx.getSharedPreferences(CHAT_PREFS, 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();
//        holder.providePhoto.setImageResource(provider.getImage_drawable());
        holder.providerName.setText(provider.getUsername());
        holder.userMessage.setText(provider.getMessage());
        holder.userMessageTime.setText(provider.getTime());
        if (provider.getType().contains("Room")) {
            if (provider.getImageUrl().isEmpty()) {
                Picasso.get().load("none").fit().placeholder(R.drawable.giconn).into(holder.imgProfile);
            } else {
                Picasso.get().load(provider.getImageUrl()).fit().placeholder(R.drawable.giconn).into(holder.imgProfile);
            }
        } else {
            Picasso.get().load(provider.getImageUrl()).fit().placeholder(R.drawable.account)
                    .into(holder.imgProfile);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String profile;
                if (provider.getImageUrl().isEmpty()) {
                    profile = "none";
                } else {
                    profile = provider.getImageUrl();
                }
                editor.putString(MyFriendName, provider.getUsername());
                editor.putString(FriendUID, provider.getSenderUID());
                editor.putString(ChatTypeFromChatsFragment, provider.getType());
                editor.putString(ProfileUrlFromChatsFragment, profile);
                editor.apply();
                Intent providerDetails = new Intent(ctx, ChatActivity.class);
//                providerDetails.putExtra("myFriendName", provider.getUsername());
//                providerDetails.putExtra("friendUID", provider.getSenderUID());
//                providerDetails.putExtra("chatTypeFromChatsFragment", provider.getType());
//                providerDetails.putExtra("imageUrlFromChatsFragment", provider.getImageUrl());
////                providerDetails.putExtra("Name", provider.getName());
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
        EmojiconTextView userMessage;
        TextView userMessageTime;
        CircleImageView imgProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            providerName = (TextView) itemView.findViewById(R.id.userNAme);
            userMessage = (EmojiconTextView) itemView.findViewById(R.id.userMessage);
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
            ArrayList<RecentChatModel> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList = providersListFiltered;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (RecentChatModel item : providersListFiltered) {
                    if (item.getUsername().toLowerCase().contains(filterPattern)) {
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
            providerModelArrayList = (ArrayList<RecentChatModel>) filterResults.values;
            notifyDataSetChanged();
        }
    };
}