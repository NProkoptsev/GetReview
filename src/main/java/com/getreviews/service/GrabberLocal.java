package com.getreviews.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.getreviews.web.rest.util.HeaderUtil;


import org.springframework.data.domain.Example;


@RestController
@RequestMapping("/grabber")
public class GrabberLocal {
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
    
    
    @RequestMapping(value = "/local",
            method = RequestMethod.GET)
    @Timed
    public ResponseEntity<String> test() {
        String returnMessage = "";
        List<dmd.project.objects.Item> objs = null;
        try {
                {// if sources don't exist - create
                Source source = new Source();
                source.setName("Ozon.ru");
                Example<Source> example = Example.of(source);
                source = sourceRepository.findOne(example);
                if (source == null) {
                    source = new Source();
                    source.setName("Ozon.ru");
                    source.setDescription("Ozon.ru - a popular internet shop");
                    source.setUrl("http://www.ozon.ru");
                    source = sourceRepository.save(source);
                }
                
                source = new Source();
                source.setName("Yandex Market");
                example = Example.of(source);
                source = sourceRepository.findOne(example);
                if (source == null) {
                    source = new Source();
                    source.setName("Yandex Market");
                    source.setDescription("Yandex market - reviews-holding platform");
                    source.setUrl("https://market.yandex.ru");
                    source = sourceRepository.save(source);
                }
            }
                
            // Deserialize
            FileInputStream fis = new FileInputStream(
                    "grabber" + File.separator + "common_items.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            objs = (List<dmd.project.objects.Item>) ois.readObject();
            
            int COUNT = 0; // need to be deleted - temporary workaraound
            for (dmd.project.objects.Item obj : objs) {
                COUNT++;
                if (COUNT == 10) break
                ;
                Item item = new Item();
                item.setName(obj.getName());
                item.setDescription(obj.getDescription());
                item = itemRepository.save(item);
             
                // Images
                Set<Image> images = new HashSet<>();
                for (String imgUrl : obj.getImages()) {
                    Image img = new Image();
                    img.setItem(item);
                    img.setUrl(imgUrl);
                    images.add(img);
                    imageRepository.save(img);
                }
                item.setImages(images);
                
                // Reviews
                Set<Review> reviews = new HashSet<>();
                for (dmd.project.objects.Review r : obj.getReviews()) {
                    Review review = new Review();
                    review.setItem(item);
                    review.setRating((float) r.getGrade());
                    review.setText(r.getOverall());
                    // PROS
                    // CONS
                    
                    Client client = new Client();
                    client.setNickname(r.getAuthor().getNickname());
                    client.setExt_or_int(true);
                    client.setFullname("");
                    client = clientRepository.save(client);
                    review.setClient(client);
                    
                    Source source = new Source();
                    source.setName(r.getSource().getName());
                    Example<Source> example = Example.of(source);
                    source = sourceRepository.findOne(example);
                    review.setSource(source);
                    
                    review = reviewRepository.save(review);
                    reviews.add(review);
                }
                item.setReviews(reviews);
            }
            returnMessage = objs.size() + " items have been exported";
            
        } catch (Exception e) {
            e.printStackTrace();
            returnMessage = "Something went wrong. Details are in log";
        }
        
        return ResponseEntity.ok()
                .body(returnMessage);
    }
}
