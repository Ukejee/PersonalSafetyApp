package com.ukejee.das;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ukejee.das.databinding.ActivityEditMessageTemplateBinding;
import com.ukejee.das.models.CreateMessageRequestBody;
import com.ukejee.das.models.response.CreateMessageResponse;
import com.ukejee.das.models.response.GetMessageResponse;
import com.ukejee.das.web.AppRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditMessageTemplateActivity extends BaseActivity {

    private ActivityEditMessageTemplateBinding binding;
    private AppRepository repository;
    private SharedPreferences sharedPreferences;

    private static final String APP_PREFERENCES = "AppPrefs";
    private static final String TOKEN_KEY = "api_token";
    private static final String MESSAGE_KEY = "alert_message";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditMessageTemplateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = new AppRepository();
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        setUpUi();
    }

    private void setUpUi(){
        getMessage(sharedPreferences.getString(TOKEN_KEY, null));
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyInput();
            }
        });
    }

    private void verifyInput(){
        if(binding.messageTemplateInput.getText().toString().isEmpty()){
            showMessage(getString(R.string.missing_field), "Please enter a new message");
        }
        else{
            editMessage(sharedPreferences.getString(TOKEN_KEY, null),
                    new CreateMessageRequestBody(binding.messageTemplateInput.getText().toString()));
        }
    }

    private void getMessage(String token){
        showProgressDialog();
        repository.getDatabaseApiService().getMessage(token)
                .enqueue(new Callback<GetMessageResponse>() {
                    @Override
                    public void onResponse(Call<GetMessageResponse> call, Response<GetMessageResponse> response) {
                        cancelProgressDialog();
                        if(response.isSuccessful()) {
                            binding.messageTemplateInput.setText(response.body().getMessage().getText());
                        }
                        else{
                            showMessage("Error", "Something went wrong");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetMessageResponse> call, Throwable t) {
                        cancelProgressDialog();
                        showMessage("Error", "Check Internet Connection");
                    }
                });
    }

    private void editMessage(String token, final CreateMessageRequestBody message){
        repository.getDatabaseApiService().editMessage(token, message)
                .enqueue(new Callback<CreateMessageResponse>() {
                    @Override
                    public void onResponse(Call<CreateMessageResponse> call, Response<CreateMessageResponse> response) {
                        if(response.isSuccessful()){
                            showMessage("Success", "Your message template has been changed",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            sharedPreferences.edit().putString(MESSAGE_KEY, message.getText()).apply();
                                            onBackPressed();
                                        }
                                    });
                        }
                        else{
                            showMessage("Error", "Something went wrong");
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateMessageResponse> call, Throwable t) {
                        showMessage("Error", "Check Internet Connection");
                    }
                });
    }
}
