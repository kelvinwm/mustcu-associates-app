package com.beyondthehorizon.associates.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.groupchat.CommentsChatActivity;
import com.beyondthehorizon.associates.database.ChatModel;
import com.beyondthehorizon.associates.viewdetails.ViewImageActivity;
import com.beyondthehorizon.associates.viewmodels.ChatsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ProMemberViewHolder>
        implements Filterable {
    private ArrayList<ChatModel> proMemberArrayList;
    private ArrayList<ChatModel> filteredProMemberArrayList = new ArrayList<>();
    private static final String TAG = "PROMEMBER";
    private Context context;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseUser fuser;
    private ChatsViewModel chatsViewModel;


    public ChatsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ProMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == 1) {
            View view = inflater.inflate(R.layout.their_message, parent, false);
            return new ChatsAdapter.ProMemberViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.my_message, parent, false);
            return new ChatsAdapter.ProMemberViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ProMemberViewHolder holder, int position) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        chatsViewModel = ViewModelProviders.of((FragmentActivity) context).get(ChatsViewModel.class);
        final ChatModel proMember = proMemberArrayList.get(position);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        final String details;
        final int numberOfComments;

        if (proMember.getType().contains("Single")) {
            holder.commentLayout.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        }
        holder.message_time.setText(proMember.getTimestamp());
        holder.providerMessage.setText(proMember.getMessage());
        holder.numberOfComments.setText(proMember.getComments());
        holder.del_status.setTextColor(Color.parseColor("#000000"));
        if (proMember.getImageUrl().contains("*hak*none0#")) {
            holder.sendImage.setVisibility(View.GONE);
        } else {
            holder.sendImage.setVisibility(View.VISIBLE);
            holder.sendImage.setImageURI(Uri.fromFile(new File(proMember.getImageUrl())));
            holder.sendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewImageActivity.class);
                    intent.putExtra("imageUri", proMember.getImageUrl());
                    context.startActivity(intent);
                }
            });
        }
        if (proMember.delivery_status.contains("sent")) {
            myRef.child("Users").child("UserChats").child(proMember.getMessage_key()).child("delivery_status").setValue("Seen");
            chatsViewModel.updateDeliveryStatus(proMember.message_key, "Delivered");
        }
        if (proMember.delivery_status.contains("Seen")) {
            holder.del_status.setTextColor(Color.parseColor("#0398fc"));
            holder.del_status.setText(proMember.getDelivery_status());
        } else {
            holder.del_status.setText(proMember.getDelivery_status());
        }

        if (proMemberArrayList.get(position).getPhoneNumber().equals(fuser.getPhoneNumber())) {
            holder.providerName.setText("");
        } else {
            details = proMember.getSenderName() + " ~" + proMember.getPhoneNumber();
            holder.providerName.setText(details);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                if (proMember.getType().contains("Single") || proMember.getSenderName().isEmpty()) {
                    return;
                }
                holder.mView.setEnabled(false);
                Intent providerDetails = new Intent(context, CommentsChatActivity.class);
                providerDetails.putExtra("MainQuestionKey", proMember.getMessage_key());
                providerDetails.putExtra("RoomId", proMember.getSenderUID());
                context.startActivity(providerDetails);
            }
        });
    }


    @Override
    public int getItemCount() {
        if (proMemberArrayList != null) {
            return proMemberArrayList.size();
        } else {
            return 0;
        }
    }

    public void setProMemberArrayList(ArrayList<ChatModel> proMembers) {
        this.proMemberArrayList = proMembers;
        notifyDataSetChanged();
        this.filteredProMemberArrayList = proMemberArrayList;
    }

    @Override
    public Filter getFilter() {
        return filteredProvidersList;
    }

    public static class ProMemberViewHolder extends RecyclerView.ViewHolder {
        TextView providerName, numberOfComments, del_status, message_time;
        ImageView sendImage;
        EmojiconTextView providerMessage;
        View mView, divider;
        LinearLayout commentLayout, r2;

        public ProMemberViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            providerName = (TextView) itemView.findViewById(R.id.name);
            providerMessage = (EmojiconTextView) itemView.findViewById(R.id.message_body);
            message_time = (TextView) itemView.findViewById(R.id.message_time);
            numberOfComments = (TextView) itemView.findViewById(R.id.numberOfComments);
            commentLayout = itemView.findViewById(R.id.commentLayout);
            divider = itemView.findViewById(R.id.divider);
            del_status = itemView.findViewById(R.id.del_status);
            sendImage = itemView.findViewById(R.id.sendImage);
            r2 = itemView.findViewById(R.id.r2);
        }
    }

    private Filter filteredProvidersList = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ChatModel> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList = filteredProMemberArrayList;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (ChatModel item : filteredProMemberArrayList) {
                    if (item.getMessage().toLowerCase().contains(filterPattern) ||
                            item.getSenderName().toLowerCase().contains(filterPattern) ||
                            item.getPhoneNumber().toLowerCase().contains(filterPattern)) {
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
            proMemberArrayList = (ArrayList<ChatModel>) filterResults.values;
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (proMemberArrayList.get(position).getPhoneNumber().equals(fuser.getPhoneNumber())) {
            return 0;
        } else {
            return 1;
        }
    }
}