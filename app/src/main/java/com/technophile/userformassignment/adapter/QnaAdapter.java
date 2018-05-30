package com.technophile.userformassignment.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.technophile.userformassignment.R;
import com.technophile.userformassignment.databinding.AdapterQnaBinding;
import com.technophile.userformassignment.model.Question;

import java.util.List;

public class QnaAdapter extends RecyclerView.Adapter<QnaAdapter.VH> {

    private List<Question> questions;

    public QnaAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        AdapterQnaBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.adapter_qna, parent, false);
        return new QnaAdapter.VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Question question = questions.get(position);
        String que = "Que. " + position + 1 + ": " + question.getQuestion();
        String ans = "Ans. " + position + ": " + question.getAnswer();
        holder.binding.tvQuestion.setText(que);
        holder.binding.tvAnswer.setText(ans);
    }


    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class VH extends RecyclerView.ViewHolder {


        AdapterQnaBinding binding;

        public VH(AdapterQnaBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

        }


    }
}
