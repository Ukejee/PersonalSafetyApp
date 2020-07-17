package com.ukejee.das;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ukejee.das.databinding.ActivityAddEmergencyContactBinding;
import com.ukejee.das.models.EmergencyContact;
import com.ukejee.das.models.response.AddContactResponse;
import com.ukejee.das.utils.AppUtils;
import com.ukejee.das.web.AppRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEmergencyContactActivity extends BaseActivity {

    private ActivityAddEmergencyContactBinding binding;
    private SharedPreferences sharedPreferences;
    private AppRepository repository;
    private AppUtils appUtils;

    private static final String APP_PREFERENCES = "AppPrefs";
    private static final String TOKEN_KEY = "api_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEmergencyContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        appUtils = new AppUtils(this);
        repository = new AppRepository();
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        setUpUi();
    }

    private void setUpUi(){
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
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
        if(binding.nameInput.getText().toString().isEmpty()){
            showMessage("Missing Field", "Please enter contact's name");
        }
        else if(binding.phoneNoInput.getText().toString().isEmpty() ){
            showMessage("Missing Field", "Please enter contact's phone number");
        }
        else if(!isNumber(binding.phoneNoInput.getText().toString())){
            showMessage(getString(R.string.invalid_format), "Please enter valid phone number");
        }
        else if(binding.phoneNoInput.getText().toString().length() != 11){
            showMessage(getString(R.string.invalid_format), "Please enter valid phone number");
        }
        else{
            String formattedNumber = "+234" + binding.phoneNoInput.getText().toString().substring(1);
            EmergencyContact contact = new EmergencyContact(binding.nameInput.getText().toString(),
                    formattedNumber);
            appUtils.hideKeyboard();
            addEmergencyContact(contact);
        }
    }

    private void addEmergencyContact(EmergencyContact contact){
        showProgressDialog();
        repository.getDatabaseApiService().addEmergencyContact(sharedPreferences.getString(TOKEN_KEY, null), contact)
                .enqueue(new Callback<AddContactResponse>() {
                    @Override
                    public void onResponse(Call<AddContactResponse> call, Response<AddContactResponse> response) {
                        cancelProgressDialog();
                        if(response.isSuccessful()){
                            showMessage("Success", "Emergency Contact has been added",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            onBackPressed();
                                        }
                                    });
                        }
                        else{
                            showMessage("Limit Reach", "You have already have five emergency contacts," +
                                    "delete one to continue");
                        }
                    }

                    @Override
                    public void onFailure(Call<AddContactResponse> call, Throwable t) {
                        cancelProgressDialog();
                        showMessage("Error", "Check Internet Connection");
                    }
                });
    }

    private Boolean isNumber(String word){
        if (word == null) {
            return false;
        }
        try{
            Long.parseLong(word);
        }catch (NumberFormatException e){
            return false;
        }
        return true;
    }
}
