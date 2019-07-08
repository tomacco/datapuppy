package co.tomac.datapuppy;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.tomac.datapuppy.devicemonitor.DeviceMonitor;
import co.tomac.datapuppy.devicemonitor.DeviceMonitorListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StateFragment extends Fragment implements DeviceMonitorListener {

    public static final String FRAGMENT_TAG = "STATE_FRAGMENT";
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.cpuLabel)
    TextView cpuLabel;

    @BindView(R.id.ramLabel)
    TextView ramLabel;

    @BindView(R.id.batteryStats)
    TextView batteryLabel;


    public StateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StateFragment.
     */
    public static StateFragment newInstance() {
        return new StateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_state, container, false);
        ButterKnife.bind(this, view);

        DeviceMonitor.registerListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DeviceMonitor.registerListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        DeviceMonitor.unregisterListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCpuSampling(int cpuUsage) {
        runOnUiThread(() -> {
            cpuLabel.setText(cpuUsage + "%");
        });
    }

    @Override
    public void onRamSampling(double ramUsage) {
        runOnUiThread(() -> {
            ramLabel.setText(String.format(Locale.getDefault(), "%.2f%%", ramUsage));
        });
    }

    @Override
    public void onBatterySampling(int batteryRemaining) {
        runOnUiThread(() -> {
            batteryLabel.setText(batteryRemaining + "%");
        });
    }

    private void runOnUiThread(Runnable runnable) {
        Activity activity = getActivity();
        if(activity == null) {
            return;
        }
        activity.runOnUiThread(runnable);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String param);
    }
}
