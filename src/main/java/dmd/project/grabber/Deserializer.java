package dmd.project.grabber;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dmd.project.grabber.YandexGrabber.Category;
import dmd.project.objects.Item;
import dmd.project.objects.Review;

public class Deserializer {
    
    public static void main(String[] args) {
        dCommonItems();
    }
    
    /**
     * Yandex grabber. Categories
     */
    public static void dCategories() {
        Map<String, Category> objs = null;
        try {
            FileInputStream fis = new FileInputStream("GrabberYandex/categories.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            objs = (Map<String, Category>) ois.readObject();
            
            StringBuilder sb = new StringBuilder();
            for (Entry<String, Category> entry : objs.entrySet()) {
                System.out.println(entry.getKey());
                printCat(sb, "", entry.getValue());
            }
            
            /**
            BufferedWriter wr = new BufferedWriter(
                    new FileWriter("categories.txt"));
            wr.write(sb.toString());
            wr.flush();
            wr.close();
            */
            
            //System.out.println(sb.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void printCat(
            StringBuilder sb, String ident, Category cat) {
        sb.append(ident + cat.getName() + " - " + 
            cat.getId() + System.getProperty("line.separator"));
        for (Entry<String, Category> entry : 
                   cat.getSubCategories().entrySet()) {
            printCat(sb, ident + "\t", entry.getValue());
        }
    }
    
    
    /**
     * Ozon grabber
     */
    public static void dObjects() {
        Map<String, Item> objs = null;
        try {
            
            FileInputStream fis = new FileInputStream("GrabberOzon/objects.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            objs = (Map<String, Item>) ois.readObject();
            
            System.out.println("Amount of items: " + objs.size());
            for (Entry<String, Item> entry : objs.entrySet()) {
                Item obj = entry.getValue();
                //System.out.println(obj);
                //System.out.println(entry.getKey());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Ozon grabber
     */
    public static void dCommonItems() {
        List<Item> objs = null;
        try {
            
            FileInputStream fis = new FileInputStream("common_items.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            objs = (List<Item>) ois.readObject();
            
            System.out.println("Amount of items: " + objs.size());
            for (Item obj : objs) {
                System.out.println(obj);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}