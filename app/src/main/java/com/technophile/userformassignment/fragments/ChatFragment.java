package com.technophile.userformassignment.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.technophile.userformassignment.adapter.ChatAdapter;
import com.technophile.userformassignment.activity.LoginActivity;
import com.technophile.userformassignment.model.Question;
import com.technophile.userformassignment.model.Questions;
import com.technophile.userformassignment.R;
import com.technophile.userformassignment.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChatFragment extends Fragment implements View.OnClickListener {

    public ChatFragment() {
        // Required empty public constructor
    }



 public static ChatFragment newInstance() {

        return new ChatFragment();
    }

    private FragmentChatBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_chat,container,false);

        return binding.getRoot();
    }

    private Context context;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }
    private SharedPreferences sharedPreferences;
    private ChatAdapter chatAdapter;
    private Questions questions;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((LoginActivity)context).setToolbarTitle("Questions");
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        linearLayoutManager.setReverseLayout(true);
        chatAdapter=new ChatAdapter();

        binding.floatingActionButton.setOnClickListener(this);
        binding.rvChatList.setLayoutManager(linearLayoutManager);
        binding.rvChatList.setAdapter(chatAdapter);
        sharedPreferences=context.getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
        questions=Questions.create(sharedPreferences.getString("list",null));
        populateList();
    }

    private void populateList(){
        List<Question> qList=new ArrayList<>();
        for (int i=0;i<sharedPreferences.getInt("last_que",1);i++){
            qList.add(questions.getData().get(i));
            if (questions.getData().get(i).isAnswered()){
                binding.rlChatBox.setVisibility(View.GONE);
                if (getFragmentManager() != null) {
                    getFragmentManager().beginTransaction()
                            .addToBackStack("submit").hide(this)
                            .add(android.R.id.content, SubmitFragment.newInstance()).commit();
                    break;
                }
            }
        }
        Collections.reverse(qList);
        chatAdapter.setQuestions(qList);
    }

    private void saveData(int saveIndex,int lastQ,String text){
        questions.getData().get(saveIndex).setAnswered(true);
        questions.getData().get(saveIndex).setAnswer(text);
        sharedPreferences.edit().putInt("last_que",lastQ).apply();
        sharedPreferences.edit().putString("list",questions.serialize()).apply();
        populateList();
    }


    @Override
    public void onClick(View v) {

        String text=binding.etChat.getText().toString();
        if (!TextUtils.isEmpty(text)&&chatAdapter.getItemCount()<=questions.getData().size()){
            binding.etChat.setText("");
           switch (chatAdapter.getItemCount()){
               case 1:if (text.matches("[0-9]+")){
                   saveData(  0,  2,  text);
               }else {
                   Toast.makeText(context, "Use numbers only", Toast.LENGTH_SHORT).show();
               }
                   break;
               case 2:if (text.matches("[a-zA-Z]+")){
                   saveData(  1,  3,  text);
               }else {
                   Toast.makeText(context, "Use letters only", Toast.LENGTH_SHORT).show();
               }
                   break;
               case 3:if (!text.matches("[0-9]+")||text.length()>10) {
                     Toast.makeText(context, "Not a valid mobile number", Toast.LENGTH_SHORT).show();
               }else {
                   saveData(  2,  4,  text);
               }
                   break;
               case 4:if (text.equals("true")||text.equals("false")){
                   saveData(  3,  5,  text);
               }else {
                   Toast.makeText(context, "Use true or false only", Toast.LENGTH_SHORT).show();
               }
                   break;
               case 5:if (text.matches("\\d*\\.?\\d+")){
                   saveData(  4,  5,  text);
               }else {
                   Toast.makeText(context, "Use float only", Toast.LENGTH_SHORT).show();
               }
                   break;
           }
        }
    }
}
