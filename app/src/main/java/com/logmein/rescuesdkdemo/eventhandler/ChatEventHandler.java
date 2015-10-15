package com.logmein.rescuesdkdemo.eventhandler;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.logmein.rescuesdk.api.chat.ChatClient;
import com.logmein.rescuesdk.api.chat.event.ChatConnectedEvent;
import com.logmein.rescuesdk.api.chat.event.ChatDisconnectedEvent;
import com.logmein.rescuesdk.api.chat.event.TechnicianTypingEvent;
import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdkdemo.config.Config;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Groups and handles chat-related events.
 */
public final class ChatEventHandler {

    // Used by the CountDownTimer as the countdown interval.
    private static final long COUNTDOWN_INTERVAL = 1000L;

    private final WeakReference<EditText> chatInput;
    private final WeakReference<Button> chatSend;
    private final WeakReference<TextView> typingNotification;

    private ChatClient chatClient;
    private AtomicBoolean isTechnicianTyping;
    private CountDownTimer technicianTypingAppearanceTimer;

    /**
     * Constructs a ChatEventHandler object with the given parameters.
     * @param chatInput The EditText which provides the messages to be sent to the technician.
     * @param chatSend The Button which fires the action to send the actual message.
     * @param typingNotification The TextView which displays the remote typing notification.
     */
    public ChatEventHandler(final EditText chatInput, final Button chatSend, final TextView typingNotification) {
        this.chatInput = new WeakReference<EditText>(chatInput);
        this.chatSend = new WeakReference<Button>(chatSend);
        this.typingNotification = new WeakReference<TextView>(typingNotification);
        isTechnicianTyping = new AtomicBoolean(false);
        technicianTypingAppearanceTimer = new CountDownTimer(Config.TYPING_NOTIFICATION_VISIBILITY_DURATION, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // do nothing
            }

            @Override
            public void onFinish() {
                if (ChatEventHandler.this.typingNotification.get() != null) {
                    ChatEventHandler.this.typingNotification.get().setVisibility(View.INVISIBLE);
                }
                isTechnicianTyping.set(false);
            }
        };

        if (this.chatSend.get() != null) {
            this.chatSend.get().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chatClient != null && ChatEventHandler.this.chatInput.get() != null) {
                        final String message = ChatEventHandler.this.chatInput.get().getText().toString();
                        if (!TextUtils.isEmpty(message)) {
                            chatClient.sendMessage(message);
                            ChatEventHandler.this.chatInput.get().setText("");
                        }
                    }
                }
            });
            this.chatSend.get().setEnabled(false);
        }

        if (this.chatInput.get() != null) {
            this.chatInput.get().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (chatClient != null) {
                        chatClient.sendTyping();
                    }
                    if (ChatEventHandler.this.chatSend.get() != null) {
                        ChatEventHandler.this.chatSend.get().setEnabled(s != null && s.toString().trim().length() > 0);
                    }
                }
            });
            this.chatInput.get().setEnabled(false);
        }
    }

    @Subscribe
    public void onChatConnectedEvent(ChatConnectedEvent event) {
        chatClient = event.getChatClient();
        if (chatInput.get() != null) {
            chatInput.get().setEnabled(true);
        }
    }

    @Subscribe
    public void onChatDisconnectedEvent(ChatDisconnectedEvent event) {
        chatClient = null;
        if (chatInput.get() != null) {
            chatInput.get().setEnabled(false);
        }
    }

    @Subscribe
    public void onTechnicianTypingEvent(TechnicianTypingEvent event) {
        if (!isTechnicianTyping.getAndSet(true) && typingNotification.get() != null) {
            typingNotification.get().setVisibility(View.VISIBLE);
        } else {
            technicianTypingAppearanceTimer.cancel();
        }
        technicianTypingAppearanceTimer.start();
    }
}
