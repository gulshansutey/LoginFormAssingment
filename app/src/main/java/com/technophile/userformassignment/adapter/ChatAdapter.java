package com.technophile.userformassignment.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.technophile.userformassignment.R;
import com.technophile.userformassignment.databinding.AdapterChatBinding;
import com.technophile.userformassignment.model.Question;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {

    private List<Question>questions=new ArrayList<>();

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
       AdapterChatBinding adapterChatBinding= DataBindingUtil.inflate(layoutInflater, R.layout.adapter_chat,parent,false);
        return new VH(adapterChatBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

            Question question=questions.get(position);
        // If the message is a received message.

        if(!question.isAnswered()){
            holder.binding.chatLeftMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.binding.chatLeftMsgTextView.setText(question.getQuestion());
            holder.binding.chatRightMsgLayout.setVisibility(LinearLayout.GONE);
        }else {
            holder.binding.chatLeftMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.binding.chatLeftMsgTextView.setText(question.getQuestion());
            holder.binding.chatRightMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.binding.chatRightMsgTextView.setText(question.getAnswer());
        }

    }




    public void setQuestions(List<Question>questi){
        this.questions=questi;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class VH extends RecyclerView.ViewHolder {


        AdapterChatBinding binding;

        public VH(AdapterChatBinding itemView) {
            super(itemView.getRoot());
               this.binding=itemView;

        }


    }
}
