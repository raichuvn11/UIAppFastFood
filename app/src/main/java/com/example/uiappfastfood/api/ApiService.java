package com.example.uiappfastfood.api;



import com.example.uiappfastfood.DTO.request.AddToCartRequest;
import com.example.uiappfastfood.DTO.request.CheckOTPRequest;
import com.example.uiappfastfood.DTO.request.EmailRequest;
import com.example.uiappfastfood.DTO.request.GoogleLoginRequest;
import com.example.uiappfastfood.DTO.request.LoginRequest;
import com.example.uiappfastfood.DTO.request.RecentSearchRequest;
import com.example.uiappfastfood.DTO.request.RegisterRequest;
import com.example.uiappfastfood.DTO.request.ResetPasswordRequest;
import com.example.uiappfastfood.DTO.request.VerifyOtpRequest;
import com.example.uiappfastfood.DTO.response.AutoCompleteResponse;
import com.example.uiappfastfood.DTO.response.GenericResponse;
import com.example.uiappfastfood.DTO.response.LoginResponse;
import com.example.uiappfastfood.DTO.response.UserResponseDTO;
import com.example.uiappfastfood.model.Category;
import com.example.uiappfastfood.model.MenuItem;
import com.example.uiappfastfood.model.RecentOrderModel;
import com.example.uiappfastfood.model.RecentSearch;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("/api/auth/google")
    Call<LoginResponse> loginWithGoogle(@Body GoogleLoginRequest request);

    @POST("/api/auth/register")
    Call<GenericResponse> register(@Body RegisterRequest request);

    @POST("/api/auth/verify-otp")
    Call<GenericResponse> verifyOtp(@Body VerifyOtpRequest request);

    @POST("/api/auth/forgot-password")
    Call<GenericResponse> sendResetOtp(@Body EmailRequest request);

    @POST("/api/auth/reset-password")
    Call<GenericResponse> resetPassword(@Body ResetPasswordRequest request);

    @POST("/api/auth/check-otp")
    Call<GenericResponse> checkOtp(@Body CheckOTPRequest request);

    @GET("api/cate")
    Call<List<Category>> getAllCategories();

    // Thêm sản phẩm yêu thích
    @POST("/api/favorites/{userId}/{menuItemId}")
    Call<Void> addFavoriteItem(@Path("userId") long userId, @Path("menuItemId") long menuItemId);

    // Xóa sản phẩm yêu thích
    @DELETE("/api/favorites/{userId}/{menuItemId}")
    Call<Void> removeFavoriteItem(@Path("userId") long userId, @Path("menuItemId") long menuItemId);

    @GET("/api/item/list-menu-item")
    Call<List<MenuItem>> getMenuItemsByCategoryId(@Query("categoryId") int categoryId);

    @POST("/api/cart/add")
    Call<Map<String, Object>> addToCart(@Body AddToCartRequest request);

    @GET("/Place/AutoComplete")
    Call<AutoCompleteResponse> getAutoComplete(
            @Query("input") String input,
            @Query("api_key") String apikey
    );

    @GET("/api/user/info")
    Call<UserResponseDTO> getUserProfile(@Query("userId") long userId);

    @PUT("api/user/{userId}/address")
    Call<UserResponseDTO> updateAddress(@Path("userId") Long userId, @Body String newAddress);

    @GET("/api/item/all-item")
    Call<List<MenuItem>> getAllMenuItems();

    @POST("/api/recent-searches/save")
    Call<Void> saveSearch(@Body RecentSearchRequest request);

    @POST("/api/recent-searches/list")
    Call<List<RecentSearch>> getRecentSearches(@Body RecentSearchRequest request);

    @POST("/api/recent-searches/clear")
    Call<Void> clearAllSearches(@Body RecentSearchRequest request);

    @POST("/api/recent-searches/delete")
    Call<Void> deleteSearch(@Body RecentSearchRequest request);

    @GET("/api/order/recent-items/{userId}")
    Call<List<MenuItem>> getRecentOrderItems(@Path("userId") long userId);
}
