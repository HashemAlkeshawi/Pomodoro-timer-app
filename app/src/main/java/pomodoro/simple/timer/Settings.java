package pomodoro.simple.timer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

        private NumberPicker mNumberPicker1, mNumberPicker2, mNumberPicker3, mNumberPicker4, mNumberPicker5, mNumberPicker6;

        // values for the number pickers
        private int v1 , v2, v3, v4, v5, v6;

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        Button save;

        Context context;

        Switch mSwitch;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.settings_layout);


                mNumberPicker1 =  findViewById(R.id.numberPicker_1);
                mNumberPicker2 =  findViewById(R.id.numberPicker_2);
                mNumberPicker3 =  findViewById(R.id.numberPicker_3);
                mNumberPicker4 =  findViewById(R.id.numberPicker_4);
                mNumberPicker5 =  findViewById(R.id.numberPicker_5);
                mNumberPicker6 =  findViewById(R.id.numberPicker_6);

                save = findViewById(R.id.Save);

                mSwitch = findViewById(R.id.switchEdit);


                context = this;

                sharedPreferences = getSharedPreferences("settings", 0);
                editor = sharedPreferences.edit();

                mSwitch.setChecked(sharedPreferences.getBoolean("AutoStart", false));

                save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        saveSettings();

                        }
                });

                setNumberPickers(mNumberPicker1,mNumberPicker2,mNumberPicker3,mNumberPicker4,mNumberPicker5,mNumberPicker6);





        }

        public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.settings_menu, menu);
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                        case R.id.Settings_save:
                           saveSettings();
                }
                return super.onOptionsItemSelected(item);
        }

        private void saveSettings(){

                editor.putInt("pomodoroPeriod", getPomodoroPeriod(mNumberPicker1,mNumberPicker2));

                editor.putInt("BreakPeriod", getBreakPeriod(mNumberPicker3,mNumberPicker4));

                editor.putInt("LongBreakPeriod", getLongBreakPeriod(mNumberPicker5,mNumberPicker6));
                editor.commit();

                editor.putBoolean("AutoStart", mSwitch.isChecked());
                editor.commit();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

        }


        private void setNumberPickers(NumberPicker n1,NumberPicker n2, NumberPicker n3, NumberPicker n4, NumberPicker n5, NumberPicker n6){

                int pomodoroPeriod = sharedPreferences.getInt("pomodoroPeriod",1500);
                n1.setMaxValue(60);
                n1.setMinValue(0);
                n1.setValue(pomodoroPeriod/60);

                n2.setMaxValue(60);
                n2.setMinValue(0);
                n2.setValue(pomodoroPeriod);


                int BreakPeriod = sharedPreferences.getInt("BreakPeriod",300);
                n3.setMaxValue(60);
                n3.setMinValue(0);
                n3.setValue(BreakPeriod/60);

                n4.setMaxValue(60);
                n4.setMinValue(0);
                n4.setValue(BreakPeriod);


                int LongBreakPeriod = sharedPreferences.getInt("LongBreakPeriod",900);
                n5.setMaxValue(60);
                n5.setMinValue(0);
                n5.setValue(LongBreakPeriod/60);

                n6.setMaxValue(60);
                n6.setMinValue(0);
                n6.setValue(LongBreakPeriod);

        }
        private int getPomodoroPeriod(NumberPicker n1,NumberPicker n2){

                v1 = n1.getValue();
                v2 = n2.getValue();

                int period = (v1 * 60) + v2;

                if(period > 4){
                        return period;
                }else {
                        return 5;
                }

        }

        private int getBreakPeriod(NumberPicker n1, NumberPicker n2){

                v3 = n1.getValue();
                v4 = n2.getValue();

                int period = (v3 * 60) + v4;

                if(period > 4){
                        return period;
                }else {
                        return 5;
                }
        }

        private int getLongBreakPeriod(NumberPicker n1, NumberPicker n2){

                v5 = n1.getValue();
                v6 = n2.getValue();

                int period = (v5 * 60) + v6;

                if(period > 9){
                        return period;
                }else {
                        return 10;
                }
        }
}
