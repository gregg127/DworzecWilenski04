package com.example.grzesiek.dworzecwilenski04;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;

public class Wilenski01 extends AppCompatActivity {
    final private String site = "http://www.ztm.waw.pl/rozklad_nowy.php?c=182&l=1&n=1003&o=01";
    private Przystanek przystanek;
    private CheckedTextView output;
    private Button button;
    private EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.przystanek_lay);
        addComponents();
        przystanek.startDownloading();
    }
    private void addComponents(){
        przystanek = new Przystanek(site);
        mEdit = (EditText)findViewById(R.id.timeTextField);
        output = (CheckedTextView)findViewById(R.id.output);
        output.setMovementMethod(new ScrollingMovementMethod());
        button = (Button) findViewById(R.id.checkSchedule);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText(przystanek.getRozkladJazdy(mEdit.getText().toString()));
            }
        });
    }
}