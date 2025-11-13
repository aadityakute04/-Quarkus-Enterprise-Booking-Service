package uk.ac.newcastle.enterprisemiddleware.travel;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "travel_agent_booking")
public class TravelAgentBooking implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private String reference;

    // store remote booking ids (nullable until created)
    private Long hotelBookingId;
    private Long taxiBookingId;
    private Long flightBookingId;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status; // e.g. CREATING | CONFIRMED | FAILED | CANCELLED

    private Double totalPrice;

    // getters & setters
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public Long getCustomerId(){ return customerId; }
    public void setCustomerId(Long customerId){ this.customerId = customerId; }

    public String getReference(){ return reference; }
    public void setReference(String reference){ this.reference = reference; }

    public Long getHotelBookingId(){ return hotelBookingId; }
    public void setHotelBookingId(Long hotelBookingId){ this.hotelBookingId = hotelBookingId; }

    public Long getTaxiBookingId(){ return taxiBookingId; }
    public void setTaxiBookingId(Long taxiBookingId){ this.taxiBookingId = taxiBookingId; }

    public Long getFlightBookingId(){ return flightBookingId; }
    public void setFlightBookingId(Long flightBookingId){ this.flightBookingId = flightBookingId; }

    public LocalDate getStartDate(){ return startDate; }
    public void setStartDate(LocalDate startDate){ this.startDate = startDate; }

    public LocalDate getEndDate(){ return endDate; }
    public void setEndDate(LocalDate endDate){ this.endDate = endDate; }

    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status = status; }

    public Double getTotalPrice(){ return totalPrice; }
    public void setTotalPrice(Double totalPrice){ this.totalPrice = totalPrice; }
}
