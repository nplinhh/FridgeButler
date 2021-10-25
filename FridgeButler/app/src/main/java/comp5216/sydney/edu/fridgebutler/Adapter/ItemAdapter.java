package comp5216.sydney.edu.fridgebutler.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;


import comp5216.sydney.edu.fridgebutler.R;

public class ItemAdapter extends ArrayAdapter < Item >  {
    ArrayList<Item> listItem;
    public ItemAdapter(@NonNull Context context, ArrayList<Item> arrayList) {
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
        textView2.setText(currentPosition.getExpiryDate());

        return currentItemView;
    }

    public ArrayList<Item> getListItem(){
        return listItem;
    }


}