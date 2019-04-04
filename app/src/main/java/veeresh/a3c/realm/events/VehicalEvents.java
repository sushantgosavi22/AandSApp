package veeresh.a3c.realm.events;

import veeresh.a3c.realm.models.InventoryItem;

/**
 * Created by Veeresh on 3/11/17.
 */

public class VehicalEvents {


    public static class RecordEvent {
        InventoryItem record;
        int actionId;


        public int getActionId() {
            return actionId;
        }

        public RecordEvent(InventoryItem record, int actionId){
            this.record = record;
            this.actionId = actionId;

        }

        public InventoryItem getRecord() {
            return record;
        }
    }
}
