package com.github.museadmin.infinite_state_machine.action;

import org.json.JSONObject;


/**
 * Parent Action class implements the methods common to all Actions.
 * Namely:
 * Checking if the action is enabled
 * Retrieving the payload from the database
 * Activating and Deactivating an action, including itself
 * Adding an action's states to the database
 */
public class Action {

    public JSONObject getPayload() {
        // TODO get the payload from the db
        return payload;
    }

    JSONObject payload;

}
