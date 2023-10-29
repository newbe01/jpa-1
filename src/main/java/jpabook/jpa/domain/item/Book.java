package jpabook.jpa.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Setter
@Getter
@DiscriminatorValue("B")
@Entity
public class Book extends Item {

    private String author;

    private String isbn;

}
