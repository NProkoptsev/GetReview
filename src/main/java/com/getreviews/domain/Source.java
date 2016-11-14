package com.getreviews.domain;

<<<<<<< HEAD
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Source.
 */
@Entity
@Table(name = "source")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
=======
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by vansickle on 13/11/16.
 */
@Entity
>>>>>>> e977217172be16aee029014f5add4e4bdbe8234a
public class Source implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

<<<<<<< HEAD
    @Column(name = "url")
    private String url;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public Source url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

=======
    private String name;

>>>>>>> e977217172be16aee029014f5add4e4bdbe8234a
    public String getName() {
        return name;
    }

<<<<<<< HEAD
    public Source name(String name) {
        this.name = name;
        return this;
    }

=======
>>>>>>> e977217172be16aee029014f5add4e4bdbe8234a
    public void setName(String name) {
        this.name = name;
    }

<<<<<<< HEAD
    public String getDescription() {
        return description;
    }

    public Source description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Source source = (Source) o;
        if(source.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, source.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Source{" +
            "id=" + id +
            ", url='" + url + "'" +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            '}';
=======
    public static Source stub() {
        Source source = new Source();
        source.name = "Ozon.ru";
        return source;
>>>>>>> e977217172be16aee029014f5add4e4bdbe8234a
    }
}
