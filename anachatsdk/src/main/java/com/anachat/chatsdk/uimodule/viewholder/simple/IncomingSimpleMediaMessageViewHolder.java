package com.anachat.chatsdk.uimodule.viewholder.simple;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anachat.chatsdk.internal.database.PreferencesManager;
import com.anachat.chatsdk.internal.model.Media;
import com.anachat.chatsdk.internal.model.Message;
import com.anachat.chatsdk.library.R;
import com.anachat.chatsdk.uimodule.chatuikit.messages.MessageHolders;
import com.anachat.chatsdk.uimodule.chatuikit.utils.DateFormatter;

public class IncomingSimpleMediaMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    private ImageView ivPreviewMedia;
    private TextView tvTime;
    private TextView tvType;

    public IncomingSimpleMediaMessageViewHolder(View itemView) {
        super(itemView);
        ivPreviewMedia = itemView.findViewById(R.id.image);
        tvTime = itemView.findViewById(R.id.messageTime);
        tvType = itemView.findViewById(R.id.message_type);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        bubble.setBackgroundColor(Color.parseColor
                (PreferencesManager.getsInstance(imageLoader.getContext()).getThemeColor()));
        triangle.setColorFilter(Color.parseColor
                (PreferencesManager.getsInstance(imageLoader.getContext()).getThemeColor()));
        if (!imageLoader.isPreviousMessageHasSameAuthor(message.getUserId(), getAdapterPosition())) {
            triangle.setVisibility(View.GONE);
        } else {
            triangle.setVisibility(View.VISIBLE);
        }
        Media media = message.getMessageSimple().getMedia();
        imageLoader.loadImage(ivPreviewMedia, media.getUrl());
        tvTime.setText(DateFormatter.format(message.getCreatedAt(), DateFormatter.Template.TIME));

        if (message.getMessageSimple().getText() != null
                && !message.getMessageSimple().getText().isEmpty()) {
            tvType.setText(message.getMessageSimple().getText());
        } else {
            switch (media.getType()) {
                case 2:
                    tvType.setText("VIDEO");
                    break;
                case 1:
                    break;
                case 0:
                    tvType.setText("PHOTO");
                    break;
            }
        }
        itemView.setOnClickListener(view -> {
            imageLoader.openMedia(media.getUrl(), media.getType());

        });
    }
}
