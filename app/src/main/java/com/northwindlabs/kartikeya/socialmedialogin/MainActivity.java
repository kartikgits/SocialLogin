package com.northwindlabs.kartikeya.socialmedialogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final Class[] CLASSES = new Class[]{
            GoogleActivity.class,
            FacebookActivity.class,
            TwitterActivity.class,
            LinkedInActivity.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.list_view);

        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_1, CLASSES);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class clicked = CLASSES[position];
        startActivity(new Intent(this, clicked));
    }

    private static class MyArrayAdapter extends ArrayAdapter<Class> {
        private Context mContext;
        private Class[] mClasses;

        private MyArrayAdapter(Context context, int resource, Class[] objects) {
            super(context, resource, objects);
            mContext = context;
            mClasses = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_list_item_1, null);
            }

            else if (mClasses[position].getSimpleName().equals("GoogleActivity")){
                ((TextView) view.findViewById(android.R.id.text1)).setText(R.string.google_login);
            }

            switch (mClasses[position].getSimpleName()){
                case "FacebookActivity":
                    ((TextView) view.findViewById(android.R.id.text1)).setText(R.string.facebook_login);
                    break;
                case "GoogleActivity":
                    ((TextView) view.findViewById(android.R.id.text1)).setText(R.string.google_login);
                    break;
                case "LinkedInActivity":
                    ((TextView) view.findViewById(android.R.id.text1)).setText(R.string.linkedin_login);
                    break;
                case "TwitterActivity":
                    ((TextView) view.findViewById(android.R.id.text1)).setText(R.string.twitter_login);
                    break;
            }

            return view;
        }
    }
}