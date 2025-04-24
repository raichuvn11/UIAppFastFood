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
import com.example.uiappfastfood.model.CartItem;
import com.example.uiappfastfood.model.Category;
import com.example.uiappfastfood.model.Coupon;
import com.example.uiappfastfood.model.DeviceTokenRequest;
import com.example.uiappfastfood.model.FavoriteItem;
import com.example.uiappfastfood.model.MenuItem;
import com.example.uiappfastfood.model.Order;
import com.example.uiappfastfood.model.OrderStatus;
import com.example.uiappfastfood.model.RecentOrderModel;
import com.example.uiappfastfood.model.RecentSearch;
import com.example.uiappfastfood.model.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    @GET("api/cart/item")
    Call<List<CartItem>> getCartItems(@Query("userId") int userId);

    @GET("api/user/info")
    Call<User> getUserProfile(@Query("userId") int userId);

    @POST("api/order/create")
    Call<Long> createOrder(@Body Order order);

    @GET("api/order")
    Call<Order> getOrderById(@Query("orderId") Long orderId);

    @PUT("api/order/update")
    Call<ResponseBody> updateOrder(@Query("orderId") Long orderId, @Query("status") String status, @Query("rating") int rating, @Query("review") String review);

    @DELETE("api/order/delete")
    Call<ResponseBody> deleteOrder(@Query("orderId") Long orderId);

    @POST("api/user/update-token")
    Call<Void> updateDeviceToken(@Body DeviceTokenRequest request);

    @Multipart
    @POST("api/user/update-profile")
    Call<Void> updateUserProfile(
            @Part("id") RequestBody id,
            @Part("username") RequestBody username,
            @Part("email") RequestBody email,
            @Part("phone") RequestBody phone,
            @Part("address") RequestBody address,
            @Part("img") RequestBody img,
            @Part MultipartBody.Part file
    );

    @GET("api/order/list-status")
    Call<List<OrderStatus>> getOrdersByStatus(@Query("status") String status, @Query("userId") Long userId);

    @GET("api/order/apply-code")
    Call<Coupon> applyCouponCode(@Query("code") String code);

    @GET("api/favorites/{userId}")
    Call<List<FavoriteItem>> getFavoriteItems(@Path("userId") Long userId);

    @DELETE("api/cart/delete-item")
    Call<Boolean> deleteCartItem(@Query("userId") Long userId, @Query("itemId") Long itemId);
}
