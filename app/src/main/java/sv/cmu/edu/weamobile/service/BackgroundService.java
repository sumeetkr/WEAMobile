package sv.cmu.edu.weamobile.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class BackgroundService extends IntentService {
    private static final String ACTION_FOO = "sv.cmu.edu.weamobile.service.action.FOO";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "sv.cmu.edu.weamobile.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "sv.cmu.edu.weamobile.service.extra.PARAM2";

    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BackgroundService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
