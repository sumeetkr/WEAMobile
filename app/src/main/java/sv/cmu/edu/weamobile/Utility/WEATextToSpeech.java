package sv.cmu.edu.weamobile.utility;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by sumeet on 11/6/14.
 */
public class WEATextToSpeech{

    TextToSpeech tts;
    Context context;


    public WEATextToSpeech(Context context) {
        this.context = context;

    }

    public void shutdown() {
        if(tts != null) tts.shutdown();
    }

    public void say(String messageToSay, Integer times) {
        if(tts == null){
            tts = new TextToSpeech(context, new TTSListener(messageToSay , times));
        }else{
            tts.shutdown();
            tts = new TextToSpeech(context, new TTSListener(messageToSay , times));
        }
    }

    protected class TTSListener implements TextToSpeech.OnInitListener {

        String what_to_speak = null;
        Integer how_many_times = null;

        public TTSListener(String to_speak, Integer times) {
            what_to_speak = to_speak;
            how_many_times = times;
        }

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.speak(what_to_speak, TextToSpeech.QUEUE_FLUSH, null);
                    for (Integer n = 1; n < how_many_times; n++) {
                        tts.speak(what_to_speak, TextToSpeech.QUEUE_ADD, null);
                    }
                }
            }
        }
    }
}
