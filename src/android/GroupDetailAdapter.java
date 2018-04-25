package com.chatcamp.plugin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.chatcamp.sdk.Participant;

import android.content.res.Resources;

/**
 * Created by shubhamdhabhai on 21/02/18.
 */

public class GroupDetailAdapter extends RecyclerView.Adapter {

    private static final int VIEW_ADD_PARTICIPANT = 0;
    private static final int VIEW_EXIT_GROUP = 1;
    private static final int VIEW_PARTICIPANT = 2;
    private static final int VIEW_PARTICIPANT_COUNT = 3;
    private final Context context;
    private Resources resources;
    private String package_name;

    private List<ParticipantView> participants;

    public interface OnParticipantClickedListener {
        void onAddParticipantClicked();

        void onParticipantClicked(Participant participant);

        void onExitGroupClicked();
    }

    public void setParticipantClickedListener(OnParticipantClickedListener participantClickedListener) {
        this.participantClickedListener = participantClickedListener;
    }

    private OnParticipantClickedListener participantClickedListener;

    public GroupDetailAdapter(Context context) {
        participants = new ArrayList<ParticipantView>();
        participants.add(0, new ParticipantView(null));
        participants.add(new ParticipantView(null));
        participants.add(new ParticipantView(null));
        this.context = context;
        package_name = context.getPackageName();
        resources = context.getResources();
    }

    public void add(ParticipantView participant) {
        participants.add(participants.size() - 1, participant);
        notifyDataSetChanged();
    }

    public void addAll(List<ParticipantView> participantViews) {
        participants.addAll(participants.size() - 1, participantViews);
        notifyDataSetChanged();
    }

    public void clear() {
        participants.clear();
        participants.add(0, new ParticipantView(null));
        participants.add(new ParticipantView(null));
        participants.add(new ParticipantView(null));
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_PARTICIPANT:
                return new ParticipantViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(resources.getIdentifier("layout_participant_view", "layout", package_name), parent, false));
            case VIEW_PARTICIPANT_COUNT:
                return new ParticipantCountViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(resources.getIdentifier("layout_participant_count", "layout", package_name), parent, false));
            case VIEW_ADD_PARTICIPANT:
                return new AddParticipantViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(resources.getIdentifier("layout_add_participant", "layout", package_name), parent, false));
            case VIEW_EXIT_GROUP:
                return new ExitGroupViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(resources.getIdentifier("layout_exit_participant", "layout", package_name), parent, false));
            default:
                return new ParticipantViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(resources.getIdentifier("layout_participant_view", "layout", package_name), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_PARTICIPANT:
                ((ParticipantViewHolder) holder).bind(participants.get(position));
                break;
            case VIEW_PARTICIPANT_COUNT:
                ((ParticipantCountViewHolder) holder).bind();
                break;
            case VIEW_ADD_PARTICIPANT:
                ((AddParticipantViewHolder) holder).bind();
                break;
            case VIEW_EXIT_GROUP:
                ((ExitGroupViewHolder) holder).bind();

        }
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_PARTICIPANT_COUNT;
        } else if (position == 1) {
            return VIEW_ADD_PARTICIPANT;
        } else if (position == participants.size() - 1) {
            return VIEW_EXIT_GROUP;
        } else {
            return VIEW_PARTICIPANT;
        }
    }

    class ParticipantCountViewHolder extends RecyclerView.ViewHolder {

        TextView participantCountTv;

        public ParticipantCountViewHolder(View itemView) {
            super(itemView);
            participantCountTv = itemView.findViewById(resources.getIdentifier("tv_participant_count", "id", package_name));
        }

        public void bind() {
            participantCountTv.setText(String.format("%d Participants", participants.size() - 3));
        }
    }

    class AddParticipantViewHolder extends RecyclerView.ViewHolder {

        public AddParticipantViewHolder(View itemView) {
            super(itemView);
        }

        public void bind() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (participantClickedListener != null) {
                        participantClickedListener.onAddParticipantClicked();
                    }
                }
            });
        }
    }

    class ExitGroupViewHolder extends RecyclerView.ViewHolder {

        public ExitGroupViewHolder(View itemView) {
            super(itemView);
        }

        public void bind() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (participantClickedListener != null) {
                        participantClickedListener.onExitGroupClicked();
                    }
                }
            });
        }
    }

    class ParticipantViewHolder extends RecyclerView.ViewHolder {

        ImageView participantIv;

        TextView participantTv;

        ImageView onlineIv;

        TextView lastSeenTv;

        public ParticipantViewHolder(View itemView) {
            super(itemView);
            participantIv = itemView.findViewById(resources.getIdentifier("iv_participant_image", "id", package_name));
            participantTv = itemView.findViewById(resources.getIdentifier("tv_participant_name", "id", package_name));
            onlineIv = itemView.findViewById(resources.getIdentifier("iv_online", "id", package_name));
            lastSeenTv = itemView.findViewById(resources.getIdentifier("tv_last_seen", "id", package_name));
        }

        public void bind(final ParticipantView participantView) {
            Picasso.with(context).load(participantView.getParticipant().getAvatarUrl())
                    .placeholder(resources.getIdentifier("icon_default_contact", "drawable", package_name))
                    .error(resources.getIdentifier("icon_default_contact", "drawable", package_name))
                    .into(participantIv);
            participantTv.setText(participantView.getParticipant().getDisplayName());
            if(participantView.getParticipant().isOnline()) {
                onlineIv.setVisibility(View.VISIBLE);
                lastSeenTv.setVisibility(View.GONE);
            } else {
                onlineIv.setVisibility(View.GONE);
                lastSeenTv.setVisibility(View.VISIBLE);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(participantView.getParticipant().getLastSeen() * 1000);
                lastSeenTv.setText(format.format(date));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (participantClickedListener != null) {
                        participantClickedListener.onParticipantClicked(participantView.getParticipant());
                    }
                }
            });
        }
    }
}
