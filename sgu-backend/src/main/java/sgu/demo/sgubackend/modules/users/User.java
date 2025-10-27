package sgu.demo.sgubackend.modules.users;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false, unique = false, length = 100)
    private String name;

    @Column(name = "lastname", nullable = false, unique = false, length = 100)
    private String lastname;

    @Column(name = "surname", nullable = false, unique = false, length = 100)
    private String surname;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "tel", nullable = false, unique = true, length = 100)
    private String tel;


    public User() {
    }


    public User(Long id, String name, String lastname, String surname, String email, String tel) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.surname = surname;
        this.email = email;
        this.tel = tel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
