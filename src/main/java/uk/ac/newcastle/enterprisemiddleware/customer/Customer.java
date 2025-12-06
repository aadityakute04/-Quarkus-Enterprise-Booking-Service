package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "customer", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
})
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "First name is required")
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "Email is required")
    @Email
    @Size(max = 254)
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 20)
    private String phoneNumber;

    @Size(max = 255)
    private String address;

    // Bidirectional relationship to Booking.
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<uk.ac.newcastle.enterprisemiddleware.booking.Booking> bookings = new ArrayList<>();

    public Customer() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<uk.ac.newcastle.enterprisemiddleware.booking.Booking> getBookings() {
        return bookings;
    }
    public void setBookings(List<uk.ac.newcastle.enterprisemiddleware.booking.Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
