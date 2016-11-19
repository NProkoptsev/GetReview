package dmd.project.grabber;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import dmd.project.grabber.YandexGrabber.Category;
import dmd.project.objects.ExternalUser;
import dmd.project.objects.Item;
import dmd.project.objects.Review;
import dmd.project.objects.Source;


public class OzonGrabber {
    protected final String LOCAL_DIR = 
            "grabber" + File.separator + "GrabberOzon" + File.separator;  
    protected final int CONNECTION_TIMEOUT = 5000;
    
    protected HashMap<String, Item> objs = new HashMap<>();
    protected Set<String> noReviews = new HashSet<>();
    protected Set<String> brokenLinks = new HashSet<>();
    protected boolean offlineWhenPossible = false;
    protected Source source = new Source();
    
    
    // Getters
    public HashMap<String, Item> getItems() {
        return objs;
    }

    public Set<String> getNoReviews() {
        return noReviews;
    }

    public Set<String> getBrokenLinks() {
        return noReviews;
    }
    
    /**
     * If the grabber was created  with true 
     * onlyOffline property, than it will try to
     * use only the local files. No file assumes that
     * there is no page in the web.
     */
    public OzonGrabber(final boolean onlyOffline) {
        this.offlineWhenPossible = onlyOffline;
        
        source.setName("Ozon.ru");
        source.setId(1);
        source.setUrl("http://www.ozon.ru");
    }

    /**
     * Returns set of already visited links which contains 
     * no objects or returned 404 error 
     * @return
     */
    public Set<String> readBrokenLinks() {
        try {
            FileInputStream fis = new FileInputStream(
                    LOCAL_DIR + "brokenLinks.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            brokenLinks = (Set<String>) ois.readObject();
            ois.close();
            
        } catch (Exception e) {
            System.out.println("Broken links file was not found");
            File file = new File(LOCAL_DIR);
            file.mkdirs();
        }
        
        return brokenLinks;
    }
    
    
    /**
     * Returns set of already visited links which contains
     * no reviews or returned 404 error 
     * @return
     */
    public Set<String> readNoReviewsList() {
        try {
            FileInputStream fis = new FileInputStream(
                    LOCAL_DIR + "noReviews.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            noReviews = (Set<String>) ois.readObject();
            ois.close();
            
        } catch (Exception e) {
            System.out.println("No reviews file was not found");
            File file = new File(LOCAL_DIR);
            file.mkdirs();
        }
        
        return noReviews;
    }
    
    
    /**
     * Returns the list of saved objects
     * @return
     */
    public HashMap<String, Item> readItems() {
        try {
            FileInputStream fis = new FileInputStream(
                    LOCAL_DIR + "objects.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            objs = (HashMap<String, Item>) ois.readObject();
            ois.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return objs;
    }
    

    /**
     * Returns html document by the specified url.
     * Null will be return, if file does not exist or
     * 404 error was received.
     * if offlineWhenPossible == true, the document will be
     * fetched ONLY from local file system, otherwise - from
     * the web.
     * @param url
     * @param forceOnline
     * @return
     */
    public Document getDocument(final String url) {
        Document doc = null;
        try {
            if (offlineWhenPossible == true) {
                String fileName = LOCAL_DIR + "html" + File.separator + 
                        url.replaceAll("/", "").replaceAll(":", "")
                        .replaceAll("\\?", "") + ".html";
                File file = new File(fileName);
                System.out.println("Go offline => " + fileName);
                doc = Jsoup.parse(file, "UTF-8");
                
            } else {
                System.out.println("Go online => " + url);
                doc = Jsoup.connect(url).timeout(CONNECTION_TIMEOUT).get();
            }
        } catch (Exception e) {
            System.out.println("The specified url cannot be parsed: " + url);
        }        
        
        return doc;
    }
    
    
    /**
     * Gets items from the category.
     * This action is ALWAYS made through the WEB.
     * Ids with broken links and no reviews will be placed
     * in corresponding sets to be not processed again later.
     * @param category
     * @return
     */
    public HashMap<String, Item> getItems(
            final String categoryId, final int amountOfPages) {
        HashMap<String, Item> items = new HashMap<>();
        
        boolean hasNextPage = true;
        for (int page = 0; page < amountOfPages && hasNextPage; page++) {
            Document doc = getDocument("http://www.ozon.ru/catalog/"
                    + categoryId + "/?page=" + page);
            Element head = doc.getElementsByTag("head").first();
            Element nextPage = head
                    .getElementsByAttributeValue("rel", "next").first();
            if (nextPage == null) {
                hasNextPage = false;
            }
            
            Elements itemLinks = 
                    doc.getElementsByClass("jsUpdateLink eOneTile_link");
            for (Element itemLink : itemLinks) {
                String[] tokens = itemLink.attr("href").split("/");
                String itemId = tokens[tokens.length - 1];
                if (brokenLinks.contains(itemId) 
                        || noReviews.contains(itemId)) {
                    System.out.println("Skipping the item");
                    
                } else {
                    try {
                        Item item = getItem(itemId);
                        items.put(itemId, item);
                    } catch(IOException npe) {
                        brokenLinks.add(itemId);
                        System.out.println("The link is broken, id: " + itemId);
                    } catch(ParseException pe) {
                        if (pe.getMessage().equals("No reviews were found")) {
                            noReviews.add(itemId);
                            System.out.println("no reviews were found");
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return items;
    }
    
    
    /**
     * Returns the Item by the specified item id.
     * @param itemId
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public Item getItem(
            final String itemId) throws IOException, ParseException {
        String itemLink = "http://www.ozon.ru/context/detail/id/" 
                + itemId + "/";
        Document doc = getDocument(itemLink);
        
        if (doc == null) {
            throw new IOException();
        }
        
        Item obj = new Item();
        
        // Reviews
        try {
            obj.setReviews(getReviews(itemId));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParseException("No reviews were found", 0);
        }
        
        // Name
        Element name = doc.getElementsByClass(
                "bItemName").first();
        if (name == null) {
            throw new ParseException(
                    "name of the item is not found", 0);
        }
        obj.setName(name.text().replaceAll("Отзывы о ", "").
                replaceAll("Отзывы и рецензии о книге ", ""));
        
        // Properties
        Element property = doc.getElementsByClass(
                "eItemProperties_line").first();
        Element prName = property.getElementsByClass(
                "eItemProperties_name").first();
        Element prValue = null;
        if (prName.text().startsWith("Тип")) {
            prValue = property.getElementsByClass(
                    "eItemProperties_text").first();
        }
        if (prValue != null) {
            obj.setType(prValue.text());
        }
        
        // Images
        Elements imgs = doc.getElementsByClass("eMicroGallery_fullImage");
        for (Element img : imgs) {
            obj.addImage("http:" + img.attr("src"));
        }
        
        // Description
        Element description = doc.getElementsByClass(
                "eProductDescriptionText_text").first();
        obj.setDescription(description.text());

        // Source
        obj.setSourceUrl(itemLink);
        
        return obj;
    }

    /**
     * Returns the list of reviews by the specified item id
     * @param itemId
     * @return
     * @throws IOException -
     * if no file was found or 404 error was returned
     * @throws ParseException -
     * if no reviews were found in the html file or web page
     */
    public List<Review> getReviews(
            final String itemId) throws IOException, ParseException {
        String reviewsLink = "http://www.ozon.ru/reviews/"
                + itemId + "/#comments";
        Document reviewsDoc = getDocument(reviewsLink);
        
        if (reviewsDoc == null) {
            throw new IOException();
        }
        
        List<Review> reviews = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        Elements comments = reviewsDoc.getElementsByClass("item");
        
        if (comments.size() == 0) {
            throw new ParseException("No reviews were found", 0);
        }
        
        for (Element comment : comments) {
            Element revBody = comment.getElementsByAttributeValue(
                    "itemprop", "reviewBody").first();
            Element rAuthor = comment.getElementsByAttributeValue(
                    "itemprop", "author").first();
            Element rDate = comment.getElementsByAttributeValue(
                    "itemprop", "datePublished").first();
            Element rGrade = comment.getElementsByAttributeValue(
                    "itemprop", "ratingValue").first();
            
            Review review = new Review();
            review.setSource(source);
            review.setSourceUrl(reviewsLink);
            review.setDate(rDate.text());
            review.setAuthor(new ExternalUser(rAuthor.text()));
            review.setGrade(Integer.valueOf(rGrade.attr("content")));

            String revType = null;
            // Parse review's text: pros, cons, overall
            for (Node el : revBody.childNodes()) {
                if (el instanceof Element && 
                        "b".equals(((Element) el).tagName())) {
                    switch (((Element) el).text()) {
                        case "Достоинства:":    revType = "pros";
                                                break;
                        case "Недостатки:":     revType = "cons";
                                                break;
                        case "Комментарий:":    revType = "comment";
                                                break;
                    }
                }
                if (el instanceof TextNode && revType != null) {
                    switch (revType) {
                        case "pros":    review.setPros(el.toString());
                                        break;
                        case "cons":    review.setCons(el.toString());
                                        break;
                        case "comment": review.setOverall(el.toString());
                                        break;
                    }
                }
            }
            if (review.getOverall() == null 
                    || review.getOverall().isEmpty()) {
                review.setOverall(revBody.text());
            }
            reviews.add(review);
        }
        
        return reviews;        
    }
    
    
    /**
     * Fetches items for the specified category.
     * Items will be saved in grabber's memory.
     * @param categoryId
     */
    public void fetchItems(final String categoryId, final int pages) {
        System.out.println("Category: " + categoryId);
        try {
            // The items already were in the map will be refreshed
            saveItems(getItems(categoryId, pages));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Fetch the item by id
     * @param itemId
     */
    public void fetchItem(final String itemId) {
        try {
            if (!brokenLinks.contains(itemId) 
                    && !noReviews.contains(itemId)) {
                Item item = getItem(itemId);
                saveItem(itemId, item);
            }
        } catch(IOException npe) {
            brokenLinks.add(itemId);
            System.out.println("The link is broken, id: " + itemId);
        } catch(ParseException pe) {
            if (pe.getMessage().equals("No reviews were found")) {
                noReviews.add(itemId);
                System.out.println("no reviews were found");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Save the item.
     */
    protected void saveItem(String itemId, Item item) {
        System.out.println("USUAL METHOD INVOKED");
        System.out.println("USUAL METHOD INVOKED");
        System.out.println("USUAL METHOD INVOKED");
        System.out.println("USUAL METHOD INVOKED");
        objs.put(itemId, item);
    }

    
    /**
     * Save the items.
     */
    protected void saveItems(HashMap<String, Item> items) {
        System.out.println("PLURAL INVOKED");
        System.out.println(items.size());
        System.out.println("PLURAL INVOKED");
        
        for (Entry<String, Item> entry : items.entrySet()) {
            saveItem(entry.getKey(), entry.getValue());
        }
    }
    
        
    /**
     * Serialization
     * @param brokenLinks
     * @param objs
     */
    public void serialize() {
        try {
            FileOutputStream fos = new FileOutputStream(LOCAL_DIR + "brokenLinks.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(brokenLinks);
            oos.flush();
            oos.close();
            
            fos = new FileOutputStream(LOCAL_DIR + "noReviews.ser");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(noReviews);
            oos.flush();
            oos.close();
            
/*            fos = new FileOutputStream(LOCAL_DIR + "objects.ser");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(objs);
            oos.flush();
            oos.close();*/
            
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    
    
    
    
    public static void main(String[] args) {
        String categoryId = null;
        
        
        OzonGrabber grabber = new OzonGrabber(true);
        Set<String> brokenLinks = grabber.readBrokenLinks();
        Set<String> noReviews = grabber.readNoReviewsList();
        Map<String, Item> items = grabber.readItems();
        int itemsWere = items.size();
        System.out.println("broken links: " + brokenLinks.size());
        System.out.println("links with no reviews: " + noReviews.size());
        System.out.println("items was read: " + itemsWere);
        
        // Grab by categories - default
        if (categoryId == null || categoryId.isEmpty()) {
            grabber.fetchItems("1168060", 50); // Смартфоны
            grabber.fetchItems("1133763", 50); // Телевизоры
            grabber.fetchItems("1141109", 50); // Художественная литература
            grabber.fetchItems("1178973", 50); // Планшеты
            grabber.fetchItems("1133719", 50); // Usb flash накопители
            grabber.fetchItems("1169395", 50); // Роботы-пылесосы
            grabber.fetchItems("1133734", 50); // Пылесосы
            grabber.fetchItems("1155471", 50); // Техника для дома
            grabber.fetchItems("1135615", 50); // Мультиварки
            grabber.fetchItems("1133732", 50); // Блендеры и миксеры
            grabber.fetchItems("1133693", 50); // Клавиатуры и мыши
        } else {
            grabber.fetchItems(categoryId, 50);
        }
            
        // Grab by ids
        /*int initialId = 135000000;
        int maxId = 135362000;
        if (args.length == 2 && 
                args[0].length() == 9 && args[1].length() == 9) {
            initialId = Integer.valueOf(args[0]);
            maxId = Integer.valueOf(args[1]);
        }
        
        for (int i = initialId; i <= maxId; i++) {
            grabber.fetchItem(String.valueOf(i));
        }*/
        
        // Save
        grabber.serialize();
        
        System.out.println("-----------------------------------------");
        System.out.println("Items saved: " + grabber.getItems().size());
        System.out.println("Items added: " 
                + (grabber.getItems().size() - itemsWere));
    }    
}
