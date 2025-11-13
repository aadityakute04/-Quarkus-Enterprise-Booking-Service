package uk.ac.newcastle.enterprisemiddleware.travel;

import java.time.LocalDate;

public class TravelAgentRequest {
    public Long customerId;
    public Long hotelId;
    public Long taxiId;
    public Long flightId;
    public String bookingReference;
    public LocalDate startDate;
    public LocalDate endDate;
    public Double totalPrice;
}
