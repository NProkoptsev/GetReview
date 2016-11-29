package com.getreviews.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Acquired_at.
 */
@Entity
@Table(name = "acquired_at")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Acquired_at implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "price")
    private Float price;

    @ManyToOne
    private Item item;

    @ManyToOne
    private Shop shop;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getPrice() {
        return price;
    }

    public Acquired_at price(Float price) {
        this.price = price;
        return this;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Item getItem() {
        return item;
    }

    public Acquired_at item(Item item) {
        this.item = item;
        return this;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Shop getShop() {
        return shop;
    }

    public Acquired_at shop(Shop shop) {
        this.shop = shop;
        return this;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Acquired_at acquired_at = (Acquired_at) o;
        if(acquired_at.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, acquired_at.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Acquired_at{" +
            "id=" + id +
            ", price='" + price + "'" +
            '}';
    }
}
