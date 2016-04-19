package langco.rocketweatherchallenge;

import android.app.Application;

import com.squareup.otto.Bus;

//General class to allow one time instigation of shared information
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static final Bus bus = new Bus();
}

