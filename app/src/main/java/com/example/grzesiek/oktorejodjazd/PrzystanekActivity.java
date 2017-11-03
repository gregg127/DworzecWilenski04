package com.example.grzesiek.oktorejodjazd;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import java.util.Calendar;

public class PrzystanekActivity extends AppCompatActivity {
    private CheckedTextView output;
    private Button button;
    private EditText mEdit;
    private ProgressBar progressBar;
    private String site;
    private String sourceCode;
    private boolean isSourceCodeDownloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.przystanek_lay);
        addComponents();
        downloadForPresentHour();
    }

    private void addComponents(){
        site = getIntent().getStringExtra("site");
        mEdit = (EditText)findViewById(R.id.timeTextField);
        output = (CheckedTextView)findViewById(R.id.output);
        output.setMovementMethod(new ScrollingMovementMethod());
        button = (Button) findViewById(R.id.checkSchedule);
        button.setOnClickListener( v ->
                new BusStopsSearchTask().execute(sourceCode, mEdit.getText().toString(), site)
        );
        progressBar = (ProgressBar) findViewById(R.id.przystanekProgressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void downloadForPresentHour(){
        String hour = Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        mEdit.setText(hour);
        new BusStopsSearchTask().execute(sourceCode, hour, site);
    }

    private void downloading(boolean b){
        if(b){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            button.setEnabled(false);
            output.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            progressBar.setIndeterminate(false);
            button.setEnabled(true);
            output.setVisibility(View.VISIBLE);
        }
    }

    // garbage collector?
    private class BusStopsSearchTask extends AsyncTask<String, Void, String>{
        boolean isSourceCodeReady;
        @Override
        protected void onPreExecute(){
            isSourceCodeReady = isSourceCodeDownloaded; // whether set progress bar running or not
            if(!isSourceCodeReady)
                downloading(true);
        }

        @Override
        protected String doInBackground(String... strings) {
            String src = strings[0]; // 0 - source code
            String hour = strings[1]; // 1 - hour
            String site = strings[2]; // 2 - site
            if (src == null || src.length() < 200) { // not downloaded yet
                src = RozkladJazdy.getSourceCode(site);
                if (src.length() < 200) { // exception
                    if (src.contains("SocketTimeoutException")) {
                        return "Przekroczono dopuszczalny czas pobierania strony.\n" +
                                "Upewnij się, że masz działający internet, " +
                                "i spróbuj ponownie.\n(" + src + ")";
                    }
                    return "Wystąpił błąd przy ładowaniu strony.\n" +
                            "Włącz internet i spróbuj ponownie.\n(" + src + ")";
                } else { // successfully downloaded
                    sourceCode = src;
                    isSourceCodeDownloaded = true;
                    return getRozkladJazdy(hour, src);
                }
            } else { // source code ready
                return getRozkladJazdy(hour, src);
            }
        }

        @Override
        protected  void onPostExecute(String s) {
            if(!isSourceCodeReady)
                downloading(false);
            output.setText(s);
        }

        private String getRozkladJazdy(String hour, String source){
            try {
                int h = Integer.parseInt(hour);
                if (h < 0 || h > 23) {
                    return "Podano złą godzinę";
                } else {
                    return RozkladJazdy.getRozkladJazdy(source, h);
                }
            } catch (NumberFormatException ex) {
                return "Nic nie zostało wpisane. \nWpisz godzinę.";
            }
        }
    }
}