package com.getreviews.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
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

    @Column(name = "text")
    private String text;

    @Column(name = "rating")
    private Float rating;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public Review text(String text) {
        this.text = text;
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Float getRating() {
        return rating;
    }

    public Review rating(Float rating) {
        this.rating = rating;
        return this;
    }

    public void setRating(Float rating) {
        this.rating = rating;
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
            ", text='" + text + "'" +
            ", rating='" + rating + "'" +
            '}';
    }
}
