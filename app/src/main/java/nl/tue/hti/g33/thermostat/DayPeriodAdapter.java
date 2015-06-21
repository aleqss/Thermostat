package nl.tue.hti.g33.thermostat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nl.tue.hti.g33.thermostat.utils.Period;

/**
 * Created by Alex on 21.06.2015.
 */
public class DayPeriodAdapter extends BaseAdapter {

    private ArrayList<Period> mDayPeriods;
    private LayoutInflater inflater;

    private class ViewHolder {

        private TextView startTime;
        private TextView endTime;
    }

    public DayPeriodAdapter(Context context, Iterable<Period> dayPeriods) {

        inflater = LayoutInflater.from(context);
        mDayPeriods = new ArrayList<>();
        for (Period p : dayPeriods) {
            mDayPeriods.add(p);
        }
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {

        return mDayPeriods.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Period getItem(int position) {

        return mDayPeriods.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {

        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_day_schedule, null);
            holder.startTime = (TextView) convertView.findViewById(R.id.startTime);
            holder.endTime = (TextView) convertView.findViewById(R.id.endTime);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.startTime.setText(mDayPeriods.get(position).getStartingTimeT().toString());
        holder.endTime.setText(mDayPeriods.get(position).getEndTimeT().toString());
        return convertView;
    }
}
