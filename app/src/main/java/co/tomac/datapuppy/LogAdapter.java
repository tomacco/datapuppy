package co.tomac.datapuppy;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.tomac.datapuppy.devicemonitor.db.Event;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
    private  LiveData<List<Event>> alarmEvents;
    private List<Event> events;

    public LogAdapter(MainActivity parent, LiveData<List<Event>> alarmEvents) {
        this.alarmEvents = alarmEvents;
        this.alarmEvents.observe(parent, events -> {
            LogAdapter.this.events = events;
            LogAdapter.this.notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_historic, parent, false);

        return new LogAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        Date createdAt = event.getCreatedAt();
        String message = event.getValue();
        SimpleDateFormat dateFormatter =
                new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        holder.createdAt.setText(dateFormatter.format(createdAt));
        holder.message.setText(message);
    }

    @Override
    public int getItemCount() {
        List<Event> list = alarmEvents.getValue();
        if(list == null) {
            return 0;
        }
        return alarmEvents.getValue().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.logMessage)
        TextView message;

        @BindView(R.id.logTime)
        TextView createdAt;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
