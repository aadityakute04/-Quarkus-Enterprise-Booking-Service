package uk.ac.newcastle.enterprisemiddleware.travel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * Service that orchestrates bookings across Hotel, Taxi and Flight services.
 * Creates bookings in sequence (hotel -> taxi -> flight). If any step fails,
 * attempts to cancel previously-created remote bookings (compensation).
 */
@ApplicationScoped
public class TravelAgentService {

    private static final Logger LOG = Logger.getLogger(TravelAgentService.class);

    @Inject
    TravelAgentBookingRepository repo;

    @Inject @RestClient
    HotelClient hotelClient;

    @Inject @RestClient
    TaxiClient taxiClient;

    @Inject @RestClient
    FlightClient flightClient;

    /**
     * Create an aggregate booking composed of hotel, taxi and flight.
     * Transactional for local persistence; remote calls are managed manually.
     */
    @Transactional
    public TravelAgentBooking createAggregateBooking(TravelAgentRequest req) {
        TravelAgentBooking tab = new TravelAgentBooking();
        tab.setCustomerId(req.customerId);
        tab.setReference(req.bookingReference);
        tab.setStartDate(req.startDate);
        tab.setEndDate(req.endDate);
        tab.setStatus("CREATING");
        tab.setTotalPrice(req.totalPrice);

        // persist initial local aggregate so we have a local id
        repo.persist(tab);

        Long hotelRemoteId = null;
        Long taxiRemoteId = null;
        Long flightRemoteId = null;

        try {
            HotelClient.RemoteBookingRequest hReq = new HotelClient.RemoteBookingRequest();
            hReq.customerId = req.customerId;
            hReq.hotelId = req.hotelId;
            hReq.bookingReference = req.bookingReference;
            hReq.startDate = req.startDate == null ? null : req.startDate.toString();
            hReq.endDate = req.endDate == null ? null : req.endDate.toString();
            hReq.totalPrice = req.totalPrice;

            RemoteBooking hBooking = hotelClient.createBooking(hReq);
            hotelRemoteId = (hBooking != null) ? hBooking.id : null;
            tab.setHotelBookingId(hotelRemoteId);
            repo.update(tab);

            TaxiClient.RemoteBookingRequest tReq = new TaxiClient.RemoteBookingRequest();
            tReq.customerId = req.customerId;
            tReq.taxiId = req.taxiId;
            tReq.bookingReference = req.bookingReference;
            tReq.startDate = req.startDate == null ? null : req.startDate.toString();
            tReq.endDate = req.endDate == null ? null : req.endDate.toString();
            tReq.totalPrice = req.totalPrice;

            RemoteBooking tBooking = taxiClient.createBooking(tReq);
            taxiRemoteId = (tBooking != null) ? tBooking.id : null;
            tab.setTaxiBookingId(taxiRemoteId);
            repo.update(tab);

            FlightClient.RemoteBookingRequest fReq = new FlightClient.RemoteBookingRequest();
            fReq.customerId = req.customerId;
            fReq.flightId = req.flightId;
            fReq.bookingReference = req.bookingReference;
            fReq.startDate = req.startDate == null ? null : req.startDate.toString();
            fReq.endDate = req.endDate == null ? null : req.endDate.toString();
            fReq.totalPrice = req.totalPrice;

            RemoteBooking fBooking = flightClient.createBooking(fReq);
            flightRemoteId = (fBooking != null) ? fBooking.id : null;
            tab.setFlightBookingId(flightRemoteId);

            tab.setStatus("CONFIRMED");
            repo.update(tab);
            return tab;

        } catch (Exception e) {
            LOG.error("Aggregate booking failed: " + e.getMessage(), e);

            try {
                if (flightRemoteId != null) {
                    flightClient.cancelBooking(flightRemoteId);
                }
            } catch (Exception ex) {
                LOG.warn("Failed to cancel flight remote booking " + flightRemoteId, ex);
            }
            try {
                if (taxiRemoteId != null) {
                    taxiClient.cancelBooking(taxiRemoteId);
                }
            } catch (Exception ex) {
                LOG.warn("Failed to cancel taxi remote booking " + taxiRemoteId, ex);
            }
            try {
                if (hotelRemoteId != null) {
                    hotelClient.cancelBooking(hotelRemoteId);
                }
            } catch (Exception ex) {
                LOG.warn("Failed to cancel hotel remote booking " + hotelRemoteId, ex);
            }

            tab.setStatus("FAILED");
            repo.update(tab);

            throw new RuntimeException("Aggregate booking failed and compensation attempted: " + e.getMessage(), e);
        }
    }
}
