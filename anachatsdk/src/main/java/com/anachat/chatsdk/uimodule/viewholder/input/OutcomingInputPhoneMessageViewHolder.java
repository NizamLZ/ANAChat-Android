package com.anachat.chatsdk.uimodule.viewholder.input;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anachat.chatsdk.internal.database.PreferencesManager;
import com.anachat.chatsdk.internal.model.Message;
import com.anachat.chatsdk.internal.utils.NFChatUtils;
import com.anachat.chatsdk.internal.utils.constants.Constants;
import com.anachat.chatsdk.uimodule.chatuikit.messages.MessageHolders;
import com.anachat.chatsdk.uimodule.chatuikit.utils.DateFormatter;
import com.anachat.chatsdk.library.R;

public class OutcomingInputPhoneMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    private TextView tvDuration;
    private TextView tvTime;
    private ImageView ivSentStatus;
    public OutcomingInputPhoneMessageViewHolder(View itemView) {
        super(itemView);
        tvDuration = itemView.findViewById(R.id.messageText);
        tvTime = itemView.findViewById(R.id.messageTime);
        ivSentStatus = itemView.findViewById(R.id.iv_sent_status);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        ivSentStatus.setColorFilter(Color.parseColor(PreferencesManager.
                getsInstance(imageLoader.getContext()).getThemeColor()));
        if (message.getSyncWithServer()) {
            ivSentStatus.setImageDrawable
                    (ContextCompat.getDrawable(imageLoader.getContext(), R.drawable.ic_tick));
        } else {
            ivSentStatus.setImageDrawable
                    (ContextCompat.getDrawable(imageLoader.getContext(), R.drawable.ic_wait));
        }
        if (message.getMessageInput().getMandatory()== Constants.FCMConstants.MANDATORY_TRUE) {
            tvDuration.setText(message.getMessageInput().getInputTypePhone().getInput().getVal());
        } else {
            tvDuration.setText(message.getMessageInput().getInputTypePhone().getInput().getInput());
        }
        tvTime.setText(DateFormatter.format(message.getCreatedAt(), DateFormatter.Template.TIME));
    }
}
