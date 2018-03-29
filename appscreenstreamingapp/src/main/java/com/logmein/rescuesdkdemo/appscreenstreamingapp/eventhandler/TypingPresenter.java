package com.logmein.rescuesdkdemo.appscreenstreamingapp.eventhandler;

import android.view.View;
import android.widget.TextView;

import com.logmein.rescuesdk.api.chat.event.TechnicianTypingEvent;
import com.logmein.rescuesdk.api.chat.event.TechnicianTypingStoppedEvent;
import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdkresources.StringResolver;

/**
 * Manipulates the typing notification view based on the related events
 */
public class TypingPresenter {

    private TextView typingStatus;
    private StringResolver stringResolver;

    public TypingPresenter(TextView typingStatus, StringResolver stringResolver) {
        this.typingStatus = typingStatus;
        this.stringResolver = stringResolver;
    }

    @Subscribe
    public void onTechnicianTypingStopped(TechnicianTypingStoppedEvent event) {
        typingStatus.setVisibility(View.INVISIBLE);
    }

    @Subscribe
    public void onTechnicianTyping(TechnicianTypingEvent event) {
        typingStatus.setVisibility(View.VISIBLE);
        typingStatus.setText(stringResolver.resolve(event));
    }
}
