package com.pubnub.pubnub.config;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.message_actions.PNMessageAction;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;
import com.pubnub.pubnub.service.ChannelData;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class PubNubConfig {

    @Autowired
    ChannelData channelData;
    PNConfiguration pconfig = new PNConfiguration("xpro-03419748-a9b2-11ec-b909-0242ac120002-uuid");
    
    public PubNubConfig() throws PubNubException {
    }

    @Bean
    public PubNub getPubNubConfig() {
        pconfig.setSubscribeKey("sub-c-20b66778-f903-11eb-bf4c-22908b043f7e");
        pconfig.setPublishKey("pub-c-888b68c3-ebbf-442d-9842-2fcef0339814");
        pconfig.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        pconfig.setSecure(true);
        //pconfig.setLogVerbosity(PNLogVerbosity.BODY);
        return new PubNub(pconfig);
    }

    @PostConstruct
    public void pubnubConfig() {
        PubNub pubnub = getPubNubConfig();

        pubnub.addListener(new SubscribeCallback() {
            // PubNub status
            @Override
            public void status(@NotNull PubNub pubnub, @NotNull PNStatus pnStatus) {
                log.info("pubnub status called .... {} ------- {}", pnStatus.getOperation(), pubnub.getBaseUrl());
                if (pnStatus.isError() && pnStatus.getCategory().equals(PNStatusCategory.PNBadRequestCategory)) {
                    log.info("Bad request found... Retrying.... !");
                    pubnub.reconnect();
                }

                if (pnStatus.getCategory().equals(PNStatusCategory.PNConnectedCategory) || pnStatus.getCategory()
                    .equals(PNStatusCategory.PNReconnectedCategory)) {
                    log.info("retry counter set to 0.....");
                }
            }

            // Messages
            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                String messagePublisher = message.getPublisher();
                log.info("Message Channel : " + message.getChannel());
                log.info("Message Payload: " + message.getMessage());
                if (message.getChannel().equals("DEV")) {
                    channelData.sendMessage(message.getMessage().toString());
                }
            }

            // Presence
            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                log.info("Presence Event: " + presence.getEvent());
                log.info("Presence Channel: " + presence.getChannel());
                log.info("Presence Occupancy: " + presence.getOccupancy());
                log.info("Presence State: " + presence.getState());
                log.info("Presence UUID: " + presence.getUuid());

            }

            // Signals
            @Override
            public void signal(PubNub pubnub, PNSignalResult pnSignalResult) {
                PNSignalResult signal = pnSignalResult;
                log.info("Signal publisher: " + signal.getPublisher());
                log.info("Signal payload: " + signal.getMessage());
                log.info("Signal subscription: " + signal.getSubscription());
                log.info("Signal channel: " + signal.getChannel());
                log.info("Signal timetoken: " + signal.getTimetoken());
            }

            // Message actions
            @Override
            public void messageAction(PubNub pubnub, PNMessageActionResult pnActionResult) {
                PNMessageAction pnMessageAction = pnActionResult.getMessageAction();
                log.info("Message action type: " + pnMessageAction.getType());
                log.info("Message action value: " + pnMessageAction.getValue());
                log.info("Message action uuid: " + pnMessageAction.getUuid());
                log.info("Message action actionTimetoken: " + pnMessageAction.getActionTimetoken());
                log.info("Message action messageTimetoken: " + pnMessageAction.getMessageTimetoken());
                log.info("Message action subscription: " + pnActionResult.getSubscription());
                log.info("Message action channel: " + pnActionResult.getChannel());
                log.info("Message action timetoken: " + pnActionResult.getTimetoken());
            }

            // files
            @Override
            public void file(PubNub pubnub, PNFileEventResult pnFileEventResult) {
                log.info("File channel: " + pnFileEventResult.getChannel());
                log.info("File publisher: " + pnFileEventResult.getPublisher());
                log.info("File message: " + pnFileEventResult.getMessage());
                log.info("File timetoken: " + pnFileEventResult.getTimetoken());
                log.info("File file.id: " + pnFileEventResult.getFile().getId());
                log.info("File file.name: " + pnFileEventResult.getFile().getName());
                log.info("File file.url: " + pnFileEventResult.getFile().getUrl());
            }

            @Override
            public void uuid(PubNub pubnub, PNUUIDMetadataResult pnUUIDMetadataResult) {
                // TODO Auto-generated method stub

            }

            @Override
            public void channel(PubNub pubnub, PNChannelMetadataResult pnChannelMetadataResult) {
                // TODO Auto-generated method stub

            }

            @Override
            public void membership(PubNub pubnub, PNMembershipResult pnMembershipResult) {
                // TODO Auto-generated method stub
            }
        });

        pubnub.subscribe().channels(
                Arrays.asList("DEV"))
            .execute();
    }

}
