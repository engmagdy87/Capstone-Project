package com.mm.plume;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {
    Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class destinationActivity = MainActivity.class;
                Intent intent = new Intent(getBaseContext(), destinationActivity);
//                Bundle extras = new Bundle();
//                extras.putString("searchKeyword",searchKeyword);
//                extras.putParcelableArrayList("booksData",booksData);
//                SearchResult.putExtras(extras);
                startActivity(intent);
            }
        });
    }
}
