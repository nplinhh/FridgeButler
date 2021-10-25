package comp5216.sydney.edu.fridgebutler.adapter;

import java.util.ArrayList;

import comp5216.sydney.edu.fridgebutler.adapter.Item;

public interface DataCallBack {
    void onComplete(ArrayList<Item> item);
}
