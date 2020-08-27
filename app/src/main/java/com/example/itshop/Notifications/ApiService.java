package com.example.itshop.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService{

@Headers(
        {
                "Content-Type:application/json",
                "Authorization:key=AAAAhLz8hM0:APA91bHWEDDcyZlKqF6tKTaL70jkzn-8uc7t2mRJ_PXakTSBkmZhHy4Ib_Rclwrz5O8claEiV6nMcyNAXLd6Z0ruS6CMPwuyFDZbbrxZPNtvtuPIRiv4xf4VDGOy0xYgxqZ28_oiMMPa"
        }
        )
@POST("fcm/send")
Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}