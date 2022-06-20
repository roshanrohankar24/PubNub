package com.pubnub.pubnub.service;

import com.google.gson.JsonObject;
import com.pubnub.pubnub.config.PubnubUtil;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelData implements Channel {

    @Autowired
    PubnubUtil pubnubUtil;

    @Override
    public void sendMessage(String toString) {
        JsonObject object = new JsonObject();
        object.addProperty("name", "Roshan");
        object.addProperty("message", toString);
        pubnubUtil.publishMessage(object, Collections.singletonList("roshan"));
    }
}
