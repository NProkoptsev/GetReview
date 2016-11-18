package dmd.project.grabber;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import dmd.project.objects.Item;
import dmd.project.objects.Review;
import dmd.project.objects.Source;
import dmd.project.objects.User;


public class YandexGrabber {
    protected final String API_HOST =
            "api.content.market.yandex.ru";
    protected final String API_KEY = 
            "zDGzmkHP5GX8qCQqpJolBMeveymLKh";
    protected final int PAGES_LIMIT = 50;
    protected final int CONNECTION_TIMEOUT = 5000;
    
    protected boolean offlineWhenPossible = false;
    protected boolean saveLocally = false;
    
    protected long onlineQueries = 0;
    
    protected static final String LOCAL_DIR =
            "grabber" + File.separator + "GrabberYandex" + File.separator;
    protected HashMap<String, Category> categories = new HashMap<>();
    protected HashMap<String, Item> objs = new HashMap<>();
    protected Set<String> noReviews = new HashSet<>();
    protected Source source = new Source();
    
    // Getters
    public HashMap<String, Category> getCategories() {
        return categories;
    }

    public HashMap<String, Item> getItems() {
        return objs;
    }

    public Set<String> getNoReviews() {
        return noReviews;
    }
    
    public long getOnlineQueriesCount() {
        return onlineQueries;
    }
    
    /**
     * If the grabber was created  with true 
     * offlineWhenPossible property, than it will try to
     * use the local files first and only then the api requests.
     */
    public YandexGrabber(boolean offlineWhenPossible) {
        this.offlineWhenPossible = offlineWhenPossible;
        
        source.setName("Yandex Market");
        source.setId(2);
        source.setUrl("https://market.yandex.ru");
        
        File dir = new File(LOCAL_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    
    /**
     * If true is specified, all responses will be saved in the
     * grabber's directory.
     * @param saveLocally
     */
    public void saveResponsesLocally(boolean saveLocally) {
        this.saveLocally = saveLocally;
    }
    
    /**
     * Returns the list of saved categories OR FETCHES IT FROM THE WEB
     * @return
     */
    public HashMap<String, Category> readCategories() {
        try {
            FileInputStream fis = new FileInputStream(LOCAL_DIR + "categories.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            categories = (HashMap<String, Category>) ois.readObject();
            ois.close();
            
        } catch (Exception e) {
            System.out.println("Categories was not found locally. " + 
                    "They will be parsed from the web");
            categories = getCategories(null);
        }
        
        return categories;
    }
    
    /**
     * Returns the list of saved objects
     * @return
     */
    public HashMap<String, Item> readItems() {
        try {
            FileInputStream fis = new FileInputStream(LOCAL_DIR + "objects.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            objs = (HashMap<String, Item>) ois.readObject();
            ois.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return objs;
    }
    
    /**
     * Returns set of already visited links which contains no objects,
     * no reviews or returned 404 error 
     * @return
     */
    public Set<String> readNoReviewsList() {
        try {
            FileInputStream fis = new FileInputStream(LOCAL_DIR + "noReviews.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            noReviews = (Set<String>) ois.readObject();
            ois.close();
            
        } catch (Exception e) {
            System.out.println("No reviews list was not found locally. " +
                    "The new one will be created");
        }
        
        return noReviews;
    }
    
    /**
     * Returns the map of categories:
     * key   - category's name
     * value - category itself
     * @return
     */
    public HashMap<String, Category> getCategories(String parentCatId) {
        String url;
        String json = null;
        HashMap<String, Category> cats = new HashMap<>(); 
        if (parentCatId == null) {
            url = "https://" + API_HOST + 
                    "/v1/category.json?remote_ip=188.130.155.154&count=30";
            json = getJson("categories.json", url);
        } else {
            url = "https://" + API_HOST + "/v1/category/" + 
                    parentCatId + "/children.json?remote_ip=188.130.155.154&count=30";
            json = getJson(parentCatId + "_subcategories.json", url);
        }
        
        // Parse json and create the categories objects
        if (json != null) {
            JsonReader jreader = Json.createReader(
                    new StringReader(json));
            JsonObject jCats = jreader.readObject()
                    .getJsonObject("categories");
            JsonArray jItems = jCats.getJsonArray("items");
            for (int i = 0; i < jItems.size(); i++) {
                JsonObject jobj = jItems.getJsonObject(i);
                Category cat = new Category(jobj);
                categories.put(cat.getName(), cat);
                if (cat.getChildrenCount() > 0) {
                    cat.setSubCategories(getCategories(
                            String.valueOf(cat.getId())));
                }
            }
        }
        
        // Save
        if (json != null) {
            String fileName = null;
            if (parentCatId == null) {
                fileName = "categories.json";
            } else {
                fileName = parentCatId + "_subcategories.json";
            }
            saveJson(fileName, json);
        }
        
        return cats;
    }
    
    
    /**
     * Gets items from the category. Items will not be automatically
     * saved to the grabber's memory.
     * Maximum amount of pages is bound by PAGES_LIMIT field, chosen to
     * correspond to the limit of api.
     * @param category
     * @return
     */
    public HashMap<String, Item> getItems(Category cat, int amountOfPages) {
        HashMap<String, Item> models = new HashMap<>();
        
        for (int page = 1; page <= Math.min(amountOfPages, PAGES_LIMIT); page++) {
            String url = "https://" + API_HOST + "/v1/category/" + cat.getId() + 
                    "/models.json?remote_ip=188.130.155.154&count=30&page=" + page;
            String json = getJson(cat.getId() + "_cat_models_p" + page + ".json", url);
            
            // Parse json and fill the set
            JsonReader jreader = Json.createReader(
                    new StringReader(json));
            JsonObject jModels = jreader.readObject()
                    .getJsonObject("models");
            JsonArray jItems = jModels.getJsonArray("items");
            for (int i = 0; i < jItems.size(); i++) {
                JsonObject jobj = jItems.getJsonObject(i);
                String modelId = String.valueOf(jobj.getInt("id"));
                
                try {
                    if (!noReviews.contains(modelId)) {
                        Item obj = getItem(modelId, cat.getName());
                        if (obj.getReviews().size() > 0) {
                            models.put(modelId, obj);
                        } else {
                            noReviews.add(modelId);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("!ERR: model " + modelId + " cannot be grabbed");
                }
            }
            
            saveJson(cat.getId() + "_cat_models_p" + page + ".json", json);
        }
        
        return models;
    }
    
    public HashMap<String, Item> getItems(Category cat) {
        return getItems(cat, 1);
    }
    
    /**
     * Returns the Item by the specified model id.
     * Category's name will not be validated and will be
     * used, if there is no type of the model.
     * If no vendor specified, it will be silently ignored.
     * @param model
     * @return
     */
    public Item getItem(String model, String catName) {
        String url = "https://" + API_HOST + "/v1/model/" + model + 
                ".json?remote_ip=188.130.155.154";
        String json = getJson("model_" + model + ".json", url);
        
        JsonReader jreader = Json.createReader(
                new StringReader(json));
        JsonObject jobj = jreader.readObject()
                .getJsonObject("model");

        Item obj = new Item();
        obj.setName(jobj.getString("name"));
        obj.setSourceUrl(url);
        try {
            obj.setVendor(jobj.getString("vendor"));
            obj.setName(obj.getName() + " " + obj.getVendor());
        } catch(Exception e) {
            // silently ignore
        }
        
        try {
            String type = jobj.getString("kind");
            obj.setType(type);
        } catch (Exception e) {
            obj.setType(catName);
        }
        obj.setDescription(jobj.getString("description"));
        
        // Reviews
        obj.setReviews(getReviews(model));
        
        // Images
        JsonArray jImgs = null;
        try { // workaround
            jImgs = jobj.getJsonObject("photos")
                    .getJsonArray("photo");
        } catch (Exception e) {
            jImgs = jobj.getJsonArray("photos");
        }
        if (jImgs != null) {
            for (int i = 0; i < jImgs.size(); i++) {
                JsonObject jImg = jImgs.getJsonObject(i);
                obj.addImage(jImg.getString("url"));
            }
        }
        
        // Save json
        saveJson("model_" + model + ".json", json);

        return obj;
    }

    /**
     * Returns the list of reviews by the specified model id
     * @param model
     * @return
     */
    public List<Review> getReviews(String model) {
        List<Review> reviews = new ArrayList<>();
        
        for (int page = 1; page <= 1; page++) {
            String url = "https://" + API_HOST + "/v1/model/" + model
                    + "/opinion.json?sort=rank&count=30&page=" + page;
            String json = getJson(
                    "model_" + model + "_reviews_p" + page + ".json", url);
            
            // Parse json and fill the list
            JsonReader jreader = Json.createReader(
                    new StringReader(json));
            JsonObject jMainObj = jreader.readObject()
                    .getJsonObject("modelOpinions");
            JsonArray jItems = jMainObj.getJsonArray("opinion");
            for (int i = 0; i < jItems.size(); i++) {
                JsonObject jobj = jItems.getJsonObject(i);
                Review r = new Review();
                r.setSource(source);
                r.setGrade(jobj.getInt("grade", 0));
                r.setOverall(jobj.getString("text", ""));
                r.setPros(jobj.getString("pro", ""));
                r.setCons(jobj.getString("contra", ""));
                r.setAuthor(new User(jobj.getString("author", "")));
                r.setSourceUrl(url);
                r.setDate(new Date(jobj.getJsonNumber("date").longValue()).toString());                
                // set the review
                reviews.add(r);
                jreader.close();
            }
            
            // Save json
            saveJson("model_" + model + "_reviews_p" + page + ".json", json);
        }
        
        return reviews;        
    }
    
    /**
     * Saves the json in the directory: localDir/json/
     * with the specified file's name.
     * @param fileName
     * @param json
     */
    protected void saveJson(String fileName, String json) {
        if (saveLocally == false) {
            return;
        }
        
        if (json != null && 
                fileName != null && !fileName.isEmpty()) {
            try {
                File file = new File(fileName);
                if (!file.exists()) {
                    BufferedWriter wr = new BufferedWriter(
                            new FileWriter(LOCAL_DIR + "json" 
                                    + File.separator + fileName));
                    wr.write(json);
                    wr.flush();
                    wr.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    /**
     * Retrieves json string for the specified filename 
     * and url (what will be used depends on 
     * offlineWhenPossible parameter.
     * @param fileName
     * @param url
     * @return
     */
    public String getJson(String fileName, String url) {
        String json = null;
        boolean offline = offlineWhenPossible;
        
        if (offline == true && fileName != null) {
            try {
                //Read the saved file
                BufferedReader br = new BufferedReader(
                        new FileReader(LOCAL_DIR + "json/" + fileName));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = br.readLine()) != null) {
                    sb.append(line);
                }
                json = sb.toString();
                
                System.out.println("Go offline => " + fileName);
                
            } catch (Exception e) {
                System.out.println("Cannot read file " + fileName);
                offline = false;
            }
        } else {
            offline = false;
        }
        
        if (offline == false) {
            System.out.println("Go online => " + url);
            onlineQueries++;
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put("Host", API_HOST);
                headers.put("Authorization", API_KEY);
                Connection conn = Jsoup.connect(url);
                conn.headers(headers);
                conn.ignoreContentType(true);
                Response resp = conn.execute();
                json = resp.body();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return json;
    }
    
    
    /**
     * Save the item.
     */
    protected void saveItem(String itemId, Item item) {
        objs.put(itemId, item);
    }
    
    
    /**
     * Save the items.
     */
    protected void saveItems(HashMap<String, Item> items) {
        for (Entry<String, Item> entry : items.entrySet()) {
            saveItem(entry.getKey(), entry.getValue());
        }
    }
    

    /**
     * Fetches items not included in excludedCats with
     * the specified depth for special categories
     * Items will be saved in grabber's memory
     * @param cats
     * @throws Exception
     */
    public void fetchItems(Set<String> excludedCats,
            Map<String, Integer> pages) {
        HashMap<String, Category> cats = getCategories();
        for (Entry<String, Category> entry : cats.entrySet()) {
            if (!excludedCats.contains(entry.getKey())) {
                fetchItems(entry.getValue(), pages);
            }
        }
    }
    
    
    /**
     * Fetches items for the specified set of categories with
     * all their sub-categories.
     * Items will be saved in grabber's memory
     * @param cats
     * @throws Exception
     */
    public void fetchItems(Map<String, Category> cats,
            Map<String, Integer> pages) {
        for (Entry<String, Category> entry : cats.entrySet()) {
            Category c = entry.getValue();
            fetchItems(c, pages);
        }
    }
    
    /**
     * Fetches items for the specified category and all its
     * sub-categories BY NAME OF CATEGORY.
     * Items will be saved in grabber's memory
     * @param c
     * @throws Exception
     */
    public void fetchItems(String cat,
            Map<String, Integer> pages) throws NullPointerException {
        System.out.println(categories.size());
        Category c = categories.get(cat);
        if (c == null) {
            throw new NullPointerException("The category does not exist: " + cat);
        }
        fetchItems(c, pages);
    }
    /**
     * Fetches items for the specified category and all its
     * sub-categories BY CATEGORY ITSELF.
     * Items will be saved in grabber's memory
     * @param c
     */
    public void fetchItems(Category c, Map<String, Integer> pages) {
        System.out.println("Category: " + c + " - " + c.getId());
        if (c.getChildrenCount() > 0) {
            fetchItems(c.getSubCategories(), pages);
        } else {
            try {
                // The items already were in the map will be refreshed
                if (pages != null && pages.containsKey(c.getName())) {
                    saveItems(getItems(c, pages.get(c.getName())));
                } else {
                    saveItems(getItems(c));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    
    
    /**
     * Serialization
     * @param objs
     */
    public void serialize() {
        try {
/*            FileOutputStream fos = new FileOutputStream(LOCAL_DIR + "objects.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(objs);
            oos.flush();
            oos.close();
            */
            FileOutputStream fos = new FileOutputStream(LOCAL_DIR + "noReviews.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(noReviews);
            oos.flush();
            oos.close();
            
            fos = new FileOutputStream(LOCAL_DIR + "categories.ser");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(categories);
            oos.flush();
            oos.close();
            
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        YandexGrabber grabber = new YandexGrabber(true);
        System.out.println("Reading categories");
        grabber.readCategories();
        System.out.println("Reading items");
        grabber.readItems();
        System.out.println("Reading the list of items with no reviews");
        grabber.readNoReviewsList();
        
        int itemsWere = grabber.getItems().size();
        System.out.println("Links with no reviews: " + 
                grabber.getNoReviews().size());
        System.out.println("Items have been read: " + 
                itemsWere);
        
        // Special categories
        Set<String> excludedCats = new HashSet<String>();
        excludedCats.add("Одежда, обувь и аксессуары");
        
        Map<String, Integer> pages = new HashMap<>();
        pages.put("Телевизоры и плазменные панели", 20);
        pages.put("Мобильные телефоны", 50);
        pages.put("Художественная литература", 20);
        pages.put("USB Flash drive", 50);
        
        // Grab and save
        HashMap<String, Category> cats = grabber.getCategories();
        for (Entry<String, Category> entry : cats.entrySet()) {
            if (!excludedCats.contains(entry.getKey())) {
                grabber.fetchItems(entry.getValue(), pages);
            }
        }
        //grabber.fetchModels("Электроника", pages);
        grabber.serialize();
        
        
        System.out.println("-----------------------------------------");
        System.out.println("Items saved: " + grabber.getItems().size());
        System.out.println("Items added: " 
                + (grabber.getItems().size() - itemsWere));
        System.out.println("Queries to api was made: " 
                + grabber.getOnlineQueriesCount());
/*
        Подарки, сувениры, цветы + (60 models)
        Товары для красоты и здоровья + (270 models)
        Услуги + (0)
        Продукты, напитки + (0)
        Товары для дома и дачи + (1020)
        Детские товары + (5670)
        Бытовая техника + (1410)
        Электроника + (1170)
        Досуг и развлечения + (4448)
        Оборудование + (0)
        Товары для спорта и отдыха + (600)
        Одежда, обувь и аксессуары + (~10000) !!! reviews -> 0
        Товары для животных + (90)
        Товары для офиса + (60)
        Товары для авто- и мототехники + (270)
        Компьютерная техника + (867)
*/        
        
        // Телевизоры и плазменные панели 90639 -> page 50
        // Художественная литература 90851 -> page 27
        // Мобильные телефоны 91491 -> page 50
        
    }
    
    
    
    
    /**
     * Category representation
     */
    protected static class Category implements Serializable {
        protected int id;
        protected String name;
        protected int childrenCount;
        protected String json;
        
        protected HashMap<String, Category> subCategories =
                new HashMap<>();
        
        public Category() {
            name = "test";
        }
        
        public Category(JsonObject jobj) {
            id = jobj.getInt("id");
            name = jobj.getString("uniqName");
            childrenCount = jobj.getInt("childrenCount");
            json = jobj.toString();
        }
        
        public int getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public int getChildrenCount() {
            return childrenCount;
        }
        
        public Map<String, Category> getSubCategories() {
            return subCategories;
        }
        
        public void setSubCategories(
                HashMap<String, Category> subCategories) {
            this.subCategories = subCategories;
        }
        
        @Override
        public int hashCode() {
            return name.hashCode();
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
}
