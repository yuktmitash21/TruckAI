package com.example.yuktmitash.testingaws;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private Context mContext;
    private List<ChatObj> chats;
    private HashMap<String, Bitmap> bitmap;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private FirebaseUser user;

    public MessageAdapter(Context mContext, List<ChatObj> chats, HashMap<String, Bitmap> bitmap) {
        this.mContext = mContext;
        this.chats = chats;
        this.bitmap = bitmap;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ChatObj chat = chats.get(i);

        viewHolder.textView.setText(chat.getMessage());

        Bitmap myBitmap = bitmap.get(chats.get(i).getSender().getFireid());
        if (myBitmap == null) {
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            viewHolder.imageView.setImageBitmap(myBitmap);

        }

    }


    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chats.get(position).getSender().getFireid().equals(user.getUid())) {
            return MSG_TYPE_LEFT;
        } else {
            return MSG_TYPE_RIGHT;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.show_message);
            imageView = itemView.findViewById(R.id.profile_image);
        }

    }
}
