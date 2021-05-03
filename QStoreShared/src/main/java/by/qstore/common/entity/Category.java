package by.qstore.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String alias;
    private String image;
    private boolean enabled;

    public Category(Long id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    @Transient
    public String getImagePath() {
        if (this.id == null) {
            return "/images/default_image_thumbnail.png";
        }
        return "/category-images/" + this.id + "/" + this.image;
    }
}
