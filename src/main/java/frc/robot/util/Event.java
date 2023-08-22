package frc.robot.util;

import java.util.ArrayList;

public class Event {
    private ArrayList<Runnable> subscribedActions = new ArrayList<>();
    private ArrayList<Runnable> modifiedActions = null;

    public void invoke() {
        modifiedActions = new ArrayList<>(subscribedActions);
        subscribedActions.forEach((Runnable action) -> action.run());
        subscribedActions = modifiedActions;
        modifiedActions = null;
    }

    public boolean subscribe(Runnable action) {
        var currentList = (modifiedActions == null ? subscribedActions : modifiedActions);
        if(currentList.contains(action)) {
            return false;
        }
        currentList.add(action);
        return true;
    }

    public boolean unsubscribe(Runnable action) {
        var currentList = (modifiedActions == null ? subscribedActions : modifiedActions);
        if(!currentList.contains(action)) {
            return false;
        }
        currentList.remove(action);
        return true;
    }
}
