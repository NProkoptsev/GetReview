package com.getreviews.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * A Review.
 */

@Entity
@Table(name = "review")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 10000)
    @Column(name = "text", length = 10000)
    private String text;

    @Column(name = "rating")
    private Float rating;

    @ManyToOne
    private Source source = Source.stub();

    @ManyToOne
    private Client client;

    @ManyToOne
    private Item item;


    //TODO should be some other structure - current User is only for internal
    //TODO implement saving
    private User author = User.johnDoe();

    //TODO implement saving
    //e.g. implementation option, see @CreateDate and @LastModifiedDate
    // in http://docs.spring.io/spring-data/jpa/docs/1.4.1.RELEASE/reference/html/jpa.repositories.html
    //another option - generate in DBMS on update (may be as trigger may be as sequence)
    //TODO may be better to add createdDate and updatedDate to all entities via inherit all entities
    //from some new base class Entity
    private Date createdDate = new Date(); //Just as stub
    private Date updatedDate = new Date(); //

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Review rating(Float rating) {
        this.rating = rating;
        return this;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Review text(String text) {
        this.text = text;
        return this;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Review source(Source source) {
        this.source = source;
        return this;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Review client(Client client) {
        this.client = client;
        return this;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Review item(Item item) {
        this.item = item;
        return this;
    }
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Review review = (Review) o;
        if(review.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Review{" +
            "id=" + id +
            ", rating='" + rating + "'" +
            ", text='" + text + "'" +
            '}';
    }
}
