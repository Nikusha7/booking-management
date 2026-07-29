using travel.booking as db from '../db/schema';

service TravelBookingService {

    @odata.draft.enabled
    entity Travelers as projection on db.Travelers;

    @odata.draft.enabled
    @restrict: [
    {
        grant: 'READ',
        to: 'viewer'
    },
    {
        grant: '*',
        to: 'admin'
    }
    ]
    entity Bookings as projection on db.Bookings;

    entity Insurances as projection on db.Insurances;

    entity Trips as projection on db.Trips;

    @readonly entity Destinations as projection on db.Destinations;
}
