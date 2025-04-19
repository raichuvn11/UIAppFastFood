package com.example.uiappfastfood.service;

import com.example.uiappfastfood.model.CartItem;
import com.example.uiappfastfood.model.Coupon;
import com.example.uiappfastfood.model.DeviceTokenRequest;
import com.example.uiappfastfood.model.FavoriteItem;
import com.example.uiappfastfood.model.Order;
import com.example.uiappfastfood.model.OrderStatus;
import com.example.uiappfastfood.model.User;

import java.util.List;

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
    @GET("cart/item")
    Call<List<CartItem>> getCartItems(@Query("userId") int userId);

    @GET("user/info")
    Call<User> getUserProfile(@Query("userId") int userId);

    @POST("order/create")
    Call<Long> createOrder(@Body Order order);

    @GET("order")
    Call<Order> getOrderById(@Query("orderId") Long orderId);

    @PUT("order/update")
    Call<ResponseBody> updateOrder(@Query("orderId") Long orderId, @Query("status") String status, @Query("rating") int rating, @Query("review") String review);

    @DELETE("order/delete")
    Call<ResponseBody> deleteOrder(@Query("orderId") Long orderId);

    @POST("user/update-token")
    Call<Void> updateDeviceToken(@Body DeviceTokenRequest request);

    @Multipart
    @PUT("user/update-profile")
    Call<Void> updateUserProfile(
            @Part("id") RequestBody id,
            @Part("username") RequestBody username,
            @Part("email") RequestBody email,
            @Part("phone") RequestBody phone,
            @Part("address") RequestBody address,
            @Part("img") RequestBody img,
            @Part MultipartBody.Part file
    );

    @GET("order/list-status")
    Call<List<OrderStatus>> getOrdersByStatus(@Query("status") String status, @Query("userId") Long userId);

    @GET("order/apply-code")
    Call<Coupon> applyCouponCode(@Query("code") String code);

    @GET("favorites/{userId}")
    Call<List<FavoriteItem>> getFavoriteItems(@Path("userId") Long userId);
}
