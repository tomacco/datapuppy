package co.tomac.datapuppy.devicemonitor;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;

public interface DeviceMonitorListener {

    void onCpuSampling(@IntRange(from=-1, to=100) int cpuUsage);

    void onRamSampling(@FloatRange(from=-1, to=100) double ramUsage);

    void onBatterySampling(@IntRange(from=-1, to=100) int batteryRemaining);
}
