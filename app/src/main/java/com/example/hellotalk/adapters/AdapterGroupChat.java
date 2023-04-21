package com.example.hellotalk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellotalk.R;
import com.example.hellotalk.models.ModelGroupChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.HolderGroupChat>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private ArrayList<ModelGroupChat> modelGroupChatList;

    private FirebaseAuth firebaseAuth;

    public AdapterGroupChat(Context context, ArrayList<ModelGroupChat> modelGroupChatList) {
        this.context = context;
        this.modelGroupChatList = modelGroupChatList;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layouts
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_right, parent, false);
            return new HolderGroupChat(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_left, parent, false);
            return new HolderGroupChat(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {

        //get data
        ModelGroupChat model = modelGroupChatList.get(position);
        String timestamp = model.getTimestamp();
        String message = model.getMessage();
        String senderUid = model.getSender();
        String messageType = model.getType();

        //convert time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        /*cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
        String dateTime = "00";*/
        //set data
        try {
            cal.setTimeInMillis(Long.parseLong(timestamp));
        }catch (Exception e){
            e.printStackTrace();
        }
        String dateTime = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        //set data
        if(messageType.equals("text")){
            holder.messageIv.setVisibility(View.GONE);
            holder.messageTv.setVisibility(View.VISIBLE);
            holder.messageTv.setText(message);

        }else {
            holder.messageIv.setVisibility(View.VISIBLE);
            holder.messageTv.setVisibility(View.GONE);
            try {
                Picasso.get().load(message).placeholder(R.drawable.ic_image_black).into(holder.messageIv);
            }
            catch (Exception e){
                holder.messageIv.setImageResource(R.drawable.ic_image_black);
            }
        }

        try {
            holder.timeTv.setText(dateTime);
        }
        catch (Exception e){
            e.printStackTrace();
        }


        setUserName(model, holder);
    }

    private void setUserName(ModelGroupChat model, HolderGroupChat holder) {
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(model.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        for (DataSnapshot ds: datasnapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();

                            holder.nameTv.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelGroupChatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (modelGroupChatList.get(position).getSender().equals(firebaseAuth.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    class HolderGroupChat extends RecyclerView.ViewHolder {

        private TextView nameTv, messageTv, timeTv;
        private ImageView messageIv;

        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            messageIv = itemView.findViewById(R.id.messageIv);

        }
    }
}
