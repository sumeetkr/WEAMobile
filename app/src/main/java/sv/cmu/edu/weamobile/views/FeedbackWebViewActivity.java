package sv.cmu.edu.weamobile.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import sv.cmu.edu.weamobile.R;
import sv.cmu.edu.weamobile.data.Message;
import sv.cmu.edu.weamobile.utility.AlertHelper;
import sv.cmu.edu.weamobile.utility.Constants;
import sv.cmu.edu.weamobile.utility.Logger;


public class FeedbackWebViewActivity extends Activity {

    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_web_view);

        int alertId = getIntent().getIntExtra(Constants.ALERT_ID, -1);
        if (alertId != -1 ) {
            message = AlertHelper.getMessageFromId(getApplicationContext(), String.valueOf(alertId));
            if(message != null){
                WebView wv = (WebView) this.findViewById(R.id.webView);
                wv.addJavascriptInterface(new WebAppInterface(this), "Android");
                wv.getSettings().setJavaScriptEnabled(true);
                wv.setWebViewClient(new MyBrowser());

                String url = AlertHelper.getFeedbackURL(getApplicationContext(), message);
                wv.loadUrl(url);
                Logger.log(url);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feedback_web_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            try{
                Toast.makeText(mContext, Constants.THANKS_FOR_FEEDBACK, Toast.LENGTH_SHORT).show();
                AlertHelper.sendFeedbackGivenToServer(getApplicationContext(), message);

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setAction(Constants.SHOW_MAIN_VIEW_ACTION);
                startActivity(intent);

                Logger.log("Submitted the feedback form");
            }catch(Exception ex){
                Logger.log(ex.getMessage());
            }
        }
    }
}
