package com.ukejee.das.web;

import com.ukejee.das.models.CreateMessageRequestBody;
import com.ukejee.das.models.EmergencyContact;
import com.ukejee.das.models.response.AddContactResponse;
import com.ukejee.das.models.response.CreateMessageResponse;
import com.ukejee.das.models.response.GetContactsResponse;
import com.ukejee.das.models.response.GetMessageResponse;
import com.ukejee.das.models.response.GetUserResponse;
import com.ukejee.das.models.response.CreateUserResponse;
import com.ukejee.das.models.LoginRequestBody;
import com.ukejee.das.models.User;
import com.ukejee.das.models.google.GoogleNearbySearchResponse;
import com.ukejee.das.models.response.SendAlertResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author .: Ukeje Emeka
 * @email ..: ukejee3@gmail.com
 * @created : 6/4/20
 */
public interface ApiService {

    @GET("place/nearbysearch/json")
    Call<GoogleNearbySearchResponse> getNearbySearch(@Query("key") String apiKey,
                                                     @Query("location") String location,
                                                     @Query("radius") String radius,
                                                     @Query("type") String type);

    @POST("users/register")
    Call<CreateUserResponse> registerUser(@Body User newUser);

    @POST("users/login")
    Call<CreateUserResponse> loginUser(@Body LoginRequestBody loginRequestBody);

    @GET("users/{id}")
    Call<GetUserResponse> getUser(@Header ("Authorization") String token, @Path("id") int id);

    @PUT("users/{id}")
    Call<GetUserResponse> editUser(@Header ("Authorization") String token, @Path("id") int id,
                                   @Body User newUser);

    @POST("contacts")
    Call<AddContactResponse> addEmergencyContact(@Header ("Authorization") String token,
                                                 @Body EmergencyContact contact);

    @GET("contacts")
    Call<GetContactsResponse> getEmergencyContacts(@Header ("Authorization") String token);

    @DELETE("contacts/{id}")
    Call<String> deleteEmergencyContact(@Header ("Authorization") String token, @Path("id") int id);

    @POST("messages")
    Call<CreateMessageResponse> createMessage(@Header ("Authorization") String token,
                                              @Body CreateMessageRequestBody message);

    @PUT("messages")
    Call<CreateMessageResponse> editMessage(@Header ("Authorization") String token,
                                              @Body CreateMessageRequestBody message);

    @GET("messages")
    Call<GetMessageResponse> getMessage(@Header ("Authorization") String token);

    @GET("alerts/send")
    Call<SendAlertResponse> sendAlert(@Header ("Authorization") String token);
}
