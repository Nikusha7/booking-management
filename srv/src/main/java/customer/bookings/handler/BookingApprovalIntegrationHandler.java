package customer.bookings.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;

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
    
    private static final String DESTINATION_NAME = "MyIFlowDestination";
    private static final String IFLOW_PATH = "/travel/booking/";

     @After(event = "CREATE", entity = "TravelBookingService.Bookings")
    public void sendDataToIFlow(Bookings booking) {

        log.info("=== BookingApprovalIntegrationHandler triggered ===");
        log.info("Booking ID: {}", booking.getId());
        log.info("Booking status: {}", booking.getStatus());

         try {

            // 1. Create JSON payload
            String jsonPayload = createPayload(booking);

            // 1. Retrieve the destination configured in SAP BTP
            HttpDestination destination = DestinationAccessor.getDestination(DESTINATION_NAME).asHttp();

            // 2. Fetch the preconfigured HttpClient from the Cloud SDK (handles tokens and proxies)
            var httpClient = HttpClientAccessor.getHttpClient(destination);

            // 3. Define the specific iFlow endpoint suffix path/ Create POST request
            HttpPost httpPostRequest = new HttpPost(IFLOW_PATH);

            // 4. Set Headers and Payload Body
            httpPostRequest.setHeader("Content-Type", "application/json");
            httpPostRequest.setEntity(new StringEntity(jsonPayload, "UTF-8"));

            // 5. Execute the POST request
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

    private String createPayload(Bookings booking) {

        return """
                {
                  "bookingId": "%s",
                  "status": "%s"
                }
                """.formatted(
                    booking.getId(),
                    booking.getStatus()
                );
    }


}