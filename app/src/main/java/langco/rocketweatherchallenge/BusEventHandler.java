package langco.rocketweatherchallenge;

import java.util.ArrayList;


public class BusEventHandler {
    private ArrayList<String> mParameterArray;

    //These bus events can probably be refactored into one or two events TODO
    public BusEventHandler (ArrayList<String> passedParameters) {
        mParameterArray=passedParameters;
    }

    public ArrayList<String> getParameter() {

        return mParameterArray;
    }
}
