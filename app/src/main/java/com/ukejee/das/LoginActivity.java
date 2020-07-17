package com.ukejee.das;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ukejee.das.databinding.ActivityLoginBinding;
import com.ukejee.das.models.response.CreateUserResponse;
import com.ukejee.das.models.LoginRequestBody;
import com.ukejee.das.utils.AppUtils;
import com.ukejee.das.web.AppRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private AppRepository repository;
    private SharedPreferences sharedPreferences;

    private static final String APP_PREFERENCES = "AppPrefs";
    private static final String tokenKey = "api_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        repository = new AppRepository();
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        setUpUi();

    }

    private void setUpUi(){

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), SignupActivity.class));
            }
        });

        binding.signInBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                verifyInput();
            }
        });
    }

    private void verifyInput(){
        if(binding.emailInput.getText().toString().isEmpty() ||
                !binding.emailInput.getText().toString().contains("@")){
            showMessage("Missing Field", "Please enter your email");
        }
        else if(binding.emailInput.getText().toString().isEmpty()){
            showMessage("Missing Field", "Please enter your password");
        }
        else{
            LoginRequestBody loginRequestBody = new LoginRequestBody(binding.emailInput.getText().toString(),
                    binding.passwordInput.getText().toString());
            loginUser(loginRequestBody);
        }
    }

    private void loginUser(LoginRequestBody loginRequestBody){
        showProgressDialog();
        repository.getDatabaseApiService().loginUser(loginRequestBody)
                .enqueue(new Callback<CreateUserResponse>() {
                    @Override
                    public void onResponse(Call<CreateUserResponse> call, Response<CreateUserResponse> response) {
                        cancelProgressDialog();
                        if(response.isSuccessful()){
                            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                            intent.putExtra("User", response.body().getUser());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(tokenKey, response.body().getToken());
                            editor.apply();

                            startActivity(intent);
                        }
                        else{
                            showMessage("Error", "Invalid Username or Password");
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateUserResponse> call, Throwable t) {
                        cancelProgressDialog();
                        showMessage("Error", "Check Network Connectivity");
                    }
                });
    }


}
