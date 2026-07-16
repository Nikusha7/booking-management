namespace travel.booking;

using { cuid, managed } from '@sap/cds/common';

entity Travelers : cuid, managed 
{
    firstName : String;
    lastName : String;
    email : String;
    phone : String;
    passportNumber: String;
    balance : Decimal(10,2);

    bookings: Association to many Bookings on bookings.traveler = $self;    
}

entity Bookings : cuid, managed 
{
    bookingDate : Date @readonly default $now;
    status : Status default 'Pending';

    traveler : Association to Travelers not null;
    trip: Association to Trips not null;

    insurance: Composition of one Insurances on insurance.booking = $self;
}

type Status : String enum {
    Pending = 'Pending';
    Confirmed = 'Confirmed';
    Cancelled = 'Cancelled';
}

entity Insurances : cuid, managed 
{
    policyNumber : String;
    provider : String;
    insuranceType : String;
    coverageAmount : Decimal(10,2);

    validFrom : Date;
    validTo : Date;

    booking: Association to Bookings;
}

entity Trips : cuid, managed 
{   
    title: String;
    startDate : Date;
    endDate : Date;
    price : Decimal(10, 2);
    availableSeats : Integer;
    flight: String;
    hotel: String;
    
    destination : Association to Destinations;
}

entity Destinations : cuid, managed 
{
    country : String;
    city : String;
}