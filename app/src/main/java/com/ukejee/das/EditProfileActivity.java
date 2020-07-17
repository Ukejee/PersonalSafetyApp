package com.ukejee.das;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ukejee.das.databinding.ActivityEditProfileBinding;
import com.ukejee.das.models.User;
import com.ukejee.das.models.response.GetUserResponse;
import com.ukejee.das.utils.AppUtils;
import com.ukejee.das.web.AppRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity {

    private ActivityEditProfileBinding binding;
    private SharedPreferences sharedPreferences;
    private AppRepository repository;
    private User currentUser;

    private static final String APP_PREFERENCES = "AppPrefs";
    private static final String TOKEN_KEY = "api_token";
    private static final String WAS_USER_DETAILS_UPDATED = "was_user_updated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currentUser = (User) getIntent().getSerializableExtra("User");
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        repository = new AppRepository();
        setUpUi();
    }

    private void setUpUi(){
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.userEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AppUtils(getBaseContext()).showSnackBar(binding.getRoot(),
                        "You can not edit the email");
            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showMessage("Are you sure?",
                        "You are about to edit your information",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                verifyInput();
                            }
                        });
            }
        });

        binding.firstNameInput.setText(currentUser.getFirstName());
        binding.lastNameInput.setText(currentUser.getLastName());
        binding.userOtherName.setText("None");
        binding.userEmail.setText(currentUser.getEmail());
        binding.userPhoneNo.setText(currentUser.getPhoneNumber());
    }

    private void verifyInput(){
        if(binding.firstNameInput.getText().toString().isEmpty()){
            showMessage(getString(R.string.missing_field), "Please enter your First Name");
        }
        else if(binding.lastNameInput.getText().toString().isEmpty()){
            showMessage(getString(R.string.missing_field), "Please enter your Last Name");
        }
        else if(binding.userPhoneNo.getText().toString().isEmpty() ||
        binding.userPhoneNo.getText().toString().length() != 11){
            showMessage(getString(R.string.missing_field), "Please enter a valid Phone Number");
        }
        else{
            User updatedUser = new User();
            updatedUser.setFirstName(binding.firstNameInput.getText().toString());
            updatedUser.setLastName(binding.lastNameInput.getText().toString());
            updatedUser.setPhoneNumber(binding.userPhoneNo.getText().toString());

            updateUserDetails(sharedPreferences.getString(TOKEN_KEY, null), currentUser.getId(),
                    updatedUser);
        }
    }

    private void updateUserDetails(String token, int userId, User updatedUser){
        showProgressDialog();
        repository.getDatabaseApiService().editUser(token, userId, updatedUser)
                .enqueue(new Callback<GetUserResponse>() {
                    @Override
                    public void onResponse(Call<GetUserResponse> call, Response<GetUserResponse> response) {
                        cancelProgressDialog();
                        if(response.isSuccessful()){
                            sharedPreferences.edit().putBoolean(WAS_USER_DETAILS_UPDATED, true).apply();
                            showMessage("Success", "User info has been updated",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            onBackPressed();
                                        }
                                    });
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
