package com.bharatbloodbank.bharatbloodbank.Remote;


import com.bharatbloodbank.bharatbloodbank.Model.MyResponse;
import com.bharatbloodbank.bharatbloodbank.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAWbdoHTY:APA91bGebYRYjj-mM8U2P8CXPmj_uTTAO0ormcwrbIYNYVWZpnwDYsP3LpvwX_3iPAG79j6bthEKDHYJ9-dsYNiD5yGtAOJsWTGghEMwYbRQl3_beZhHv6Os5PS-5O8dtcpVhVEnToV3"
            }

    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
