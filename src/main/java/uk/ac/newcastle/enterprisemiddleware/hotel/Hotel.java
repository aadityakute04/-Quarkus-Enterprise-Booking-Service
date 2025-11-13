package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "hotel", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Hotel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Hotel name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull(message = "City is required")
    @Size(min = 2, max = 100)
    private String city;

    @NotNull(message = "Address is required")
    @Size(min = 5, max = 255)
    private String address;

    @Min(1)
    @Max(5)
    private Integer starRating;

    @Size(max = 254)
    private String email;

    @Size(max = 30)
    private String phoneNumber;

    // Bidirectional relationship to Booking: cascade so deleting hotel removes bookings.
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<uk.ac.newcastle.enterprisemiddleware.booking.Booking> bookings = new ArrayList<>();

    public Hotel() {}

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getStarRating() { return starRating; }
    public void setStarRating(Integer starRating) { this.starRating = starRating; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public List<uk.ac.newcastle.enterprisemiddleware.booking.Booking> getBookings() {
        return bookings;
    }
    public void setBookings(List<uk.ac.newcastle.enterprisemiddleware.booking.Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
