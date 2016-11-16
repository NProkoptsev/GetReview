package com.getreviews.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Client.
 */
@Entity
@Table(name = "client")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final boolean INTERNAL = false;
    public static final boolean EXTERNAL = true;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "ext_or_int")
    private Boolean ext_or_int;

    @OneToMany(mappedBy = "client")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Review> reviews = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Client fullname(String fullname) {
        this.fullname = fullname;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Client nickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public Boolean isExt_or_int() {
        return ext_or_int;
    }

    public Client ext_or_int(Boolean ext_or_int) {
        this.ext_or_int = ext_or_int;
        return this;
    }

    public void setExt_or_int(Boolean ext_or_int) {
        this.ext_or_int = ext_or_int;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public Client reviews(Set<Review> reviews) {
        this.reviews = reviews;
        return this;
    }

    public Client addReview(Review review) {
        reviews.add(review);
        review.setClient(this);
        return this;
    }

    public Client removeReview(Review review) {
        reviews.remove(review);
        review.setClient(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Client client = (Client) o;
        if(client.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Client{" +
            "id=" + id +
            ", fullname='" + fullname + "'" +
            ", nickname='" + nickname + "'" +
            ", ext_or_int='" + ext_or_int + "'" +
            '}';
    }
    public static Client johnDoe() {
        Client client = new Client();
        client.fullname = "John Doe";
        client.nickname = "JD";
        client.ext_or_int = EXTERNAL;
        return client;
    }
}
