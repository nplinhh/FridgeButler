package comp5216.sydney.edu.fridgebutler.adapter;

import android.media.Image;

import java.io.Serializable;

public class Item {
    private String itemName;
    private String time;
    private String docRef;
    private String image;

    public Item(String name, String time, String docRef) {
        this.itemName = name;
        this.time = time;
        this.docRef = docRef;

    }

    public void setImage(String image){ this.image = image;}

    public String getName() {
        return itemName;
    }

    public String getTime() {
        return time;
    }

    public String getDocRef(){return docRef;}

    public String getImage() {
        return image;
    }
}
