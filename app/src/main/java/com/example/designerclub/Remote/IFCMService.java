package com.example.designerclub.Remote;

import com.example.designerclub.Models.FCMResponse;
import com.example.designerclub.Models.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAzkiH50E:APA91bETfnykxzAHx7Q4g5EFcPqZRHuyWi8S72OlnzFvxY4x_9o5MWl-BuxSB2ZUYt73Im1RJsOb55d7cTe3IPZR4fLIAaoJd_CaH1THCq3lxV4Rs6DfDdTtrC2HgIKNEgON2rD2ICGb"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);

}
