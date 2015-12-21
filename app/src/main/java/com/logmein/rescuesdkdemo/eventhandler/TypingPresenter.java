package com.logmein.rescuesdkdemo.eventhandler;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.logmein.rescuesdk.api.chat.event.TechnicianTypingEvent;
import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdkdemo.config.Config;
import com.logmein.rescuesdkresources.StringResolver;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manipulates the typing notification view based on the related events
 */
public class TypingPresenter {

    private static final long COUNTDOWN_INTERVAL = 1000L;

    private final CountDownTimer technicianTypingAppearanceTimer;
    private TextView typingStatus;
    private AtomicBoolean isTechnicianTyping;
    private StringResolver stringResolver;

    public TypingPresenter(TextView typingStatus, StringResolver stringResolver) {
        this.typingStatus = typingStatus;
        this.stringResolver = stringResolver;

        isTechnicianTyping = new AtomicBoolean(false);
        technicianTypingAppearanceTimer = new CountDownTimer(Config.TYPING_NOTIFICATION_VISIBILITY_DURATION, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // do nothing
            }

            @Override
            public void onFinish() {
                TypingPresenter.this.typingStatus.setVisibility(View.INVISIBLE);
                isTechnicianTyping.set(false);
            }
        };
    }

    @Subscribe
    public void onTechnicianTyping(TechnicianTypingEvent event) {
        if (!isTechnicianTyping.getAndSet(true)) {
            typingStatus.setVisibility(View.VISIBLE);
            typingStatus.setText(stringResolver.resolve(event));
        } else {
            technicianTypingAppearanceTimer.cancel();
        }
        technicianTypingAppearanceTimer.start();
    }
}
