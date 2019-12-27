package com.beyondthehorizon.associates.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.database.CommentsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ProMemberViewHolder>
        implements Filterable {
    private ArrayList<CommentsModel> proMemberArrayList;
    private ArrayList<CommentsModel> filteredProMemberArrayList = new ArrayList<>();
    private static final String TAG = "PROMEMBER";
    private Context context;
    private FirebaseUser fuser;


    public CommentsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ProMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == 1) {
            View view = inflater.inflate(R.layout.their_message, parent, false);
            return new CommentsAdapter.ProMemberViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.my_message, parent, false);
            return new CommentsAdapter.ProMemberViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ProMemberViewHolder holder, int position) {
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
        final CommentsModel proMember = proMemberArrayList.get(position);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        final String details;

        holder.commentLayout.setVisibility(View.GONE);
        holder.divider.setVisibility(View.GONE);
        holder.message_time.setText(proMember.getTimestamp());
        holder.providerMessage.setText(proMember.getMessage());
//        }
        if (proMemberArrayList.get(position).getPhoneNumber().equals(fuser.getPhoneNumber())) {
            holder.providerName.setText("");
        } else {
            details = proMember.getSenderName() + " ~" + proMember.getPhoneNumber();
            holder.providerName.setText(details);
        }

    }


    @Override
    public int getItemCount() {
        if (proMemberArrayList != null) {
            return proMemberArrayList.size();
        } else {
            return 0;
        }
    }

    public void setProMemberArrayList(ArrayList<CommentsModel> proMembers) {
        this.proMemberArrayList = proMembers;
        notifyDataSetChanged();
        this.filteredProMemberArrayList = proMemberArrayList;
    }

    @Override
    public Filter getFilter() {
        return filteredProvidersList;
    }

    public static class ProMemberViewHolder extends RecyclerView.ViewHolder {
        TextView providerName, numberOfComments;
        TextView providerMessage, message_time;
        View mView, divider;
        LinearLayout commentLayout, r2;

        public ProMemberViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            providerName = (TextView) itemView.findViewById(R.id.name);
            providerMessage = (TextView) itemView.findViewById(R.id.message_body);
            message_time = (TextView) itemView.findViewById(R.id.message_time);
            numberOfComments = (TextView) itemView.findViewById(R.id.numberOfComments);
            commentLayout = itemView.findViewById(R.id.commentLayout);
            divider = itemView.findViewById(R.id.divider);
            r2 = itemView.findViewById(R.id.r2);
        }
    }

    private Filter filteredProvidersList = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<CommentsModel> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList = filteredProMemberArrayList;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (CommentsModel item : filteredProMemberArrayList) {
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
            proMemberArrayList = (ArrayList<CommentsModel>) filterResults.values;
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