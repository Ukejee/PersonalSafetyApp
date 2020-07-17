package com.ukejee.das;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ukejee.das.adapter.EmergencyContactListAdapter;
import com.ukejee.das.databinding.ActivityEmergencyContactListBinding;
import com.ukejee.das.models.EmergencyContact;
import com.ukejee.das.models.response.GetUserResponse;
import com.ukejee.das.models.User;
import com.ukejee.das.web.AppRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyContactListActivity extends BaseActivity {

    private ActivityEmergencyContactListBinding binding;
    private List<EmergencyContact> emergencyContacts;
    private EmergencyContactListAdapter adapter;
    private AppRepository repository;
    private User currentUser;
    private SharedPreferences sharedPreferences;

    private static final String APP_PREFERENCES = "AppPrefs";
    private static final String tokenKey = "api_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmergencyContactListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new AppRepository();
        currentUser = (User) getIntent().getSerializableExtra("User");
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        setUpUi(this);
    }

    public void setUpUi(final Activity activity){
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), AddEmergencyContactActivity.class));
            }
        });

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEmergencyContactList(currentUser.getId(), activity);
            }
        });

        getEmergencyContactList(currentUser.getId(), activity);
    }

    private void getEmergencyContactList(int id, final Activity activity){
        binding.swipeRefreshLayout.setRefreshing(true);
        repository.getDatabaseApiService().getUser(sharedPreferences.getString(tokenKey, null), id)
                .enqueue(new Callback<GetUserResponse>() {
                    @Override
                    public void onResponse(Call<GetUserResponse> call, Response<GetUserResponse> response) {
                        binding.swipeRefreshLayout.setRefreshing(false);
                        if(response.isSuccessful()){
                            User user = response.body().getUser();
                            if(user.getContacts().size() > 0){
                                emergencyContacts = response.body().getUser().getContacts();
                                adapter = new EmergencyContactListAdapter(emergencyContacts,
                                        activity, repository, sharedPreferences.getString(tokenKey, null));
                                binding.recyclerView.setAdapter(adapter);
                            }
                            else{
                               showMessage("Not Found", "You have not added any emergency contact, " +
                                       "please do so and try again");
                            }
                        }
                        else{
                            showMessage("Error", "Something went wrong");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetUserResponse> call, Throwable t) {
                        binding.swipeRefreshLayout.setRefreshing(false);
                        showMessage("Error", "Check Internet Connection");
                        t.getMessage();
                    }
                });
    }
}
