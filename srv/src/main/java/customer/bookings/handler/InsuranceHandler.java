package customer.bookings.handler;

import org.springframework.stereotype.Component;
import org.slf4j.LoggerFactory;

import com.sap.cds.ql.Select;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.ql.Update;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;

import cds.gen.travelbookingservice.*;

/**
 * Business logic handler responsible for automatic insurance generation.
 *
 * When a new Booking is created, this handler is triggered after the creation process.
 * It retrieves the selected Traveler information, generates a new insurance policy,
 * fills the required insurance details, and links the insurance record to the booking.
 *
 * This allows the system to automatically create insurance for every booking
 * without requiring the user to manually enter insurance information.
 */
@Component
@ServiceName(TravelBookingService_.CDS_NAME)
public class InsuranceHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(InsuranceHandler.class);

    private final PersistenceService persistence;

    public InsuranceHandler(PersistenceService persistence) {
        this.persistence = persistence;
    }


    @After(event = "CREATE", entity = "TravelBookingService.Bookings")
    public void generateInsurance(Bookings booking) {
        logger.info("=== After CREATE Booking, InsuranceHandler.java started ===");

        logger.info("Creating insurance for booking: {}", booking.getId());
        Insurances insurance = Insurances.create();


        Travelers traveler = persistence.run(
        Select.from(Travelers_.class)
        .where(t -> t.ID().eq(booking.getTravelerId()))
        ).single();

        logger.info("Traveler found: {} {}", 
            traveler.getFirstName(), 
            traveler.getLastName()
        );


        insurance.setPolicyNumber(
                "INS-" + UUID.randomUUID()
                .toString()
                .substring(0,8)
        );

        insurance.setProvider(
                "Travelex Insurance FOR: " + traveler.getFirstName() + " " + traveler.getLastName()
        );
        insurance.setInsuranceType("Standard");
        insurance.setCoverageAmount(BigDecimal.valueOf(100000));

        insurance.setValidFrom(
                booking.getBookingDate()
        );
        insurance.setValidTo(
                booking.getBookingDate().plusMonths(1)
        );

        insurance.setBookingId(
                booking.getId()
        );

        booking.setInsurance(insurance);

        persistence.run(
            Update.entity(Bookings_.class)
            .data(booking)
        );

        logger.info("Insurance {} successfully created for booking {}", 
            insurance.getPolicyNumber(),
            booking.getId()
        );

    }
}