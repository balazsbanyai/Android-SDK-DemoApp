package com.logmein.rescuesdkdemo.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.logmein.rescuesdk.api.chat.event.LocalChatMessageEvent;
import com.logmein.rescuesdk.api.chat.event.RemoteChatMessageEvent;
import com.logmein.rescuesdk.api.chat.event.UrlMessageEvent;
import com.logmein.rescuesdk.api.event.Event;
import com.logmein.rescuesdk.api.eventbus.Subscribe;
import com.logmein.rescuesdk.api.remoteview.event.RemoteViewEvent;
import com.logmein.rescuesdk.api.session.event.ConnectionEvent;
import com.logmein.rescuesdkdemo.R;
import com.logmein.rescuesdkresources.StringResolver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Simple adapter to display event logs and chat messages in a ListView.
 */
public class ChatLogAdapter extends BaseAdapter {

    private static final String LOG_OWN_CHAT_MESSAGE = "%s: %s";
    private static final String TIMESTAMP_FORMAT = "[%s]";
    private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

    private final Context context;
    private StringResolver stringResolver;
    private final List<ChatMessageHolder> messages = new ArrayList<ChatMessageHolder>();

    public ChatLogAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ChatMessageHolder holder = messages.get(position);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.chat_list_item, null);
        }

        TextView timeView = (TextView) view.findViewById(R.id.time);
        TextView messageView = (TextView) view.findViewById(R.id.message);
        timeView.setText(formatTime(holder.time));
        messageView.setText(holder.message);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());

        view.setEnabled(false);

        return view;
    }

    public void setStringResolver(StringResolver stringResolver) {
        this.stringResolver = stringResolver;
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    @Subscribe
    public void onRemoteChatMessageEvent(RemoteChatMessageEvent event) {
        String resolvedMessage = stringResolver.resolve(event);
        ChatMessageHolder messageHolder = new ChatMessageHolder(resolvedMessage, event.getTime());
        addChatMessage(messageHolder);
    }

    @Subscribe
    public void onLocalChatMessageEvent(LocalChatMessageEvent event) {
        String resolvedMessage = stringResolver.resolve(event);
        ChatMessageHolder messageHolder = new ChatMessageHolder(resolvedMessage, event.getTime());
        addChatMessage(messageHolder);
    }

    @Subscribe
    public void onConnectionEvent(ConnectionEvent event) {
        addSystemMessage(event);
    }

    @Subscribe
    public void onRemoteViewEvent(final RemoteViewEvent event) {
        addSystemMessage(event);
    }

    @Subscribe
    public void onUrlMessageEvent(UrlMessageEvent event) {
        addSystemMessage(event);
    }

    private void addSystemMessage(Event event) {
        final String resolvedMessage = stringResolver.resolve(event);
        final long time = System.currentTimeMillis();

        if (resolvedMessage != null) {
            ChatMessageHolder message = new ChatMessageHolder(resolvedMessage, time);
            addChatMessage(message);
        }
    }

    private void addChatMessage(final ChatMessageHolder message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                messages.add(message);
                notifyDataSetChanged();
            }
        });
    }

    private String formatTime(long time) {
        Date date = new Date(time);
        String formattedDate = TIME_FORMAT.format(date);
        return String.format(TIMESTAMP_FORMAT, formattedDate);
    }

    private static class ChatMessageHolder {
        long time;
        CharSequence message;

        ChatMessageHolder(CharSequence message, long time) {
            this.message = message;
            this.time = time;
        }
    }
}
