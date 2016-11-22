package com.getreviews.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.codahale.metrics.annotation.Timed;
import com.getreviews.domain.Client;
import com.getreviews.domain.Image;
import com.getreviews.domain.Item;
import com.getreviews.domain.Review;
import com.getreviews.domain.Source;
import com.getreviews.repository.ClientRepository;
import com.getreviews.repository.ImageRepository;
import com.getreviews.repository.ItemRepository;
import com.getreviews.repository.ReviewRepository;
import com.getreviews.repository.SourceRepository;
import com.getreviews.security.AuthoritiesConstants;

import dmd.project.grabber.DateTransformer;
import dmd.project.grabber.Merger;
import dmd.project.grabber.OzonGrabber;
import dmd.project.grabber.YandexGrabber;



@RestController
@RequestMapping("/grabber")
public class Grabber {
    @Inject
    private ItemRepository itemRepository;
    
    @Inject
    private ReviewRepository reviewRepository;
    
    @Inject
    private ImageRepository imageRepository;
    
    @Inject
    private ClientRepository clientRepository;
    
    @Inject
    private SourceRepository sourceRepository;
    
    
    Map<String, Source> sources = new HashMap<>();

    /**
     * if sources don't exist - create
     */
    public void initSources() {
        Source source = new Source();
        source.setName("Ozon.ru");
        source = sourceRepository.findOneByExample(source);
        if (source == null) {
            source = new Source();
            source.setName("Ozon.ru");
            source.setDescription("Ozon.ru - a popular internet shop");
            source.setUrl("http://www.ozon.ru");
            source = sourceRepository.save(source);
        }
        sources.put(source.getName(), source);
        
        source = new Source();
        source.setName("Yandex Market");
        source = sourceRepository.findOneByExample(source);
        if (source == null) {
            source = new Source();
            source.setName("Yandex Market");
            source.setDescription("Yandex market - a reviews-holding platform");
            source.setUrl("https://market.yandex.ru");
            source = sourceRepository.save(source);
        }
        sources.put(source.getName(), source);
    }
    
    
    @RequestMapping(value = "/local",
            method = RequestMethod.GET)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<String> localImport(
            @RequestParam(value="import_all", required=false) boolean importAll) {
        String returnMessage = "";
        List<dmd.project.objects.Item> objs = null;
        initSources();
        
        if (importAll == false) {
            try {
                // Deserialize
                FileInputStream fis = new FileInputStream(
                        "grabber" + File.separator + "common_items.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);
                objs = (List<dmd.project.objects.Item>) ois.readObject();
                
                for (dmd.project.objects.Item obj : objs) {
                    Item item = saveToItem(obj);
                }
                returnMessage = "Items have been exported";
                
                ois.close();
                
            } catch (Exception e) {
                e.printStackTrace();
                returnMessage = "Something went wrong. Details are in log";
            }
        } else {
            Collection<dmd.project.objects.Item> ozonItems = 
                    Merger.getObjects("grabber" + File.separator
                            + "GrabberOzon" + File.separator + "objects.ser").values();
            Collection<dmd.project.objects.Item> yandexItems = 
                    Merger.getObjects("grabber" + File.separator
                            + "GrabberYandex" + File.separator + "objects.ser").values();
            Set<String> common = new HashSet<>();
            
            for (dmd.project.objects.Item yandexItem : yandexItems) {
                 for (dmd.project.objects.Item ozonItem : ozonItems) {
                     if (Merger.similiar(yandexItem, ozonItem)) {
                         dmd.project.objects.Item item =
                                 Merger.merge(yandexItem, ozonItem);
                         common.add(ozonItem.getSourceUrl());
                         common.add(yandexItem.getSourceUrl());
                         
                         saveToItem(item);
                     }
                 }
             }
            
            for (dmd.project.objects.Item item : yandexItems) {
                if (!common.contains(item.getSourceUrl())) {
                    saveToItem(item);
                }
            }
            for (dmd.project.objects.Item item : ozonItems) {
                if (!common.contains(item.getSourceUrl())) {
                    saveToItem(item);
                }
            }
            
            System.out.println("Similiar items found: " + (common.size() / 2));
        }
        
        return ResponseEntity.ok().body(returnMessage);
    }
    
    
    /**
     * Grab items from Ozon.ru
     * @return
     */
    @RequestMapping(value = "/ozon",
            method = RequestMethod.GET)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<String> grabOzon(
            @RequestParam(value="cat", required=false) String categoryId,
            @RequestParam(value="depth", required=false) Integer depth,
            @RequestParam(value="start", required=false) String startId,
            @RequestParam(value="finish", required=false) String finishId,
            @RequestParam(value="ignore_history", required=false) boolean ignoreHistory) {
        initSources();
        OzonGrabber grabber = new OzonGrabberService(false);
        if (ignoreHistory == true) {
            System.out.println("History is ignoresd");
        } else {
            grabber.readBrokenLinks();
            grabber.readNoReviewsList();
        }
        
        if (categoryId == null && startId == null) {
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
        }
        if (categoryId != null) {
            int p = 1;
            if (depth != null) {
                p = depth;
            }
            grabber.fetchItems(categoryId, p);
        }
        if (startId != null && finishId != null) {
            int initialId = Integer.valueOf(startId);
            int maxId = Integer.valueOf(finishId);
            
            for (int i = initialId; i <= maxId; i++) {
                grabber.fetchItem(String.valueOf(i));
            }
        }
        
        return ResponseEntity.ok().body("ozon grabber finished its work");
    }
    
    /**
     * Grab items from Yandex.Market
     * @return
     */
    @RequestMapping(value = "/yandex",
            method = RequestMethod.GET)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<String> grabYandex(
            @RequestParam(value="cat_name", required=false) String categoryName,
            @RequestParam(value="depth", required=false) Integer depth) {
        initSources();
        YandexGrabber grabber = new YandexGrabberService(true);
        grabber.saveResponsesLocally(true); //!!!
        grabber.readCategories();        
        grabber.readNoReviewsList();
        
        Map<String, Integer> pages = new HashMap<>();
        
        if (categoryName == null) {
            // Excluded categories
            Set<String> excludedCats = new HashSet<String>();
            excludedCats.add("Одежда, обувь и аксессуары");
            
            // Categories with hard-coded depth
            pages.put("Телевизоры и плазменные панели", 20);
            pages.put("Мобильные телефоны", 20);
            pages.put("Художественная литература", 20);
            pages.put("USB Flash drive", 20);
            
            // Grab and save
            //grabber.fetchItems(excludedCats, pages);
            grabber.fetchItems("Электроника", pages);
            grabber.fetchItems("Досуг и развлечения", pages);
            grabber.fetchItems("Компьютерная техника", pages);
            
        } else {
            if (depth != null) {
                pages.put(categoryName, depth);
            }
            grabber.fetchItems(categoryName, pages);
        }
        
        // Save cagtegories list and no reviews list
        //grabber.serialize();
        
        return ResponseEntity.ok().body("Queries to api was made: " 
                + grabber.getOnlineQueriesCount());
    }
    
    
    /**
     * Ozon grabber
     */
    protected class OzonGrabberService extends OzonGrabber {
        public OzonGrabberService(boolean onlyOffline) {
            super(onlyOffline);
        }
        
        @Override
        public void saveItem(String itemId, dmd.project.objects.Item item) {
            System.out.println("SAVING ITEM");
            saveToItem(item);
        }
    }
    
    
    /**
     * Yandex grabber
     */
    protected class YandexGrabberService extends YandexGrabber {
        public YandexGrabberService(boolean onlyOffline) {
            super(onlyOffline);
        }
        
        @Override
        public void saveItem(String itemId, dmd.project.objects.Item item) {
            saveToItem(item);
        }
    }
    
    
    // SaveTo methods - the bridge between grabber's object model and
    // application's object model
    public Item saveToItem(dmd.project.objects.Item obj) {
        boolean itemExisted = true;
        Item item = new Item();
        item.setName(obj.getName());
        List<Item> similiarItems = itemRepository.findAllLike(item);
        System.out.println("SIMILIAR ITEMS FOUND: " + similiarItems.size());
        
        if (similiarItems.size() == 1) {
            item = similiarItems.get(0);
        } else {
            item = itemRepository.findOneByExample(item);
            if (item == null) {
                // Create and save new item
                System.out.println("CREATING NEW ITEM");
                itemExisted = false;
                
                item = new Item();
                item.setName(obj.getName());
                item.setDescription(obj.getDescription());
                item = itemRepository.save(item);
            }
        }

        // Images
        if (itemExisted == false) {
            Set<Image> images = new HashSet<>();
            for (String imgUrl : obj.getImages()) {
                Image img = saveToImage(imgUrl, item);
                images.add(img);
            }
            item.setImages(images);
        }
        // Reviews
        Set<Review> reviews = new HashSet<>();
        for (dmd.project.objects.Review r : obj.getReviews()) {
            try {
                Review review = saveToReview(r, item, itemExisted);
                if (review != null) {
                    reviews.add(review);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        item.setReviews(reviews);
        
        return item;
    }
        
    public Image saveToImage(String imgUrl, Item item) {
        Image img = new Image();
        img.setItem(item);
        img.setUrl(imgUrl);
        img = imageRepository.save(img);
        
        return img;
    }
    
    
    public Review saveToReview(
            dmd.project.objects.Review r, Item item) {
        return saveToReview(r, item, false);
    }
    
    
    public Review saveToReview(dmd.project.objects.Review r,
            Item item, boolean itemExisted) {
        Review review = new Review();
        review.setItem(item);
        if (r.getGrade() < 0) {
            r.setGrade(-1 * r.getGrade());
        }
        review.setRating((float) r.getGrade());
        String text = "";
        if (r.getPros() != null) {
            text += r.getPros();
        }
        if (r.getCons() != null) {
            text = text + " " + r.getCons();
        }
        if (r.getOverall() != null) {
            text = text + " " + r.getOverall();
        }
        if (text.isEmpty()) {
            return null;
        }
        review.setText(text);
        // PROS!
        // CONS!
        
        if (sources.size() == 0) {
            initSources();
        }
        Source source = sources.get(r.getSource().getName());
        review.setSource(source);
        
        Review rvw = null;
        if (itemExisted == true) {
            rvw = reviewRepository.findOneByExample(review);
        }
        if (rvw != null) {
            review = rvw;
        
        } else {
            Client client = saveToClient(r.getAuthor(), item);
            review.setClient(client);
            
            try {
                review.setCreatedDate(
                        DateTransformer.transform(r.getDate()));
            } catch(Exception e) {
                e.printStackTrace();
            }
            
            review = reviewRepository.save(review);
         }
         return review;
    }
    
    
    public Client saveToClient(
            dmd.project.objects.User user, Item item) {
        Client client = new Client();
        if (user.getNickname() == null 
                || user.getNickname().isEmpty()) {
            client.setNickname("Anonymous");
            client.setExt_or_int(true);
            client = clientRepository.findOneByExample(client);
            if (client == null) {
                client = new Client();
                client.setNickname("Anonymous");
                client.setExt_or_int(true);
                client = clientRepository.save(client);
            }
        
        } else {
            client.setNickname(user.getNickname());
            client.setExt_or_int(true);
            client = clientRepository.save(client);
        }
        
        return client;
    }
    
    
    public Source saveToSource(
            dmd.project.objects.Source s, Item item) {
        Source source = new Source();
        source.setName(s.getName());
        source.setUrl(s.getUrl());
        source.setDescription(s.getDescription());
        
        source = sourceRepository.save(source);
        return source;            
    }
}
