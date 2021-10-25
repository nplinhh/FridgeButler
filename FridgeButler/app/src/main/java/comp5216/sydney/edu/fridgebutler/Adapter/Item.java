package comp5216.sydney.edu.fridgebutler.Adapter;

public class Item {
    private String name;
    private String expiryDate;
    private String docRef;


    public Item(String name, String expiryDate, String docRef) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.docRef = docRef;

    }

    public Item(String name, String docRef) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.docRef = docRef;

    }

    public String getName() {
        return name;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getDocRef(){return docRef;}

}
