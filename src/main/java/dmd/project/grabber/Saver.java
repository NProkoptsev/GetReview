package dmd.project.grabber;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import com.getreviews.domain.Client;
import com.getreviews.domain.Image;
import com.getreviews.domain.Item;
import com.getreviews.domain.Review;
import com.getreviews.domain.Source;
import com.getreviews.repository.*;


public class Saver {
    private ItemRepository itemRepository;
    private ReviewRepository reviewRepository;
    private ImageRepository imageRepository;
    private ClientRepository clientRepository;
    private SourceRepository sourceRepository;
    
    
    public Saver(ItemRepository iR, ReviewRepository rR,
            ImageRepository imgR, ClientRepository cR, SourceRepository sR) {
        itemRepository = iR;
        reviewRepository = rR;
        imageRepository = imgR;
        clientRepository = cR;
        sourceRepository = sR;
    }
    
    public Item saveItem(dmd.project.objects.Item obj) {
        Item item = new Item();
        item.setName(obj.getName());
        item.setDescription(obj.getDescription());
        System.out.println("Repo");
        System.out.println(itemRepository);
        item = itemRepository.save(item);
     
        // Images
        Set<Image> images = new HashSet<>();
        for (String imgUrl : obj.getImages()) {
            Image img = saveImage(imgUrl, item);
            images.add(img);
        }
        item.setImages(images);
        
        // Reviews
        Set<Review> reviews = new HashSet<>();
        for (dmd.project.objects.Review r : obj.getReviews()) {
            Review review = saveReview(r, item);
            reviews.add(review);
        }
        item.setReviews(reviews);
        
        return item;
    }
        
    public Image saveImage(String imgUrl, Item item) {
        Image img = new Image();
        img.setItem(item);
        img.setUrl(imgUrl);
        img = imageRepository.save(img);
        
        return img;
    }
    
    
    public Review saveReview(
            dmd.project.objects.Review r, Item item) {
        Review review = new Review();
        review.setItem(item);
        review.setRating((float) r.getGrade());
        review.setText(r.getPros() + "/n" 
                + r.getCons() + "/n" + r.getOverall());
        // PROS!
        // CONS!
        
        Client client = saveClient(r.getAuthor(), item);
        review.setClient(client);
        
        Source source = new Source();
        source.setName(r.getSource().getName());
        source = sourceRepository.findOneByExample(source);
        review.setSource(source);
        
        review = reviewRepository.save(review);
        return review;
    }
    
    
    public Client saveClient(
            dmd.project.objects.User user, Item item) {
        Client client = new Client();
        client.setNickname(user.getNickname());
        client.setExt_or_int(true);
        client.setFullname("");
        
        client = clientRepository.save(client);
        return client;
    }
    
    
    public Source saveSource(
            dmd.project.objects.Source s, Item item) {
        Source source = new Source();
        source.setName(s.getName());
        source.setUrl(s.getUrl());
        source.setDescription(s.getDescription());
        
        source = sourceRepository.save(source);
        return source;            
    }
}