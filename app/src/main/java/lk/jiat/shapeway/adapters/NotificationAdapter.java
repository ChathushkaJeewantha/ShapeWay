package lk.jiat.shapeway.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.util.List;

import lk.jiat.shapeway.R;
import lk.jiat.shapeway.models.NotificationModel;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private final List<NotificationModel> notificationList;

    public NotificationAdapter(Context context, List<NotificationModel> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel item = notificationList.get(position);

        holder.txtTitle.setText(item.getTitle());
        holder.txtMessage.setText(item.getMessage());
        holder.txtTopic.setText(item.getTopic() == null ? "general" : item.getTopic());
        holder.txtTime.setText(formatTime(item.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    private String formatTime(Object timestampObj) {
        if (timestampObj instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) timestampObj;
            return DateUtils.getRelativeTimeSpanString(
                    timestamp.toDate().getTime(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
            ).toString();
        }
        return "";
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtMessage, txtTopic, txtTime;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtNotificationTitle);
            txtMessage = itemView.findViewById(R.id.txtNotificationMessage);
            txtTopic = itemView.findViewById(R.id.txtNotificationTopic);
            txtTime = itemView.findViewById(R.id.txtNotificationTime);
        }
    }
}