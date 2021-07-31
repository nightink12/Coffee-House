package com.example.coffeehouse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DisplayList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);

        String message = getIntent().getStringExtra("key");
        TextView cartTextView = (TextView) findViewById(R.id.cart_text_view);
        cartTextView.setText(message);
    }
}