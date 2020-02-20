package com.vjd.movie.drivedetecttestproject.adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vjd.movie.drivedetecttestproject.R;
import com.vjd.movie.drivedetecttestproject.entity.LogEntity;

import java.util.Date;

public class LogsAdapter extends PagedListAdapter<LogEntity, LogsAdapter.LogEntityHolder> {

    private static DiffUtil.ItemCallback<LogEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<LogEntity>() {
                // LogEntity details may have changed if reloaded from the database, but ID is fixed.
                @Override
                public boolean areItemsTheSame(
                        LogEntity oldLogEntity,
                        LogEntity newLogEntity) {
                    return oldLogEntity.getId()
                            == newLogEntity.getId();
                }

                @Override
                public boolean areContentsTheSame(
                        LogEntity oldLogEntity,
                        @NonNull LogEntity newLogEntity) {
                    return oldLogEntity.equals(newLogEntity);
                }
            };

    public LogsAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public LogEntityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_log, parent, false);
        return new LogEntityHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull LogEntityHolder holder,
                                 int position) {
        if (position <= -1) {
            return;
        }
        LogEntity logEntity = getItem(position);

        try {
            if (logEntity != null) {
                holder.logId.setText(String.valueOf(logEntity.getId()));
                holder.activityType.setText(logEntity.getState());
                holder.confienceLevel.setText(logEntity.getCconfidence());
                CharSequence format = DateFormat.format("yyyy-MM-dd hh:mm:ss", logEntity.getDateAdded());
                holder.activityDate.setText(format);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class LogEntityHolder extends RecyclerView.ViewHolder {

        private TextView activityType, activityDate, confienceLevel, logId;

        LogEntityHolder(View itemView) {
            super(itemView);
            activityType = itemView.findViewById(R.id.tv_activity_type);
            activityDate = itemView.findViewById(R.id.tv_date);
            confienceLevel = itemView.findViewById(R.id.tv_confidence_percentage);
            logId = itemView.findViewById(R.id.tv_log_id);
        }

    }
}
