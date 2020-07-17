package com.ukejee.das;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ukejee.das.databinding.ActivitySignupBinding;
import com.ukejee.das.models.CreateMessageRequestBody;
import com.ukejee.das.models.response.CreateMessageResponse;
import com.ukejee.das.models.response.CreateUserResponse;
import com.ukejee.das.models.User;
import com.ukejee.das.web.AppRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends BaseActivity {

    private ActivitySignupBinding binding;
    private AppRepository repository;
    private SharedPreferences sharedPreferences;
    private Intent intent;

    private static final String APP_PREFERENCES = "AppPrefs";
    private static final String TOKEN_KEY = "api_token";
    private static final String IS_MESSAGE_SAVED = "is_message_saved";
    private static final String MESSAGE_KEY = "alert_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        repository = new AppRepository();
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        intent = new Intent(getBaseContext(), HomeActivity.class);
        setUpUi();
    }

    private void setUpUi(){
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyInput();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void verifyInput(){
        if(binding.firstNameInput.getText().toString().isEmpty()){
            showMessage("Missing Field", "Please enter your first name");
        }
        else if(binding.lastNameInput.getText().toString().isEmpty()){
            showMessage("Missing Field", "Please enter your last name");
        }
        else if(binding.emailInput.getText().toString().isEmpty()){
            showMessage("Missing Field", "Please enter your email");
        }
        else if(binding.passwordInput.getText().toString().isEmpty()){
            showMessage("Missing Field", "Please enter your password");
        }
        else{
            User newUser = new User();
            newUser.setFirstName(binding.firstNameInput.getText().toString());
            newUser.setLastName(binding.lastNameInput.getText().toString());
            newUser.setEmail(binding.emailInput.getText().toString());
            newUser.setPassword(binding.passwordInput.getText().toString());
            createUser(newUser);
        }

    }

    private void createUser(User newUser){
        showProgressDialog();
        repository.getDatabaseApiService().registerUser(newUser)
                .enqueue(new Callback< CreateUserResponse >(){
                    @Override
                    public void onResponse(Call<CreateUserResponse> call, Response<CreateUserResponse> response) {
                        cancelProgressDialog();
                        if(response.isSuccessful()) {
                            intent.putExtra("User", response.body().getUser());

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(TOKEN_KEY, response.body().getToken());
                            editor.apply();

                            createMessage(response.body().getToken(),
                                    new CreateMessageRequestBody("Help, I'm in danger"));
                        }
                        else{
                            showMessage("Error", "Something went wrong");
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateUserResponse> call, Throwable t) {
                        cancelProgressDialog();
                        showMessage("Error", "Check network connection");
                    }
                });
    }

    private void createMessage(String token, final CreateMessageRequestBody message){
        repository.getDatabaseApiService().createMessage(token, message)
                .enqueue(new Callback<CreateMessageResponse>() {
                    @Override
                    public void onResponse(Call<CreateMessageResponse> call, Response<CreateMessageResponse> response) {
                        if(response.isSuccessful()){
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(IS_MESSAGE_SAVED, true);
                            editor.putString(MESSAGE_KEY, message.getText());
                            editor.apply();
                            startActivity(intent);

                        }
                        else{
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(IS_MESSAGE_SAVED, false);
                            editor.putString(MESSAGE_KEY, message.getText());
                            editor.apply();
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateMessageResponse> call, Throwable t) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(IS_MESSAGE_SAVED, false);
                        editor.putString(MESSAGE_KEY, message.getText());
                        editor.apply();
                        startActivity(intent);
                    }
                });
    }
}
