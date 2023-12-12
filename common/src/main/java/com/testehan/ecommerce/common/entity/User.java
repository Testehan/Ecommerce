package com.testehan.ecommerce.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "userz")          // "user" is reserver word in postgresql..
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200, nullable = false, unique = true)
    private String email;

    @Column(length = 64, nullable = false)      // because of encoding used
    private String password;

    @Column(name = "first_name", length = 200, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 200, nullable = false)
    private String lastName;

    @Column(length = 1000)
    private String photo;

    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE           // see note 5 from notes.txt as to why this is used
    })
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Transient
    public boolean hasRole(String roleName) {
        return roles.stream().filter(role -> role.getName().equalsIgnoreCase(roleName)).count()>0;
    }

    @Transient
    public String getPhotosImagePath() {
        if (id == null || photo == null) {
            return "/images/default-user.png";
        } else {
            return "/user-photos/" + this.id + "/" + this.photo;
        }
    }

    @Transient
    public String getFullName(){
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return isEnabled() == user.isEnabled() && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getFirstName(), user.getFirstName()) && Objects.equals(getLastName(), user.getLastName()) && Objects.equals(getPhoto(), user.getPhoto()) && Objects.equals(getRoles(), user.getRoles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getPassword(), getFirstName(), getLastName(), getPhoto(), isEnabled(), getRoles());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", photo='" + photo + '\'' +
                ", enabled=" + enabled +
                ", roles=" + roles +
                '}';
    }
}
