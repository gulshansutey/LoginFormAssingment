package com.technophile.userformassignment.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;
import com.technophile.userformassignment.network.AppController;
import com.technophile.userformassignment.activity.LoginActivity;
import com.technophile.userformassignment.database.MyDataBase;
import com.technophile.userformassignment.model.Questions;
import com.technophile.userformassignment.R;
import com.technophile.userformassignment.databinding.FragmentUserInfoBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class UserInfoFragment extends Fragment implements View.OnClickListener {


    private FragmentUserInfoBinding binding;
    private SharedPreferences sharedPreferences;
    private Context context;
    private String avatar_url;

    public UserInfoFragment() {
    }

    public static UserInfoFragment newInstance() {
        return new UserInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_info, container, false);
        return binding.getRoot();
    }

    private List<String> ageList = new ArrayList<>();
    private  MyDataBase myDataBase;
    private String id;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        ((LoginActivity) context).setToolbarTitle("Questions");
        if (sharedPreferences.getString("list", null) == null) {
            getQA();
        }

          myDataBase = new MyDataBase(context);
        binding.btnUpdate.setOnClickListener(this);
        HashMap<String, String> user_info = myDataBase.getUserInfo();
            id= user_info.get(MyDataBase.KEY_ID);
        binding.etEmail.setText(user_info.get(MyDataBase.KEY_EMAIL));

        binding.etName.setText(user_info.get(MyDataBase.KEY_NAME));

        binding.etMobile.setText(TextUtils.isEmpty(user_info.get(MyDataBase.KEY_MOBILE)) ? "N/A" : user_info.get(MyDataBase.KEY_MOBILE));

        if (user_info.get(MyDataBase.KEY_GENDER) != null && user_info.get(MyDataBase.KEY_GENDER).equals("male")) {
            binding.rbFemale.setChecked(false);
            binding.rbMale.setChecked(true);
        } else {
            binding.rbFemale.setChecked(true);
            binding.rbMale.setChecked(false);
        }


        int pos = 0;
        for (int i = 10; i < 85; i++) {
            if (user_info.get(MyDataBase.KEY_AGE).equals("" + i)) {
                pos = i;
            }
            ageList.add(i + "");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, ageList);
        binding.spinnerAge.setAdapter(adapter);
        binding.spinnerAge.setSelection(pos);
        avatar_url=user_info.get(MyDataBase.KEY_PIC);
        Picasso.get()
                .load(avatar_url)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.ivAvatar);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    private void getQA() {
        AppController.getRetrofitInstance().fetchFromUrl("v1/test/questions")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Questions>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Questions ques) {
                sharedPreferences.edit().putString("list", ques.serialize()).apply();
            }
        });
    }

    @Override
    public void onClick(View v) {

        ContentValues values = new ContentValues();

        values.put(MyDataBase.KEY_NAME,binding.etName.getText().toString());
        values.put(MyDataBase.KEY_EMAIL,binding.etEmail.getText().toString());
        values.put(MyDataBase.KEY_GENDER,binding.rbMale.isSelected()?"male":"female");
        values.put(MyDataBase.KEY_AGE,ageList.get(binding.spinnerAge.getSelectedItemPosition()));
        values.put(MyDataBase.KEY_ID,id);
        values.put(MyDataBase.KEY_PIC,avatar_url);
        myDataBase.updateInfo(values);

        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .addToBackStack("chat").hide(this)
                    .add(android.R.id.content, ChatFragment.newInstance()).commit();
        }
    }


}
