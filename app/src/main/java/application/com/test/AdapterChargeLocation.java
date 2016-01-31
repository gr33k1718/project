package application.com.test;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class AdapterChargeLocation extends ArrayAdapter<ChargeLocation> {


    public AdapterChargeLocation(Context context, int resource, List<ChargeLocation> locations) {
        super(context, resource, locations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.charge_location_list_item, null);
        }

        ChargeLocation p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.text);

            if (tt1 != null) {
                tt1.setText(p.toString());
            }
        }

        return v;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
