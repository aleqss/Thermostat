package nl.tue.hti.g33.thermostat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import nl.tue.hti.g33.thermostat.utils.DAY;
import nl.tue.hti.g33.thermostat.utils.Thermostat;
import nl.tue.hti.g33.thermostat.utils.ThermostatProvider;


public class DetailDayActivity extends AppCompatActivity implements ThermostatProvider {

    private Thermostat mThermostat;
    private DAY mDay;
    private Toolbar mToolbar;

    private static final String LOG_TAG = "DetailDayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_day);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String day = bundle.getString("day");
        mDay = DAY.getByShortName(day);
        setTitle("Schedule " + mDay.getFullName());
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mThermostat = Thermostat.getInstance();
        DayScheduleFragment frag = DayScheduleFragment.newInstance(mDay);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_day_schedule, frag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_day, menu);
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

        if (id == R.id.action_add_switch) {

            AddRuleDialogFragment dialog = AddRuleDialogFragment.newInstance(null, mDay);
            dialog.show(getSupportFragmentManager(), "Add switch");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Thermostat provideThermostat() {

        return mThermostat;
    }
}
