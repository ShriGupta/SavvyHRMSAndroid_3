package com.savvy.hrmsnewapp.activity;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by orapc7 on 6/2/2017.
 */

public class Text_To_Speech {

    public TextToSpeech tts;
    public void speech(Context context, final String message)
    {
        try {
            tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        //int result = tts.setLanguage(new Locale("en", "IN"));
                        int result = tts.setLanguage(new Locale("en","IN"));
                        tts.setPitch((float) 1.0); //ranges from 0.0 to 2.0
                        tts.setSpeechRate((float) 1.0);  //ranges from 0.0 to 2.0

                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS", "Language is not supported");
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    } else {
                        Log.e("Error", "Status not OK");
                    }
                }
            });
        }catch (Exception e){
            //tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            e.printStackTrace();
        }
    }
}
