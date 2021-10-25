package comp5216.sydney.edu.fridgebutler.Recipe;




import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Ingredient implements Serializable {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM YYYY");

    private String name;
    private String addedDate;
    private String expiryDate;
    private String category;
    private String Uuid;
    private String image;
    private String amount;
    private String unit;

    public Ingredient() {
    }

    public Ingredient(String IngredientName, String category, Date expDate) {
        this.addedDate = new Date().toString();
        this.name = IngredientName;
        this.category = category;
        this.expiryDate = DATE_FORMAT.format(expDate);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }


    public void setImage(String image) {
        this.image = image;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}