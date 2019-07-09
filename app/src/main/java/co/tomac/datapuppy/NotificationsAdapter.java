package co.tomac.datapuppy;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder>{


    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == 0) {
            holder.setType(MetricType.CPU);
            Drawable cpuDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.cpu, null);
            holder.notificationImage.setImageDrawable(cpuDrawable);
            holder.thresholdDescription.setText(context.getResources().getString(R.string.cpu_notification_description));
        }
        if(position == 1) {
            holder.setType(MetricType.RAM);
            Drawable ramDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.analytics, null);
            holder.notificationImage.setImageDrawable(ramDrawable);
            holder.thresholdDescription.setText(context.getResources().getString(R.string.ram_notification_description));
        }
        if(position == 0) {
            holder.setType(MetricType.BATTERY);
            Drawable batteryDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.battery, null);
            holder.notificationImage.setImageDrawable(batteryDrawable);
            holder.thresholdDescription.setText(context.getResources().getString(R.string.battery_notification_description));
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;

        @BindView(R.id.imageNotificationSettings)
        ImageView notificationImage;

        @BindView(R.id.seekBarNotifications)
        SeekBar seekBar;

        @BindView(R.id.thresholdNotifications)
        TextView threshold;
        
        @BindView(R.id.descriptionNotificationSettings)
        TextView thresholdDescription;

        private MetricType type;

        @OnCheckedChanged(R.id.switchNotifications)
        public void onNotificationToggle(Switch toggle) {
            Intent intent  = new Intent(context, NotificationService.class);
            intent.putExtra(NotificationService.METRIC_TYPE, type.toString());

            int threshold = seekBar.getProgress();
            if(!toggle.isEnabled()) {
                threshold = -1; //means the user has deactivated the notification
            }
            intent.putExtra(NotificationService.THRESHOLD, threshold);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }

        ViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }

        public void setType(MetricType type) {
            this.type = type;
        }

    }
}
