package co.tomac.datapuppy;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.tomac.datapuppy.devicemonitor.DeviceMonitor;
import co.tomac.datapuppy.devicemonitor.db.Event;
import co.tomac.datapuppy.devicemonitor.db.EventRepository;

public class NotificationsFragment extends Fragment {

    static final String FRAGMENT_TAG = "NOTIFICATION_FRAGMENT";

    @BindView(R.id.notificationsRecyclerView)
    RecyclerView notificationsRecyclerView;

    @BindView(R.id.logRecyclerView)
    RecyclerView logRecyclerView;

    @OnClick(R.id.trashBinIcon)
    public void onClearLog(View view) {
        EventRepository eventRepo = DeviceMonitor.getEventRepository();
        if(eventRepo == null) {
            Toast.makeText(getActivity(),
                    "Unable to delete log. DB Unavailable", Toast.LENGTH_LONG).show();
            return;
        }
        eventRepo.deleteEventsWithType("alarm");
    }

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationsFragment.
     */
    public static NotificationsFragment newInstance(String param1, String param2) {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, view);

        notificationsRecyclerView.setHasFixedSize(true);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        NotificationsAdapter adapter = new NotificationsAdapter();
        notificationsRecyclerView.setAdapter(adapter);

        logRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        EventRepository eventRepo = DeviceMonitor.getEventRepository();
        if(eventRepo != null) {
            LiveData<List<Event>> alarmEvents = eventRepo.getEventsForType("alarm");

            LogAdapter logAdapter = new LogAdapter((MainActivity) this.getActivity(), alarmEvents);
            logRecyclerView.setAdapter(logAdapter);
        }

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
