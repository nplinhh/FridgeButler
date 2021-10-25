package comp5216.sydney.edu.fridgebutler.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import comp5216.sydney.edu.fridgebutler.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;
    public CustomInfoWindowAdapter(Context context) {
        this.context = context.getApplicationContext();
    }


    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =  inflater.inflate(R.layout.custom_info_window, null);

        String titleString = marker.getTitle();
        String infoString = marker.getSnippet();

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView info = (TextView) view.findViewById(R.id.info);

        title.setText(titleString);
        info.setText(infoString);

        return view;
    }
}
