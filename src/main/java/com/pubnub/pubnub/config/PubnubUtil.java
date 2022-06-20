package com.pubnub.pubnub.config;

import com.google.gson.JsonObject;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PubnubUtil {

    private static final Logger logger = LoggerFactory.getLogger(PubnubUtil.class);

    @Autowired
    private PubNub pubNub;

    public void publishMessage(JsonObject requestJson, List<String> channelList) {

        for (String channel : channelList) {
            pubNub.publish().message(requestJson).channel(channel).async(new PNCallback<PNPublishResult>() {
                @Override
                public void onResponse(PNPublishResult result, PNStatus status) {
                    // handle publish result, status always present, result if successful
                    // status.isError() to see if error happened
                    if (!status.isError()) {
                        logger.info("pub timetoken: " + result.getTimetoken());
                    }
                    logger.info("pub status code: " + status.getStatusCode());
                }
            });
        }
    }
}
