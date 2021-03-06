package com.getreviews.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Item.
 */
@Entity
@Table(name = "item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Size(max = 10000)
    @Column(name = "description", length = 10000)
    private String description;

    @Column(name = "category_id")
    private Category category;
    private Double rating;
    private Date createdDate;
    @OneToMany(mappedBy = "item")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Image> images = new HashSet<>();

    @OneToMany(mappedBy = "item")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Review> reviews = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "item_sale",
        joinColumns = @JoinColumn(name="items_id", referencedColumnName="ID"),
        inverseJoinColumns = @JoinColumn(name="sales_id", referencedColumnName="ID"))
    private Set<Sale> sales = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Item name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Item description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Image> getImages() {
        return images;
    }

    /**
     * @return just one image url
     */
    public String getImageUrl() {
        if(images ==null || images.isEmpty())
            return null;

        return images.stream().findFirst().get().getUrl();
    }

    public Item images(Set<Image> images) {
        this.images = images;
        return this;
    }

    public Item addImage(Image image) {
        images.add(image);
        image.setItem(this);
        return this;
    }

    public Item removeImage(Image image) {
        images.remove(image);
        image.setItem(null);
        return this;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public Item reviews(Set<Review> reviews) {
        this.reviews = reviews;
        return this;
    }

    public Item addReview(Review review) {
        reviews.add(review);
        review.setItem(this);
        return this;
    }

    public Item removeReview(Review review) {
        reviews.remove(review);
        review.setItem(null);
        return this;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public Set<Sale> getSales() {
        return sales;
    }

    public Item sales(Set<Sale> sales) {
        this.sales = sales;
        return this;
    }

    public Item addSale(Sale sale) {
        sales.add(sale);
        sale.getItems().add(this);
        return this;
    }

    public Item removeSale(Sale sale) {
        sales.remove(sale);
        sale.getItems().remove(this);
        return this;
    }

    public void setSales(Set<Sale> sales) {
        this.sales = sales;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Item item = (Item) o;
        if(item.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Item{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            '}';
    }


    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
