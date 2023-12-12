package com.testehan.ecommerce.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 128, nullable = false, unique = true)
    private String name;

    @Column(length = 64, nullable = false,  unique = true)
    private String alias;

    @Column(length = 1000 , nullable = false)
    private String image;

    private boolean enabled;

    @Column(name="all_parent_ids", length = 256)
    private String allParentIds;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @OrderBy("name ASC")
    private Set<Category> children = new HashSet<>();

    @Transient
    public boolean hasChildren;

    public Category(String name, String alias, String image) {
        this.name = name;
        this.alias = alias;
        this.image = image;
    }

    // TODO This is not a good practice...one should not be able to create entities with his own id..ids should come from DB only
    public Category(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.alias = name;
        this.image = "default-category.png";
    }

    public Category(Integer id, String name,String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.image = "default-category.png";
    }

    // we need this method because when we will display the categories in the UI in hierarchical form, we will need to
    // set the prefixes "--" and if we do that setting on the objects retrieved from DB, it will also change their
    // names in DB because they are hibernate managed objects
    public static Category copyPartial(Category category){
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setImage(category.getImage());
        copyCategory.setEnabled(category.isEnabled());
        copyCategory.setHasChildren(category.getChildren().size()>0);

        return copyCategory;
    }

    public static Category copyPartialWithNewName(Category category,String newName){
        Category copyCategory = copyPartial(category);
        copyCategory.setName(newName);
        return copyCategory;
    }

    @Transient
    public String getImagePath() {
        if (this.id == null  ){
            return "/images/default-category.png";
        } else {
            return "/category-images/" + this.id + "/" + this.image;
        }
    }



}
