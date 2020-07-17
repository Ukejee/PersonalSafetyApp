package com.ukejee.das;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ukejee.das.databinding.ActivityDistressAlertBinding;
import com.ukejee.das.models.CreateMessageRequestBody;
import com.ukejee.das.models.response.CreateMessageResponse;
import com.ukejee.das.models.response.GetContactsResponse;
import com.ukejee.das.models.response.SendAlertResponse;
import com.ukejee.das.utils.AppUtils;
import com.ukejee.das.web.AppRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DistressAlertActivity extends BaseActivity {

    private ActivityDistressAlertBinding binding;
    private AppRepository repository;
    private SharedPreferences sharedPreferences;

    private static final String APP_PREFERENCES = "AppPrefs";
    private static final String TOKEN_KEY = "api_token";
    private static final String IS_MESSAGE_SAVED = "is_message_saved";
    private static final String MESSAGE_KEY = "alert_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDistressAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = new AppRepository();
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        setUpUi();
    }

    private void setUpUi(){
        binding.changeDistressMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), EditMessageTemplateActivity.class));
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        String token = sharedPreferences.getString(TOKEN_KEY, null);
        getEmergencyContacts(token);
        if(!sharedPreferences.getBoolean(IS_MESSAGE_SAVED, false)){
            createMessage(token,
                    new CreateMessageRequestBody(sharedPreferences.getString(MESSAGE_KEY, null)));
        }
    }

    private void sendAlert(final String token){
        repository.getDatabaseApiService().sendAlert(token)
                .enqueue(new Callback<SendAlertResponse>() {
                    @Override
                    public void onResponse(Call<SendAlertResponse> call, Response<SendAlertResponse> response) {
                        if(response.isSuccessful()){
                            binding.successfulIcon.setVisibility(View.VISIBLE);
                            binding.progress.setVisibility(View.INVISIBLE);
                            binding.progressText.setText(R.string.distress_alert_success_message);
                        }
                        else{
                            sendAlert(token);
                        }
                    }

                    @Override
                    public void onFailure(Call<SendAlertResponse> call, Throwable t) {
                        sendAlert(token);
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
                            editor.apply();

                        }
                        else{
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(IS_MESSAGE_SAVED, false);
                            editor.apply();
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateMessageResponse> call, Throwable t) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(IS_MESSAGE_SAVED, false);
                        editor.apply();
                    }
                });
    }

    private void getEmergencyContacts(final String token){
        repository.getDatabaseApiService().getEmergencyContacts(token)
                .enqueue(new Callback<GetContactsResponse>() {
                    @Override
                    public void onResponse(Call<GetContactsResponse> call, Response<GetContactsResponse> response) {
                        if (response.isSuccessful() && response.body().getContacts().isEmpty()){
                            new AppUtils(getBaseContext()).showSnackBar(binding.getRoot(),
                                    "You have no registered emergency contacts");
                            showMessage("No Emergency Contact", "User has no emergency contact");
                        }
                        else{
                            sendAlert(token);
                        }
                    }

                    @Override
                    public void onFailure(Call<GetContactsResponse> call, Throwable t) {
                        sendAlert(token);
                    }
                });
    }


}
