package co.tomac.datapuppy;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(PowerMockRunner.class)
public class AlarmNotificationServiceTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void onCpuSampling() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        //Test 1:
        // - alarm is being configured with 20%
        // - Previous CPU reading was NOT over threshold.
        // - New CPU reading is NOT over threshold
        // - Input is still below threshold = no alarm.
        AlarmNotificationService service = mock(AlarmNotificationService.class); //mock the service
        doCallRealMethod().when(service).onCpuSampling(anyInt()); //ensure real method is called

        Map<MetricType, Integer> configMap = new HashMap<>(); //create the test1 config

        configMap.put(MetricType.CPU, 20); // inject the config
        Whitebox.setInternalState(service, "activeNotificationsConfig", configMap);

        service.onCpuSampling(10); //exercise the code
        verify(service, times(0)).showNotificationForMetric(anyString(),
                any(MetricType.class)); //check that the test was successful


        //Test 2:
        // - alarm is being configured with 20%
        // - Previous CPU reading was NOT over threshold.
        // - New CPU reading IS over threshold
        // - Input is ABOVE threshold -> alarm should trigger -> showNotificationForMetric called once
        service = mock(AlarmNotificationService.class); //mock the service
        doCallRealMethod().when(service).onCpuSampling(anyInt()); //ensure real method is called

        configMap = new HashMap<>(); //create the test 2 config

        configMap.put(MetricType.CPU, 20); // inject the config
        Whitebox.setInternalState(service, "activeNotificationsConfig", configMap);

        service.onCpuSampling(21); //exercise the code
        verify(service, times(1)).showNotificationForMetric(anyString(),
                any(MetricType.class)); //check that the test was successful

        //Test 3:
        // - alarm is being configured with 20%
        // - Previous CPU reading WAS over threshold.
        // - New CPU reading IS ALSO over threshold
        // - Alarm should be already triggered. Do NOT trigger it again -> showNotificationForMetric not called

        service = mock(AlarmNotificationService.class); //mock the service
        doCallRealMethod().when(service).onCpuSampling(anyInt()); //ensure real method is called

        configMap = new HashMap<>(); //create the test 2 config

        configMap.put(MetricType.CPU, 20); // inject the config
        Whitebox.setInternalState(service, "activeNotificationsConfig", configMap);

        //Configure cpuOverThreshold -> true
        Whitebox.setInternalState(service, "cpuOverThreshold", true);

        service.onCpuSampling(21); //exercise the code
        verify(service, times(0)).showNotificationForMetric(anyString(),
                any(MetricType.class)); //check that the test was successful

        //Test 4:
        // - alarm is being configured with 20%
        // - Previous CPU reading WAS over threshold.
        // - New CPU reading IS NOT over threshold
        // - New Notification stating that the CPU is below threshold should be shown.
        // - cpuOverThreshold should go back to false

        service = mock(AlarmNotificationService.class); //mock the service
        doCallRealMethod().when(service).onCpuSampling(anyInt()); //ensure real method is called

        configMap = new HashMap<>(); //create the test 2 config

        configMap.put(MetricType.CPU, 20); // inject the config
        Whitebox.setInternalState(service, "activeNotificationsConfig", configMap);

        //Configure cpuOverThreshold -> true
        Whitebox.setInternalState(service, "cpuOverThreshold", true);

        service.onCpuSampling(13); //exercise the code
        verify(service, times(1)).showNotificationForMetric(anyString(),
                any(MetricType.class)); //check that the test was successful

        boolean cpuOverThreshold = Whitebox.getInternalState(service, "cpuOverThreshold");
        assertFalse(cpuOverThreshold);

    }

}