package veeresh.a3c.realm.events;

import veeresh.a3c.realm.models.Task;

/**
 * Created by Veeresh on 3/11/17.
 */

public class TaskEvents {


    public static class RecordEvent {
        Task record;
        int actionId;


        public int getActionId() {
            return actionId;
        }

        public RecordEvent(Task record, int actionId){
            this.record = record;
            this.actionId = actionId;

        }

        public Task getRecord() {
            return record;
        }
    }
}
