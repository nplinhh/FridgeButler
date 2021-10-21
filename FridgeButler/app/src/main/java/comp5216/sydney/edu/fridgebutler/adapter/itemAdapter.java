package comp5216.sydney.edu.fridgebutler.adapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;


import comp5216.sydney.edu.fridgebutler.R;

public class itemAdapter extends ArrayAdapter < Item >  {
    ArrayList<Item> listItem;
    public itemAdapter(@NonNull Context context, ArrayList<Item> arrayList) {
        super(context, 0, arrayList);
        listItem = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentItemView = convertView;

        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_listview, parent, false);
        }
        Item currentPosition = getItem(position);

        TextView textView1 = currentItemView.findViewById(R.id.textView1);
        textView1.setText(currentPosition.getName());

        TextView textView2 = currentItemView.findViewById(R.id.textView2);
        textView2.setText(currentPosition.getTime());
        if (currentPosition.getTime().equals("OVERDUE")) {
            textView2.setTextColor(Color.RED);
        }
        if (currentPosition.getTime().charAt(0) == '1') {
            textView2.setTextColor(Color.BLUE);
        }

        return currentItemView;
    }

    public ArrayList<Item> getListItem(){
        return listItem;
    }


}