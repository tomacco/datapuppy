package co.tomac.datapuppy;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleRegistry;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        replaceFragment(R.id.container, new StateFragment(), StateFragment.FRAGMENT_TAG);
                        return true;
                    case R.id.navigation_notifications:
                        replaceFragment(R.id.container, new NotificationsFragment(), NotificationsFragment.FRAGMENT_TAG);
                        return true;
                }
                return false;
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        replaceFragment(R.id.container, new StateFragment(), StateFragment.FRAGMENT_TAG);
        checkAndroidVersionAndShowWarningsIfAny();
    }

    private void checkAndroidVersionAndShowWarningsIfAny() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        AlertDialog dialog =  new AlertDialog.Builder(this)
                .setTitle("WARNING")
                .setMessage(getString(R.string.androidVersionCPUAlert)).setPositiveButton("OK", null)
                .create();

        dialog.show();
    }


    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .commit();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        //NOP
    }
}
