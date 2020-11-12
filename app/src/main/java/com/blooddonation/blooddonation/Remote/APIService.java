package com.blooddonation.blooddonation.Remote;


import com.blooddonation.blooddonation.Model.MyResponse;
import com.blooddonation.blooddonation.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAArt0FDdM:APA91bGuG5qoV7I1WJL3k-EVVdpyOfvigvzFFtSrysT6uMUonZGaZa3WLUwoN84_qPxGmVzhAc90Vz7kFRptBKa0B6TPNKKZ-TFpx0_hHAAiS-ZCaV-FICa8hu7DGuyQLuyZSOW_VlhF"
            }

    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
