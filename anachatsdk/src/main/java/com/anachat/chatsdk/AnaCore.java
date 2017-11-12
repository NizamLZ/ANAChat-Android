package com.anachat.chatsdk;

import android.content.Context;
import android.support.annotation.NonNull;

import com.anachat.chatsdk.internal.AnaConfigBuilder;
import com.anachat.chatsdk.internal.AnaCoreFactory;
import com.anachat.chatsdk.internal.database.MessageRepository;
import com.anachat.chatsdk.internal.database.PreferencesManager;
import com.anachat.chatsdk.internal.model.Content;
import com.anachat.chatsdk.internal.model.Data;
import com.anachat.chatsdk.internal.model.Message;
import com.anachat.chatsdk.internal.model.MessageResponse;
import com.anachat.chatsdk.internal.model.inputdata.Input;
import com.anachat.chatsdk.internal.network.ApiCalls;
import com.anachat.chatsdk.internal.utils.ListenerManager;
import com.anachat.chatsdk.internal.utils.concurrent.ApiExecutor;
import com.anachat.chatsdk.internal.utils.concurrent.ApiExecutorFactory;
import com.anachat.chatsdk.internal.utils.concurrent.PushConsumer;
import com.anachat.chatsdk.internal.utils.constants.Constants;
import com.google.gson.Gson;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public final class AnaCore {

    public static ConfigBuilder config() {
        return new AnaConfigBuilder();
    }

    public static void install(AnaChatSDKConfig config,
                               MessageListener listener) {
        AnaCoreFactory.create(config, listener);
    }

    public static Message getLastMessage(Context context) {
        try {
            return MessageRepository.getInstance(context).getLastMessage().get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateToken(Context context, String refreshedToken,
                                   @NonNull String baseUrl, @NonNull String businessId) {
        PreferencesManager.getsInstance(context).setBusinessId(businessId);
        PreferencesManager.getsInstance(context).setBaseUrl(baseUrl);
        ApiCalls.updateToken(context, refreshedToken, null);
    }


    public static void handlePush(final Context context, final String payload) {
        try {
            MessageResponse messageResponse =
                    new Gson().fromJson(payload, MessageResponse.class);
            int messageType = messageResponse.getData().getType();
            messageResponse.getMessage().setMessageType(messageType);
            messageResponse.getMessage().setSyncWithServer(true);
            messageResponse.setNotifyMessage(true);
            PushConsumer.getInstance(context).addTask(messageResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void disableCarousel(Context context) {
//        try {
//            MessageRepository.getInstance(context).
//                    updateCarouselMessage();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public static long getOldestTimeStamp(Context context) {
        try {
            return MessageRepository.getInstance(context).getFirstMessage().get(0).getTimestamp();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static void loadMoreMessages(Context context, int totalItems, int page, long timestamp) {
        ApiExecutor apiExecutor = ApiExecutorFactory.getHandlerExecutor();
        apiExecutor.runAsync(() -> {
            try {
                List<Message> messages =
                        MessageRepository.getInstance(context).loadHistoryMessages(totalItems);
                if (messages != null && messages.size() > 0) {
                    ListenerManager.getInstance().notifyHistoryLoaded(messages);
                } else {
                    ApiCalls.fetchHistoryMessages(context, page, totalItems, timestamp);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void loadInitialHistory(Context context) {
        ApiCalls.fetchHistoryMessages(context, 0, 0, 0);
    }

    public static void addWelcomeMessage(Context context) {
        MessageResponse.MessageResponseBuilder responseBuilder
                = new MessageResponse.MessageResponseBuilder(context);
        MessageResponse messageResponse = responseBuilder.build();
        Data data
                = new Data();
        data.setType(Constants.MessageType.INPUT);
        data.setContent(new Content());
        data.getContent().setInputType(Constants.InputType.TEXT);
        data.getContent().setMandatory(Constants.FCMConstants.MANDATORY_TRUE);
        Input input
                = new Input();
        input.setVal("Get Started");
        data.getContent().setInput(input);
        messageResponse.setData(data);
        int messageType = messageResponse.getData().getType();
        messageResponse.getMessage().setMessageType(messageType);
        messageResponse.getMessage().setSyncWithServer(false);
        messageResponse.getMessage().setTimestamp(System.currentTimeMillis());
        MessageRepository.getInstance(context).handleMessageResponse(messageResponse);
    }


}

