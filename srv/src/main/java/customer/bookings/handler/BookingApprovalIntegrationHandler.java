package customer.bookings.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import cds.gen.travelbookingservice.Travelers;
import cds.gen.travelbookingservice.Trips;
import cds.gen.travelbookingservice.Destinations;
import cds.gen.travelbookingservice.Travelers_;
import cds.gen.travelbookingservice.Trips_;
import cds.gen.travelbookingservice.Destinations_;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import cds.gen.travelbookingservice.TravelBookingService_;
import cds.gen.travelbookingservice.Bookings;



@Component
@ServiceName(TravelBookingService_.CDS_NAME)
public class BookingApprovalIntegrationHandler implements EventHandler {
    private static final Logger log = LoggerFactory.getLogger(BookingApprovalIntegrationHandler.class);
    
    private final PersistenceService persistenceService;

    public BookingApprovalIntegrationHandler(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    private static final String DESTINATION_NAME = "MyIFlowDestination";
    private static final String IFLOW_PATH = "/travel/booking/";

     @After(event = "CREATE", entity = "TravelBookingService.Bookings")
    public void sendDataToIFlow(Bookings booking) {

        log.info("=== BookingApprovalIntegrationHandler triggered ===");
        log.info("Booking ID: {}", booking.getId());
        log.info("Booking status: {}", booking.getStatus());

         try {

            // Create JSON payload
            String jsonPayload = buildBookingApprovalPayload(booking);

            // Retrieve the destination configured in SAP BTP
            HttpDestination destination = DestinationAccessor.getDestination(DESTINATION_NAME).asHttp();

            // Fetch the preconfigured HttpClient from the Cloud SDK (handles tokens and proxies)
            var httpClient = HttpClientAccessor.getHttpClient(destination);

            // Define the specific iFlow endpoint suffix path/ Create POST request
            HttpPost httpPostRequest = new HttpPost(IFLOW_PATH);

            // Set Headers and Payload Body
            httpPostRequest.setHeader("Content-Type", "application/json");
            httpPostRequest.setEntity(new StringEntity(jsonPayload, "UTF-8"));

            // Execute the POST request
            HttpResponse response = httpClient.execute(httpPostRequest);
            
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

            if (statusCode >= 200 && statusCode < 300) {
                log.info("Successfully triggered iFlow. Response: {}", responseBody);
            } else {
                log.error("Failed to trigger iFlow. Status: {}, Error: {}", statusCode, responseBody);
            }

        } catch (Exception e) {
            log.error("Network or parsing error triggering iFlow: ", e);
            throw new RuntimeException("iFlow connection failure", e);
        }
    
    }

    private String buildBookingApprovalPayload(Bookings booking) {
// fetching travelers and trips/destinations data to send it to iflow, 
// so iflow will be able to fetch weather using latitude and longitude of specific city
        Travelers traveler = persistenceService.run(
                Select.from(Travelers_.class)
                .where(t -> t.ID().eq(booking.getTravelerId()))
        ).single(Travelers.class);


        Trips trip = persistenceService.run(
                Select.from(Trips_.class)
                .where(t -> t.ID().eq(booking.getTripId()))
        ).single(Trips.class);


        Destinations destination = persistenceService.run(
                Select.from(Destinations_.class)
                .where(d -> d.ID().eq(trip.getDestinationId()))
        ).single(Destinations.class);


        log.info("========== FETCHED DATA FROM OTHER ENTITIES TO SEND TO IFLOW ==========");

        log.info("Traveler ID: {}", traveler.getId());
        log.info("Traveler Email: {}", traveler.getEmail());

        log.info("Trip ID: {}", trip.getId());
        log.info("Trip Title: {}", trip.getTitle());
        log.info("Trip Start Date: {}", trip.getStartDate());
        log.info("Trip End Date: {}", trip.getEndDate());

        log.info("Destination ID: {}", destination.getId());
        log.info("Destination City: {}", destination.getCity());
        log.info("Destination Country: {}", destination.getCountry());


        Coordinates coordinates =
                getCoordinatesByCity(destination.getCity());

        log.info("Latitude: {}", coordinates.latitude());
        log.info("Longitude: {}", coordinates.longitude());
        log.info("============================================");

        return """
                {
              "bookingId": "%s",
              "status": "%s",
              "bookingDate": "%s",
              "travelerEmail": "%s",
              "tripTitle": "%s",
              "tripStartDate": "%s",
              "tripEndDate": "%s",
              "city": "%s",
              "latitude": "%s",
              "longitude": "%s"
            }
                """.formatted(
                booking.getId(),
                booking.getStatus(),
                booking.getBookingDate(),
                traveler.getEmail(),
                trip.getTitle(),
                trip.getStartDate(),
                trip.getEndDate(),
                destination.getCity(),
                coordinates.latitude(),
                coordinates.longitude()
                );
    }

    private record Coordinates(double latitude, double longitude) {}

    private Coordinates getCoordinatesByCity(String city) {

    return switch (city.toLowerCase()) {
        case "paris" -> new Coordinates(48.8566, 2.3522);
        case "tokyo" -> new Coordinates(35.6762, 139.6503);
        case "rome" -> new Coordinates(41.9028, 12.4964);
        case "dubai" -> new Coordinates(25.2048, 55.2708);
        case "barcelona" -> new Coordinates(41.3874, 2.1686);
        default -> throw new IllegalArgumentException(
                "Unsupported city for weather lookup: " + city
        );
    };
}


}