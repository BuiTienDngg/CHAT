package com.example.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewSender;
        private TextView textViewContent;
        private TextView textViewTimestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewSender = itemView.findViewById(R.id.text_view_sender);
            textViewContent = itemView.findViewById(R.id.text_view_content);
            textViewTimestamp = itemView.findViewById(R.id.text_view_timestamp);
        }

        public void bind(Message message) {
            textViewSender.setText(message.getSender());
            textViewContent.setText(message.getContent());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date(message.getTimestamp()));
            textViewTimestamp.setText(formattedDate);
        }
    }
}

