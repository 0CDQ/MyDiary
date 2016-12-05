package com.kiminonawa.mydiary.main;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.contacts.ContactsActivity;
import com.kiminonawa.mydiary.entries.DiaryActivity;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.memo.MemoActivity;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.util.List;

/**
 * Created by daxia on 2016/10/17.
 */

public class MainTopicAdapter extends RecyclerView.Adapter<MainTopicAdapter.TopicViewHolder> {


    private List<ITopic> topicList;
    private MainActivity activity;

    public MainTopicAdapter(MainActivity activity, List<ITopic> topicList) {
        this.activity = activity;
        this.topicList = topicList;
    }


    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_topic_item, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, final int position) {

        holder.getRootView().setBackground(ThemeManager.getInstance().getTopicItemSelectDrawable(activity));
        holder.getIconView().setImageResource(topicList.get(position).getIcon());
        holder.getTitleView().setText(topicList.get(position).getTitle());
        holder.getTVCount().setText(String.valueOf(topicList.get(position).getCount()));
        holder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (topicList.get(position).getType()) {
                    case ITopic.TYPE_CONTACTS:
                        Intent goContactsPageIntent = new Intent(activity, ContactsActivity.class);
                        goContactsPageIntent.putExtra("topicId", topicList.get(position).getId());
                        goContactsPageIntent.putExtra("diaryTitle", topicList.get(position).getTitle());
                        activity.startActivity(goContactsPageIntent);
                        break;
                    case ITopic.TYPE_DIARY:
                        Intent goEntriesPageIntent = new Intent(activity, DiaryActivity.class);
                        goEntriesPageIntent.putExtra("topicId", topicList.get(position).getId());
                        goEntriesPageIntent.putExtra("diaryTitle", topicList.get(position).getTitle());
                        activity.startActivity(goEntriesPageIntent);
                        break;
                    case ITopic.TYPE_MEMO:
                        Intent goMemoPageIntent = new Intent(activity, MemoActivity.class);
                        goMemoPageIntent.putExtra("topicId", topicList.get(position).getId());
                        goMemoPageIntent.putExtra("diaryTitle", topicList.get(position).getTitle());
                        activity.startActivity(goMemoPageIntent);
                        break;
                }
            }
        });
        holder.getRootView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CreateTopicDialogFragment createTopicDialogFragment =
                        CreateTopicDialogFragment.newInstance(true, position, topicList.get(position).getTextColor());
                createTopicDialogFragment.setCallBack(activity);
                createTopicDialogFragment.show(activity.getSupportFragmentManager(),
                        "createTopicDialogFragment");
                return true;
            }
        });
    }


    protected class TopicViewHolder extends RecyclerView.ViewHolder {

        private ImageView IV_topic_icon;
        private TextView TV_topic_title;
        private TextView TV_topic_count;
        private View rootView;

        protected TopicViewHolder(View view) {
            super(view);
            this.rootView = view;
            this.IV_topic_icon = (ImageView) rootView.findViewById(R.id.IV_topic_icon);
            this.TV_topic_title = (TextView) rootView.findViewById(R.id.TV_topic_title);
            this.TV_topic_count = (TextView) rootView.findViewById(R.id.TV_topic_count);
        }

        protected ImageView getIconView() {
            return IV_topic_icon;
        }

        protected TextView getTitleView() {
            return TV_topic_title;
        }

        public TextView getTVCount() {
            return TV_topic_count;
        }

        protected View getRootView() {
            return rootView;
        }
    }
}
