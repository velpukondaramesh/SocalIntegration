package com.zappstech.socalintegration.api;


import com.zappstech.socalintegration.model.RegistrationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @author Pratik Butani.
 */
public interface ApiService {

    /*
    Retrofit get annotation with our URL
    And our method that will return us the List of ContactList
    */
   /* @FormUrlEncoded
    @POST("insertMembership")
    Call<Membership> insertMembership(@Field("name") String name,
                                      @Field("email") String email,
                                      @Field("contact") String contact);

    @FormUrlEncoded
    @POST("contact")
    Call<Contact> insertContact(@Field("name") String name,
                                @Field("email") String email,
                                @Field("phone") String contact,
                                @Field("description") String description);

    @GET("getDairy")
    Call<DairyResponse> getDairy();

    @GET("getUpdates")
    Call<UpdateResponse> getUpdates();

    @FormUrlEncoded
    @POST("localBodies")
    Call<LocalBodiesResponse> getLocalbodies(@Field("type") String type);

    @GET("getGallery")
    Call<List<Gallery>> getGallery();*/

    /*@GET("/v1/OrderReport.json")
    Call<POJO_Class> getExampleMethod(@Header("Authorization") String token, @Query("id") String id);*/

    /*call it from activity
    getExampleMethod("Basic " + token, id);*/

    @FormUrlEncoded
    @POST("insertMembership")
    Call<RegistrationResponse> insertNewUser(@Field("name") String name,
                                             @Field("email") String email,
                                             @Field("password") String password,
                                             @Field("contact") String contact,
                                             @Field("gender") String gender,
                                             @Field("date_birth") String date_birth,
                                             @Field("country") String country);

}
