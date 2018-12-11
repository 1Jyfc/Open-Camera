package net.sourceforge.opencamera.test;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageButton;

import net.sourceforge.opencamera.MainActivity;
import net.sourceforge.opencamera.NewFunction.FavoriteActivity;
import net.sourceforge.opencamera.R;

public class TestActivity extends ActivityInstrumentationTestCase2<MainActivity> {

    private Context ctx;
    private MainActivity mainActivity;

    public TestActivity() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = getActivity().getApplicationContext();
        mainActivity = getActivity();
    }

    public void testClick() {
        final ImageButton imageButton = (ImageButton)getActivity().findViewById(R.id.exposure_lock);
        SystemClock.sleep(2000);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageButton.performClick();
            }
        });
        SystemClock.sleep(2000);
    }
}
