package com.ukejee.das;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ukejee.das.databinding.ActivityProfileBinding;
import com.ukejee.das.models.EmergencyContact;
import com.ukejee.das.models.User;
import com.ukejee.das.models.response.GetUserResponse;
import com.ukejee.das.web.AppRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    private SharedPreferences sharedPreferences;
    private AppRepository repository;
    private User currentUser;

    private static final String APP_PREFERENCES = "AppPrefs";
    private static final String TOKEN_KEY = "api_token";
    private static final String WAS_USER_DETAILS_UPDATED = "was_user_updated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = new AppRepository();
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        currentUser = (User) getIntent().getExtras().getSerializable("User");

        if(sharedPreferences.getBoolean(WAS_USER_DETAILS_UPDATED, false)){
            getUser(sharedPreferences.getString(TOKEN_KEY, null), currentUser.getId());
        }
        setUpUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sharedPreferences.getBoolean(WAS_USER_DETAILS_UPDATED, false)){
            getUser(sharedPreferences.getString(TOKEN_KEY, null), currentUser.getId());
        }
    }

    private void setUpUi(){
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), EditProfileActivity.class);
                intent.putExtra("User", currentUser);
                startActivity(intent);
            }
        });
        binding.emergencyContactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), EmergencyContactListActivity.class);
                intent.putExtra("User", currentUser);
                startActivity(intent);
            }
        });

        binding.userFirstName.setText(currentUser.getFirstName());
        binding.userLastName.setText(currentUser.getLastName());
        binding.userEmail.setText(currentUser.getEmail());
        binding.userPhoneNo.setText(currentUser.getPhoneNumber());
    }

    private void getUser(String token, int id){
        showProgressDialog();;
        repository.getDatabaseApiService().getUser(token, id)
                .enqueue(new Callback<GetUserResponse>() {
                    @Override
                    public void onResponse(Call<GetUserResponse> call, Response<GetUserResponse> response) {
                        cancelProgressDialog();
                        if(response.isSuccessful()){
                            currentUser = response.body().getUser();
                            sharedPreferences.edit().putBoolean(WAS_USER_DETAILS_UPDATED, false).apply();
                        }
                        else{
                            showMessage("Error", "Something went wrong");
                        }

                    }

                    @Override
                    public void onFailure(Call<GetUserResponse> call, Throwable t) {
                        cancelProgressDialog();
                        showMessage("Error", "Check Internet Connection");
                    }
                });
    }
}
