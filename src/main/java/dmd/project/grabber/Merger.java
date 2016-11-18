package dmd.project.grabber;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dmd.project.objects.Item;
import dmd.project.objects.Review;

public class Merger {
    
    public static boolean similiar(Item yaItem, Item ozItem) {
        if (yaItem.getName().equals(ozItem.getName())) {
            return true;
        }
        
        String[] yaName = (yaItem.getName())
                .replaceAll("[(|)|,|.|-]", "").toLowerCase().split(" ");
        
        int commaIndex = ozItem.getName().indexOf(",");
        String[] ozName = null;
        if (commaIndex > 0) {
            ozName = ozItem.getName().substring(0, commaIndex)
                    .replaceAll("[(|)|,|.|-]", "").toLowerCase().split(" ");
        } else {
            ozName = ozItem.getName()
                    .replaceAll("[(|)|,|.|-]", "").toLowerCase().split(" ");
        }
        
        
        // How much words in name are equal
        Arrays.sort(yaName);
        Arrays.sort(ozName);
        
        int counter = 0;
        for (int i = 0; i < yaName.length; i++) {
            if (Arrays.binarySearch(ozName, yaName[i]) >= 0) {
                counter++;
            }
        }
        if (counter == Math.min(yaName.length, ozName.length)) {
            return true;
        }
        
        return false;
    }
    
    
    public static Item merge(Item yandexItem, Item ozonItem) {
        Item item = new Item();
        item.setName(ozonItem.getName());
        item.setDescription(maxStr(
                ozonItem.getDescription(), yandexItem.getDescription()));
        item.setType(maxStr(ozonItem.getType(), yandexItem.getType()));
        item.addReviews(ozonItem.getReviews());
        item.addReviews(yandexItem.getReviews());
        item.addImages(ozonItem.getImages());
        item.addImages(yandexItem.getImages());
        
        item.setSourceUrl(ozonItem.getSourceUrl()); //!!!!!!!!!!!!!!!
        return item;
    }
    
    /**
     * Deserialize objects map
     */
    public static HashMap<String, Item> getObjects(String fileName) {
        System.out.println("Trying to read the file: " + fileName);
        HashMap<String, Item> objs = null;
        try {
            
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            objs = (HashMap<String, Item>) ois.readObject();
            ois.close();
            
            System.out.println(fileName + " - items: " + objs.size());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return objs;
    }
    
    
    public static Item createItem(String name, String vendor) {
        Item item = new Item();
        item.setName(name);
        item.setVendor(vendor);
        
        return item;
    }
    
    public static void test() {
        List<Item> yandexItems = new ArrayList<>();               
        yandexItems.add(createItem("Galaxy S7 32Gb", "Samsung"));
        yandexItems.add(createItem("Vivobook X556UQ", "ASUS"));
        yandexItems.add(createItem("JetFlash 790", "Transcend"));
        yandexItems.add(createItem("Бог как иллюзия", ""));
        yandexItems.add(createItem("Николай Гоголь \"Тарас Бульба\"", ""));
        yandexItems.add(createItem("Механический ангел", ""));
        
        
        List<Item> ozonItems = new ArrayList<>();
        ozonItems.add(createItem("Samsung SM-G930FD Galaxy S7 (32GB), Black", ""));
        ozonItems.add(createItem("Asus VivoBook E202SA, Dark Blue (90NL0052-M00700)", ""));
        ozonItems.add(createItem("Transcend JetFlash 790 32GB USB 3.0, Black USB-накопитель", ""));
        ozonItems.add(createItem("Бог как иллюзия", ""));
        
        
        
        for (Item yandexItem : yandexItems) {
            for (Item ozonItem : ozonItems) {
                if (similiar(yandexItem, ozonItem)) {
                    System.out.println("These items are considered SIMILIAR:");
                    System.out.println("Yandex: " + yandexItem);
                    System.out.println("Ozon: " + ozonItem);
                    System.out.println();
                }
            }
        }
    }
    
    
    
    protected static String maxStr(String str1, String str2){
        if (str1 == null || str1.isEmpty()) {
            return str2;
        }
        if (str2 == null || str2.isEmpty()) {
            return str1;
        }
        if (str1.length() >= str2.length()) {
            return str1;
        } else {
            return str2;
        }
    }
    
    
    
    /**
     * Serialization
     * @param brokenLinks
     * @param objs
     */
    public static void serialize(final List<Item> items) {
        try {
            FileOutputStream fos = new FileOutputStream("common_items.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(items);
            oos.flush();
            oos.close();
            
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    
    
    public static void main(String[] args) {
        Collection<Item> ozonItems = 
                getObjects("GrabberOzon/objects.ser").values();
        Collection<Item> yandexItems = 
                getObjects("GrabberYandex/objects.ser").values();
        List<Item> common = new ArrayList<>();
        
        for (Item yandexItem : yandexItems) {
             for (Item ozonItem : ozonItems) {
                 if (similiar(yandexItem, ozonItem)) {
                     Item item = merge(yandexItem, ozonItem);
                     common.add(item);

                     System.out.println("These items are considered SIMILIAR:");
                     System.out.println("Yandex: " + yandexItem);
                     System.out.println("Ozon: " + ozonItem);
                     System.out.println();
                 }
             }
         }
        
        serialize(common);
        System.out.println("Similiar items found: " + common.size());
    }
}
