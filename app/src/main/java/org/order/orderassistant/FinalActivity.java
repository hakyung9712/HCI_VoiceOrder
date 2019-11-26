package org.order.orderassistant;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class FinalActivity extends AppCompatActivity {
    TextToSpeech tts;
    Intent intent;
    SpeechRecognizer mRecognizer;
    Button sttBtn;
    TextView textView;
    final int PERMISSION = 1;
    String first,menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent2=getIntent();
        first=intent2.getExtras().getString("first");
        menu=intent2.getExtras().getString("menu");
        Log.d("success",first);
        Log.d("success",menu);

        textView = (TextView) findViewById(R.id.first_sttResult);
        sttBtn = (Button) findViewById(R.id.first_sttStart);

        //tts 객체 생성하고 OnInitListener로 초기화 함
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                    //String을 text로 바꾸는게 안됨
                    tts.speak(first, TextToSpeech.QUEUE_FLUSH, null);
                    tts.speak(menu, TextToSpeech.QUEUE_FLUSH, null);
                    tts.speak("주문하셨습니다.", TextToSpeech.QUEUE_FLUSH, null);
                    /*
                    if(first=="매장"){
                        tts.speak("매장에서", TextToSpeech.QUEUE_FLUSH, null);
                        tts.speak(menu, TextToSpeech.QUEUE_FLUSH, null);
                        tts.speak("주문하셨습니다.", TextToSpeech.QUEUE_FLUSH, null);

                    }else if(first=="포장"){
                        tts.speak("매장에서", TextToSpeech.QUEUE_FLUSH, null);
                        tts.speak(menu, TextToSpeech.QUEUE_FLUSH, null);
                        tts.speak("주문하셨습니다.", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else{
                        tts.speak("주문이 잘못되었습니다.",TextToSpeech.QUEUE_FLUSH,null);
                    }

                     */

                }
            }
        });


        //음성인식 부분
        if (Build.VERSION.SDK_INT >= 23) {
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        //사용자에게 음성을 요구하고 음성인식기를 통해 전송 시작
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //음성 인식기의 의도에 사용되는 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        //음성 번역 언어 설정
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        //음성 인식하는 부분
        sttBtn.setOnClickListener(v -> {
            //새 SpeechRecognizer 만들기
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            //모든 콜백 수신하는 리스너 설정.
            mRecognizer.setRecognitionListener(listener);
            //듣기 시작
            mRecognizer.startListening(intent);
        });
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            //말하기 시작할 준비가 되면 호출
            Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
        }
        //말하기 시작했을 때 호출

        @Override
        public void onRmsChanged(float rmsdB) {
        }
        //입력받는 소리의 크기 알려줌

        @Override
        public void onBufferReceived(byte[] buffer) {
        }
        //사용자의 말 중 인식이 된 단어를 buffer에 담음

        @Override
        public void onEndOfSpeech() {
        }
        //말하기 중지하면 호출

        //네트워크 또는 인식 오류 발생 시 호출
        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        //인식 결과가 준비되면 호출
        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            //for문으로 음성인식된 결과를 setText로 설정
            for (int i = 0; i < matches.size(); i++) {
                textView.setText(matches.get(i));
            }
            Toast.makeText(getApplicationContext(), matches.toString(), Toast.LENGTH_LONG).show();

            String txt1 = "매장";
            String txt2 = "포장";
            if (matches.toString().contains(txt1)) {
                Toast.makeText(getApplicationContext(), "매장 주문", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                intent.putExtra("first", "매장");
                startActivity(intent);
                finish();
            } else if (matches.toString().contains(txt2)) {
                Toast.makeText(getApplicationContext(), "포장 주문", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                intent.putExtra("first", "포장");
                startActivity(intent);
                finish();
            } else {
                tts.speak("한번 더 말해주세요.", TextToSpeech.QUEUE_FLUSH, null);
            }


        }

        //부분 인식 결과 사용할 수 있을 때 호출
        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        //향후 이벤트 추가
        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}


