package com.technophile.userformassignment.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.technophile.userformassignment.R;
import com.technophile.userformassignment.activity.LoginActivity;
import com.technophile.userformassignment.adapter.QnaAdapter;
import com.technophile.userformassignment.database.MyDataBase;
import com.technophile.userformassignment.databinding.FragmentSubmitBinding;
import com.technophile.userformassignment.model.Answer;
import com.technophile.userformassignment.model.Question;
import com.technophile.userformassignment.model.Questions;
import com.technophile.userformassignment.model.ResponseModel;
import com.technophile.userformassignment.network.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SubmitFragment extends Fragment implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    MyDataBase myDataBase;
    public SubmitFragment() {
    }

     public static SubmitFragment newInstance() {

        return new SubmitFragment();
    }


    private FragmentSubmitBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         binding = DataBindingUtil.inflate(inflater, R.layout.fragment_submit, container, false);
        return binding.getRoot();
    }

    private Context context;
    private Questions questions;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    private HashMap<String,String>db_data;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((LoginActivity)context).setToolbarTitle("Submit Info");
        sharedPreferences=context.getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
        myDataBase=new MyDataBase(context);
        db_data=myDataBase.getUserInfo();
        questions= Questions.create(sharedPreferences.getString("list",null));
        binding.btnSubmit.setOnClickListener(this);
        binding.rvQnaList.setLayoutManager(new LinearLayoutManager(context));
        binding.rvQnaList.setAdapter(new QnaAdapter(questions.getData()));
    }

    @Override
    public void onClick(View v) {

        ResponseModel responseModel=new ResponseModel();
        responseModel.setAge(db_data.get(MyDataBase.KEY_AGE));
        responseModel.setEmail(db_data.get(MyDataBase.KEY_EMAIL));
        responseModel.setFbUserName(db_data.get(MyDataBase.KEY_NAME));
        responseModel.setGender(db_data.get(MyDataBase.KEY_GENDER));
        responseModel.setName(db_data.get(MyDataBase.KEY_NAME));
        responseModel.setId(db_data.get(MyDataBase.KEY_ID));
        responseModel.setMobile(db_data.get(MyDataBase.KEY_MOBILE));

        List<Answer>answers=new ArrayList<>();
        for (Question question:questions.getData()){
            Answer answer=new Answer();
            answer.setAnswer(question.getAnswer());
            answer.setId(question.getId());
            answers.add(answer);
        }

        responseModel.setQuestions(answers);
        pushToServer(responseModel.serialize());
    }

    private void pushToServer(String s){
            binding.progressBar.setVisibility(View.VISIBLE);
        AppController.getRetrofitInstance().postJson(s).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                System.out.println("es = " + e);
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                binding.progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject=new JSONObject(responseBody.string());
                    if (jsonObject.optInt("status")==1){
                        Toast.makeText(context, jsonObject.optString("data"), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
