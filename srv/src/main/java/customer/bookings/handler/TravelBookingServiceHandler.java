package customer.bookings.handler;

import org.springframework.stereotype.Component;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.ql.Select;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.ql.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cds.gen.travelbookingservice.*;


@Component
@ServiceName(TravelBookingService_.CDS_NAME)
public class TravelBookingServiceHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(TravelBookingServiceHandler.class);

    private final PersistenceService db;

    public TravelBookingServiceHandler(PersistenceService db) {
        this.db = db;
    }
    
    /**
     * Validates that the selected trip has available seats
     * before allowing a booking to be created.
     */
    @Before(event = "CREATE", entity = "TravelBookingService.Bookings")
    public void validateTripAvailability(Bookings booking) {

         Trips trip = db.run(
            Select.from(Trips_.class)
            .where(t -> t.ID().eq(booking.getTripId()))
        ).single();   

        logger.info("Checking availability for trip: {}", trip.getTitle());

        Integer seats = trip.getAvailableSeats();
        logger.info("Available seats: {}", seats);

        if (seats == null || seats <= 0) {
            logger.warn(
                "Booking rejected. No available seats for trip {}",
                trip.getTitle()
            );

             throw new ServiceException("No available seats for this trip");
        }

        logger.info(
            "Trip {} has {} available seats",
            trip.getTitle(),
            trip.getAvailableSeats()
        );
    }

     /**
     * Decreases available seats after successful booking creation and updating trips available seats field.
     */
    @After(event = "CREATE", entity = "TravelBookingService.Bookings")
    public void updateAvailableSeats(Bookings booking) {

        Trips trip = db.run(
            Select.from(Trips_.class)
            .where(t -> t.ID().eq(booking.getTripId()))
        ).single();

        int remainingSeats = trip.getAvailableSeats() - 1;

        trip.setAvailableSeats(remainingSeats);

        db.run(
            Update.entity(Trips_.class)
            .data(trip)
        );

        logger.info(
            "Trip {} seats updated. Remaining seats: {}",
            trip.getTitle(),
            remainingSeats
        );
    }

}