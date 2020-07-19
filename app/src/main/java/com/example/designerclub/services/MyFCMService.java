package com.example.designerclub.services;

import com.example.designerclub.Common.common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFCMService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String,String> dataRecv = remoteMessage.getData();

         if (dataRecv != null){
             common.showNotification(this,new Random().nextInt(),
                     dataRecv.get(common.NOTI_TITLE),
                     dataRecv.get(common.NOTI_CONTENT),
                     null);
         }

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        common.updateToken(this,s);
    }
}
