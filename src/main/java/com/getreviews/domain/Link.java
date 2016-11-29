package com.getreviews.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Link.
 */
@Entity
@Table(name = "link")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Link implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private Item item;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public Link url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public Link description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Item getItem() {
        return item;
    }

    public Link item(Item item) {
        this.item = item;
        return this;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Link link = (Link) o;
        if(link.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, link.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Link{" +
            "id=" + id +
            ", url='" + url + "'" +
            ", description='" + description + "'" +
            '}';
    }
}
