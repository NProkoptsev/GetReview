package com.getreviews.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Sale.
 */
@Entity
@Table(name = "sale")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "start_time")
    private LocalDate start_time;

    @Column(name = "end_time")
    private LocalDate end_time;

    @ManyToMany(mappedBy = "sales")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Item> items = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public Sale description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStart_time() {
        return start_time;
    }

    public Sale start_time(LocalDate start_time) {
        this.start_time = start_time;
        return this;
    }

    public void setStart_time(LocalDate start_time) {
        this.start_time = start_time;
    }

    public LocalDate getEnd_time() {
        return end_time;
    }

    public Sale end_time(LocalDate end_time) {
        this.end_time = end_time;
        return this;
    }

    public void setEnd_time(LocalDate end_time) {
        this.end_time = end_time;
    }

    public Set<Item> getItems() {
        return items;
    }

    public Sale items(Set<Item> items) {
        this.items = items;
        return this;
    }

    public Sale addItem(Item item) {
        items.add(item);
        item.getSales().add(this);
        return this;
    }

    public Sale removeItem(Item item) {
        items.remove(item);
        item.getSales().remove(this);
        return this;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sale sale = (Sale) o;
        if(sale.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, sale.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Sale{" +
            "id=" + id +
            ", description='" + description + "'" +
            ", start_time='" + start_time + "'" +
            ", end_time='" + end_time + "'" +
            '}';
    }
}
