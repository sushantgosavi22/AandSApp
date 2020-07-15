package com.aandssoftware.aandsinventory.database;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

/**
 * This class provides a simplified interface to the Firebase remote database system.
 * Methods require the context such as an {@code Activity}, so you
 * must call {@code SimpleFirebase.with(yourActivity)} first.
 */
public final class










FirebaseHelper {
    /**
     * An event listener that can respond to database errors.
     */
    public interface ErrorListener {
        public void onError(DatabaseError error);
    }

    /**
     * An event listener that can respond to the result of get() calls.
     */
    public interface GetListener {
        public void onGet(String path, DataSnapshot data);
    }

    /**
     * An event listener that can respond to the result of push() or pushById() calls.
     */
    public interface PushListener {
        public void onPush(String path, DatabaseReference ref);
    }

    /**
     * An event listener that can respond to the result of set() calls.
     */
    public interface SetListener {
        public void onSet(String path);
    }

    /**
     * An event listener that can respond to the result of signIn() calls.
     */
    public interface SignInListener {
        public void onSignIn(boolean successful);
    }

    /**
     * An event listener that can respond to the result of transaction() calls.
     */
    public interface TransactionListener {
        public void onTransaction(String path, MutableData mdata);
    }

    /**
     * An event listener that can respond to the result of watch() calls.
     */
    public interface WatchListener {
        public void onDataChange(DataSnapshot data);
    }

    // tag for debug logging
    private static final String LOG_TAG = "SimpleFirebase";

    // whether the Firebase db has been initialized
    private static boolean sInitialized = false;

    private Context context;                         // activity/fragment used to load resources
    private ErrorListener errorListener;             // responds to database errors (null if none)
    private DatabaseError lastDatabaseError = null;  // last database error that occurred (null if none)
    private String lastQueryPath = null;             // last string/Query from get()/etc.
    private Query lastQuery = null;
    private boolean signInComplete = false;          // true if finished signing in to db
    private boolean logging = false;                 // true if we should Log various debug messages
    private boolean keepSynced = false;              // true if we want to sync local database with server

    /**
     * Returns a new SimpleFirebase instance using the given activity or other context.
     */
    public static FirebaseHelper getInstance(Context context) {
        FirebaseHelper fb = new FirebaseHelper();
        fb.context = context;
        fb.setKeepSynced(true);
        fb.setLogging(true);
        if (!sInitialized) {
            synchronized (FirebaseHelper.class) {
                if (!sInitialized) {
                    FirebaseApp.initializeApp(context);
                    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                    sInitialized = true;
                }
            }
        }
        return fb;
    }

    /**
     * Returns a child of the overall database; equivalent to Firebase's child() method
     * or the SimpleFirebase query() method.
     *
     * @see FirebaseHelper#query(String)
     */
    public DatabaseReference child(String queryText) {
        return query(queryText);
    }

    /**
     * Clears this object's record of any past database error.
     * If there was no past error, has no effect.
     */
    public void clearLastDatabaseError() {
        lastDatabaseError = null;
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the GetListener interface, it will be notified when the data arrives.
     *
     * @param path absolute database path such as "foo/bar/baz"
     */
    public FirebaseHelper get(String path) {
        return get(path, /* listener */ null);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * The given GetListener will be notified when the data has arrived.
     *
     * @param path     absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the data has arrived
     */
    public FirebaseHelper get(String path, final GetListener listener) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        fb.keepSynced(keepSynced);
        DatabaseReference child = fb.child(path);
        return getWatchHelper(path, child, listener, /* watch */ false);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the GetListener interface, it will be notified when the data arrives.
     *
     * @param ref a Query object containing an absolute database reference
     */
    public FirebaseHelper get(Query ref) {
        return get(ref, /* listener */ null);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * The given GetListener will be notified when the data has arrived.
     *
     * @param ref      a Query object containing an absolute database reference
     * @param listener object to notify when the data has arrived
     */
    public FirebaseHelper get(final Query ref, final GetListener listener) {
        return getWatchHelper(/* path */ null, ref, listener, /* watch */ false);
    }

    // common helper code for all overloads of get() and watch()
    private FirebaseHelper getWatchHelper(String path, Query ref, GetListener listener, boolean watch) {
        if (ref == null) {
            return this;
        } else if (path == null) {
            if (ref == lastQuery) {
                path = lastQueryPath;
            } else {
                lastQuery = ref;
                lastQueryPath = null;
                path = ref.toString();
            }
        }
        if (logging) {
            Log.d(LOG_TAG, "get/watch: path=" + path);
        }

        // listen to the data coming back
        if (listener == null && context instanceof GetListener) {
            listener = (GetListener) context;
        }
        InnerValueEventListener valueListener = new InnerValueEventListener();
        valueListener.path = path;
        valueListener.getListener = listener;

        // either listen once (get) or keep listening (watch)
        if (watch) {
            ref.addValueEventListener(valueListener);
        } else {
            ref.addListenerForSingleValueEvent(valueListener);
        }
        return this;
    }


    /*
     * Helper function to check for database errors and call listeners as needed.
     * Returns true if there was an error, false if not.
     */
    private boolean handleDatabaseError(DatabaseError error) {
        if (error != null) {
            lastDatabaseError = error;
            if (errorListener != null) {
                errorListener.onError(error);
            } else if (context instanceof ErrorListener) {
                ((ErrorListener) context).onError(error);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if there has been a database error that has not been cleared.
     */
    public boolean hasLastDatabaseError() {
        return lastDatabaseError != null;
    }

    /**
     * Returns true if a user is currently signed in.
     */
    public boolean isSignedIn() {
        return signInComplete;
    }

    /**
     * Returns the last database error that occurred, or null if no error has occurred.
     */
    public DatabaseError lastDatabaseError() {
        return lastDatabaseError;
    }

    /**
     * Adds a new object with a randomly-generated unique string key at the given path in the database,
     * and returns that newly pushed object.
     *
     * @param path absolute database path such as "foo/bar/baz"
     */
    public DatabaseReference push(String path) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        fb.keepSynced(keepSynced);
        return fb.child(path).push();
    }

    /**
     * Adds a new object with a new unique integer ID key at the given path in the database.
     * This call will query the given path to find the currently largest child ID, and set the newly
     * added child to have an ID that is +1 higher than that largest child ID.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the PushListener interface, it will be notified when the new object's key is found
     * and the new object has been created.
     *
     * @param path absolute database path such as "foo/bar/baz"
     */
    public FirebaseHelper pushById(String path) {
        return pushById(path, /* listener */ null);
    }

    /**
     * Adds a new object with a new unique integer ID key at the given path in the database.
     * The object will be stored with the given initial value.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the PushListener interface, it will be notified when the new object's key is found
     * and the new object has been created.
     *
     * @param path  absolute database path such as "foo/bar/baz"
     * @param value value to store at this path
     */
    public FirebaseHelper pushById(String path, Object value) {
        return pushById(path, value, /* listener */ null);
    }

    /**
     * Adds a new object with a new unique integer ID key at the given path in the database.
     * The object will be temporarily given a value of boolean 'false'.
     * The given PushListener will be notified when the data has been created.
     *
     * @param path     absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the data has arrived
     */
    public FirebaseHelper pushById(String path, PushListener listener) {
        return pushById(path, /* value */ false, listener);
    }

    /**
     * Adds a new object with a new unique integer ID key at the given path in the database.
     * The object will be stored with the given initial value.
     * The given PushListener will be notified when the data has been created.
     *
     * @param path     absolute database path such as "foo/bar/baz"
     * @param value    value to store at this path
     * @param listener object to notify when the data has arrived
     */
    public FirebaseHelper pushById(String path, Object value, PushListener listener) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        fb.keepSynced(keepSynced);
        DatabaseReference child = fb.child(path);
        if (logging) {
            Log.d(LOG_TAG, "push: path=" + path + ", value=" + value);
        }

        if (listener == null && context instanceof PushListener) {
            listener = (PushListener) context;
        }

        // query to get largest current ID (may need to repeat)
        pushById_getMaxId(path, value, child, listener);

        return this;
    }

    /*
     * Helper that queries the db to find the max numeric ID in given area.
     * Once found, tries to start a transaction to add a new child with next available ID.
     */
    private void pushById_getMaxId(final String path, final Object value,
                                   final DatabaseReference child, final PushListener listener) {
        Query query = child.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                long key = 0;
                if (!data.hasChildren()) {
                    // this will be the first child
                    key = 0;
                    pushById_addNewChild(path, value, child, key, listener);
                } else {
                    DataSnapshot lastChild = data.getChildren().iterator().next();
                    String keyStr = lastChild.getKey();
                    try {
                        key = Long.parseLong(keyStr) + 1;   // increment to next key
                        pushById_addNewChild(path, value, child, key, listener);
                    } catch (NumberFormatException nfe) {
                        // empty
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleDatabaseError(error);
            }
        });
    }

    /*
     * Starts a transaction to add a new child with the given ID.
     * If the ID is taken by the time we get the transaction lock, retries
     * by querying again to get the next available ID.
     */
    private void pushById_addNewChild(final String path, final Object value,
                                      final DatabaseReference ref, final long idKey, final PushListener listener) {
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mdata) {
                // add the new child
                Log.d("SimpleFirebase", "doTransaction: mdata=" + mdata);
                if (mdata.hasChild(String.valueOf(idKey))) {
                    // oops; somebody already claimed this ID; retry!
                    pushById_getMaxId(path, value, ref, listener);
                    return Transaction.abort();
                } else {
                    MutableData newChild = mdata.child(String.valueOf(idKey));
                    newChild.setValue(value);
                    return Transaction.success(mdata);
                }
            }

            @Override
            public void onComplete(DatabaseError error, boolean completed, DataSnapshot data) {
                Log.d("SimpleFirebase", "transaction onComplete: error=" + error + ", completed=" + completed + ", data=" + data);
                if (!handleDatabaseError(error) && completed && listener != null) {
                    String childPath = path + (path.endsWith("/") ? "" : "/") + idKey;
                    DatabaseReference childRef = ref.child(String.valueOf(idKey));
                    listener.onPush(childPath, childRef);
                }
            }
        });
    }

    /**
     * Performs a query on the Firebase database.
     * Similar to the Firebase child() method.
     * Common intended usage:
     *
     * <pre>
     * SimpleFirebase fb = SimpleFirebase.with(this);
     * fb.get(fb.query("foo/bar/baz")
     *     .orderByChild("quux")
     *     .limitToFirst(1));
     * </pre>
     *
     * @param queryText absolute path in database such as "foo/bar/baz"
     */
    public DatabaseReference query(String queryText) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        fb.keepSynced(keepSynced);
        DatabaseReference query = fb.child(queryText);
        this.lastQuery = query;
        this.lastQueryPath = queryText;
        return query;
    }

    /**
     * Sets the given location in the database to store the given value.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the SetListener interface, it will be notified when the data has been synced.
     *
     * @param path  an absolute database path such as "foo/bar/baz"
     * @param value the value to store there in the database
     */
    public FirebaseHelper set(String path, Object value) {
        return setHelper(path, /* key */ "", value, /* listener */ null);
    }

    /**
     * Sets the given location in the database to store the given value.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the SetListener interface, it will be notified when the data has been synced.
     *
     * @param path  an absolute database path such as "foo/bar/baz"
     * @param key   child key name within that path, such as "quux" to indicate "foo/bar/baz/quux"
     * @param value the value to store there in the database
     */
    public FirebaseHelper set(String path, String key, Object value) {
        return setHelper(path, key, value, /* listener */ null);
    }

    /**
     * Sets the given location in the database to store the given value.
     * The given SetListener will be notified when the data has been synced.
     *
     * @param path  an absolute database path such as "foo/bar/baz"
     * @param value the value to store there in the database
     */
    public FirebaseHelper set(String path, Object value, SetListener listener) {
        return setHelper(path, /* key */ "", value, /* listener */ listener);
    }

    /**
     * Sets the given location in the database to store the given value.
     * The given SetListener will be notified when the data has been synced.
     *
     * @param path  an absolute database path such as "foo/bar/baz"
     * @param key   child key name within that path, such as "quux" to indicate "foo/bar/baz/quux"
     * @param value the value to store there in the database
     */
    public FirebaseHelper set(String path, String key, Object value, SetListener listener) {
        return setHelper(path, key, value, /* listener */ listener);
    }

    // helper for common set() code
    private FirebaseHelper setHelper(String path, String key, Object value, SetListener listener) {
        if (listener == null && context instanceof SetListener) {
            listener = (SetListener) context;
        }

        if (logging) {
            Log.d(LOG_TAG, "set: path=" + path + ", key=" + key + ", value=" + value);
        }

        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        fb.keepSynced(keepSynced);
        DatabaseReference child = null;
        if (key == null || key.isEmpty()) {
            child = fb.child(path);
        } else {
            if (!path.endsWith("/")) {
                path += "/";
            }
            path += key;
            child = fb.child(path);
        }
        if (child == null) {
            return this;
        }

        if (listener != null) {
            InnerCompletionListener myListener = new InnerCompletionListener();
            myListener.path = path;
            myListener.set = listener;
            child.setValue(value, myListener);
        } else {
            child.setValue(value);
        }
        return this;
    }

    /**
     * Sets the given listener object to be notified of future database errors.
     * Pass null to disable listening for database errors.
     * If the context passed to with() implements ErrorListener, it will be automatically
     * notified of database errors even if you don't call setErrorListener.
     */
    public FirebaseHelper setErrorListener(ErrorListener listener) {
        this.errorListener = listener;
        return this;
    }

    /**
     * Sets whether the SimpleFirebase library should print log messages for debugging.
     */
    public FirebaseHelper setLogging(boolean logging) {
        this.logging = logging;
        if (errorListener == null) {
            // set up a default error logging listener if there is none
            errorListener = new InnerErrorListener();
        }
        return this;
    }


    /**
     * Initiates a request to perform a transaction on the data at the given path in the database.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the TransactionListener interface, it will be notified when the mutable data has arrived.
     *
     * @param path absolute database path such as "foo/bar/baz"
     */
    public FirebaseHelper transaction(String path) {
        return transaction(path, /* listener */ null);
    }

    /**
     * Initiates a request to perform a transaction on the data at the given path in the database.
     * The given TransactionListener will be notified when the mutable data has arrived.
     *
     * @param path     absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the mutable data has arrived
     */
    public FirebaseHelper transaction(String path, final TransactionListener listener) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        fb.keepSynced(keepSynced);
        DatabaseReference child = fb.child(path);

        // query to get largest current ID
        InnerTransactionHandler handler = new InnerTransactionHandler();
        handler.path = path;
        if (listener != null) {
            handler.listener = listener;
        } else if (context instanceof TransactionListener) {
            handler.listener = (TransactionListener) context;
        }
        child.runTransaction(handler);

        return this;
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     *
     * @param path absolute database path such as "foo/bar/baz"
     */
    public FirebaseHelper watch(String path) {
        return watch(path, /* listener */ null);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     *
     * @param path     absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the data has arrived
     */
    public FirebaseHelper watch(String path, final GetListener listener) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        fb.keepSynced(keepSynced);
        DatabaseReference child = fb.child(path);
        this.lastQuery = child;
        this.lastQueryPath = path;
        return getWatchHelper(path, child, listener, /* watch */ true);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the GetListener interface, it will be notified when the data arrives.
     *
     * @param ref a Query object representing an absolute database path
     */
    public FirebaseHelper watch(Query ref) {
        return watch(ref, /* listener */ null);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     *
     * @param ref      a Query object representing an absolute database path
     * @param listener object to notify when the data has arrived
     */
    public FirebaseHelper watch(final Query ref, final GetListener listener) {
        return getWatchHelper(/* path */ null, ref, listener, /* watch */ true);
    }


    /*
     * Helper class that listens for database task completion results; used by set().
     */
    private class InnerCompletionListener implements DatabaseReference.CompletionListener {
        private boolean complete = false;
        private DatabaseError error;
        private SetListener set;
        private String path;

        @Override
        public void onComplete(DatabaseError error, DatabaseReference ref) {
            complete = true;
            if (set != null) {
                set.onSet(path);
            }
            handleDatabaseError(error);
        }
    }

    /*
     * Helper class that listens for database errors and logs them to the Android Studio console.
     */
    private class InnerErrorListener implements ErrorListener {
        @Override
        public void onError(DatabaseError error) {
            Log.d(LOG_TAG, " *** DATABASE ERROR: " + error);
        }
    }

    private class InnerTransactionHandler implements Transaction.Handler {
        private String path;
        private TransactionListener listener;

        @Override
        public Transaction.Result doTransaction(MutableData mdata) {
            if (listener != null) {
                listener.onTransaction(path, mdata);
            }
            return Transaction.success(mdata);
        }

        @Override
        public void onComplete(DatabaseError error, boolean committed, DataSnapshot data) {
            handleDatabaseError(error);
        }
    }

    /*
     * Helper class that listens for data arrival results; used by get() and watch().
     */
    private class InnerValueEventListener implements ValueEventListener {
        private String path = null;
        private GetListener getListener;

        @Override
        public void onDataChange(DataSnapshot data) {
            if (getListener != null) {
                getListener.onGet(path, data);
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            handleDatabaseError(error);
        }
    }

    public void setKeepSynced(boolean keepSynced) {
        this.keepSynced = keepSynced;
    }
}
