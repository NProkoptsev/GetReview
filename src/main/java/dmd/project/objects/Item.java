package dmd.project.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Item implements Serializable {
    protected int id;
    protected String name;
    protected String vendor;
    protected String type;
    protected String description;
    protected String sourceUrl;
    protected int sourceId;
    protected List<String> images = new ArrayList<>();
    protected List<Review> reviews = new ArrayList<>();
    
    public Item() {
        
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        sb.append("#" + getObjectId());
        sb.append(" Name: " + getName() + "; ");
        sb.append("Type: " + getType() + "; ");
        sb.append("Reviews: " + getReviews().size() + "; ");
        sb.append("Description: " + getDescription());
        
        sb.append("}");
        return sb.toString();
    }
    
    // Getters
    public int getObjectId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getVendor() {
        return vendor;
    }
    
    public String getType() {
        return type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<Review> getReviews() {
        return reviews;
    }
    
    public String getSourceUrl() {
        return sourceUrl;
    }
    
    public int getSourceId() {
        return sourceId;
    }
    
    public List<String> getImages() {
        return images;
    }
    
    // Setters
    public void setObjectId(int objectId) {
        this.id = objectId;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
    
    public void addReview(Review...reviews) {
        for (Review r : reviews) {
            this.reviews.add(r);
        }
    }
    
    public void addReviews(List<Review> reviews) {
        this.reviews.addAll(reviews);
    }
    
    public void setSourceUrl(String srcUrl) {
        this.sourceUrl = srcUrl;
    }
    
    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }
    
    public void addImage(String...imagesUrl) {
        for (String imageUrl : imagesUrl) {
            images.add(imageUrl);
        }
    }
    
    public void addImages(List<String> images) {
        this.images.addAll(images);
    }
}
