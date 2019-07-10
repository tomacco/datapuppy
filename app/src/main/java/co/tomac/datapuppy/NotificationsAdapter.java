package co.tomac.datapuppy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> implements ServiceConnection {


    private Context context;
    private boolean notificationServiceBound;
    private AlarmNotificationService notificationService;
    private Map<MetricType, Integer> notifications;
    private boolean isLoading = true;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification, parent, false);
        context = parent.getContext();
        startAndBindService();
        return new ViewHolder(view);
    }

    private void loadConfiguredNotifications() {
        if (notificationService == null) {
            Toast.makeText(context,
                    "Unable to load configured notifications. Service not bound",
                    Toast.LENGTH_LONG).show();
            return;
        }
        notifications = notificationService.getConfiguredNotifications();
    }

    private void startAndBindService() {
        Intent intent = new Intent(context, AlarmNotificationService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

        context.bindService(intent, NotificationsAdapter.this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) {
            setupHolder(holder, MetricType.CPU, R.drawable.cpu, R.string.cpu_notification_description);
        }
        if (position == 1) {
            setupHolder(holder, MetricType.RAM, R.drawable.analytics, R.string.ram_notification_description);
        }
        if (position == 2) {
            setupHolder(holder, MetricType.BATTERY, R.drawable.battery, R.string.battery_notification_description);
        }
    }

    private void setupHolder(@NonNull ViewHolder holder, MetricType type, int icon, int message) {
        isLoading = true;
        holder.setType(type);
        Drawable cpuDrawable = ResourcesCompat.getDrawable(context.getResources(), icon, null);
        holder.notificationImage.setImageDrawable(cpuDrawable);
        holder.thresholdDescription.setText(context.getResources().getString(message));
        loadMetric(holder, type);
        isLoading = false;

    }


    private void loadMetric(ViewHolder holder, MetricType type) {
        if (notifications == null) {
            return;
        }
        Integer metric = notifications.get(type);
        if (metric != null) {
            holder.isEnabled = true;
            holder.toggle.setChecked(true);
            holder.seekBar.setEnabled(false);
            holder.seekBar.setProgress(metric);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        AlarmNotificationService.NotificationBinder binder =
                (AlarmNotificationService.NotificationBinder) iBinder;
        notificationService = binder.getService();
        notificationServiceBound = true;
        loadConfiguredNotifications();
        this.notifyDataSetChanged();

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        notificationService = null;
        notificationServiceBound = false;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        boolean isEnabled = false;

        @BindView(R.id.imageNotificationSettings)
        ImageView notificationImage;

        @BindView(R.id.seekBarNotifications)
        SeekBar seekBar;

        @BindView(R.id.thresholdNotifications)
        TextView threshold;

        @BindView(R.id.descriptionNotificationSettings)
        TextView thresholdDescription;

        @BindView(R.id.switchNotifications)
        Switch toggle;

        private MetricType type;

        @OnCheckedChanged(R.id.switchNotifications)
        void onNotificationToggle(Switch toggle) {
            if(isLoading) {
                return;
            }
            isEnabled = !isEnabled;
            if (!notificationServiceBound) {
                Toast.makeText(context, "Notification service not bound!!", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            int threshold = seekBar.getProgress();
            seekBar.setEnabled(!seekBar.isEnabled());
            if (!isEnabled) {
                threshold = -1; //means the user has deactivated the notification
            }
            notificationService.alertResourceUsage(type, threshold);
        }

        ViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    threshold.setText(i + "%");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        public void setType(MetricType type) {
            this.type = type;
        }

    }
}
