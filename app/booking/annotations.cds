using TravelBookingService as service from '../../srv/services';

//	Booking List Page
annotate service.Bookings with @(UI : {
    SelectionFields : [
        bookingDate,
        status,
        traveler_ID,
        trip_ID
    ],

    LineItem : [
        {
            $Type : 'UI.DataField',
            Label : 'bookingDate',
            Value : bookingDate,
        },
        {
            $Type : 'UI.DataField',
            Label : 'status',
            Value : status,
        },
        {
            $Type : 'UI.DataField',
            Label : 'Traveler Email',
            Value : traveler.email,
        },
        {
            $Type : 'UI.DataField',
            Label : 'Trip title',
            Value : trip.title,
        }
    ]
}

);

//	Booking Object Page
annotate service.Bookings with @(UI: {
HeaderInfo        : {
        TypeName       : 'Booking',
        TypeNamePlural : 'Bookings',
        Title    : {Value : status}
    },

    Facets            : [
        {
            $Type  : 'UI.ReferenceFacet',
            Label  : 'BookingInformation',
            Target : '@UI.FieldGroup#BookingInformation'
        },
        {
            $Type: 'UI.ReferenceFacet',
            Label: 'Traveler Details',
            Target: 'traveler/@UI.FieldGroup#Traveler'
        },
        {
            $Type: 'UI.ReferenceFacet',
            Label: 'Trip Details',
            Target: 'trip/@UI.FieldGroup#Trip'
        },
        {
            $Type: 'UI.ReferenceFacet',
            Label: 'Insurance',
            Target: 'insurance/@UI.FieldGroup#Insurance'
        },
     ],

    FieldGroup #BookingInformation : {
        Data : [
            {
                $Type : 'UI.DataField',
                Label : 'Booking Date',
                Value : bookingDate
            },
            {
                $Type : 'UI.DataField',
                Label : 'Status',
                Value : status
            },
            {
                $Type : 'UI.DataField',
                Label : 'Traveler',
                Value : traveler_ID
            },
            {
                $Type : 'UI.DataField',
                Label : 'Trip',
                Value : trip_ID
            }
        ]},

});

// making sure insurance is not editable because it is generated automatically when a booking is created
annotate service.Insurances with {
    policyNumber @Common.FieldControl: #ReadOnly;
    provider @Common.FieldControl: #ReadOnly;
    insuranceType @Common.FieldControl: #ReadOnly;
    coverageAmount @Common.FieldControl: #ReadOnly;
    validFrom @Common.FieldControl: #ReadOnly;
    validTo @Common.FieldControl: #ReadOnly;
};

// fieldGroup to display insurance info at bookings object page
annotate service.Insurances with @(UI: {
    FieldGroup #Insurance: {
        Data: [
            {
                $Type : 'UI.DataField',
                Label : 'Policy Number',
                Value : policyNumber
            },
            {
                $Type : 'UI.DataField',
                Label : 'Provider',
                Value : provider
            },
            {
                $Type : 'UI.DataField',
                Label : 'Insurance Type',
                Value : insuranceType
            },
            {
                $Type : 'UI.DataField',
                Label : 'Coverage Amount',
                Value : coverageAmount
            },
            {
                $Type : 'UI.DataField',
                Label : 'Valid From',
                Value : validFrom
            },
            {
                $Type : 'UI.DataField',
                Label : 'Valid To',
                Value : validTo
            }
        ]
    }
}); 

annotate service.Travelers with {
    firstName @Common.FieldControl: #ReadOnly;
    lastName @Common.FieldControl: #ReadOnly;
    email @Common.FieldControl: #ReadOnly;
    phone @Common.FieldControl: #ReadOnly;
    passportNumber @Common.FieldControl: #ReadOnly;
};

annotate service.Trips with {
    title @Common.FieldControl: #ReadOnly;
    startDate @Common.FieldControl: #ReadOnly;
    endDate @Common.FieldControl: #ReadOnly;
    price @Common.FieldControl: #ReadOnly;
    availableSeats @Common.FieldControl: #ReadOnly;
    flight @Common.FieldControl: #ReadOnly;
    hotel @Common.FieldControl: #ReadOnly;
};

// FieldGroup to display traveler info at bookings object page
annotate service.Travelers with @(UI: {

    FieldGroup #Traveler: {
        Data: [
            {
                $Type : 'UI.DataField',
                Label : 'First Name',
                Value : firstName
            },
            {
                $Type : 'UI.DataField',
                Label : 'Last Name',
                Value : lastName
            },
            {
                $Type : 'UI.DataField',
                Label : 'Email',
                Value : email
            },
            {
                $Type : 'UI.DataField',
                Label : 'Phone',
                Value : phone
            },
            {
                $Type : 'UI.DataField',
                Label : 'Passport Number',
                Value : passportNumber
            }
        ]
    }

});

// fieldGroup to display trip info at bookings object page
annotate service.Trips with @(UI: {

    FieldGroup #Trip: {
        Data: [
            {
                $Type : 'UI.DataField',
                Label : 'Title',
                Value : title
            },
            {
                $Type : 'UI.DataField',
                Label : 'Start Date',
                Value : startDate
            },
            {
                $Type : 'UI.DataField',
                Label : 'End Date',
                Value : endDate
            },
            {
                $Type : 'UI.DataField',
                Label : 'Price',
                Value : price
            },
            {
                $Type : 'UI.DataField',
                Label : 'Flight',
                Value : flight
            },
            {
                $Type : 'UI.DataField',
                Label : 'Hotel',
                Value : hotel
            },
            {
                $Type : 'UI.DataField',
                Label : 'Available Spots',
                Value : availableSeats
            }
        ]
    }

});

annotate service.Bookings with {
    traveler @Common.ValueList : {
        $Type : 'Common.ValueListType',
        CollectionPath : 'Travelers',
        Parameters : [
            {
                $Type : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'firstName'
            },
            {
                $Type : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'lastName'
            },
            {
                $Type : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'email'
            }
        ]
    }
    @Common.ValueListWithFixedValues : true
};

annotate service.Bookings with {
    trip @Common.ValueList : {
        $Type : 'Common.ValueListType',
        CollectionPath : 'Trips',
        Parameters : [
            {
                $Type : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'title',
            },
            {
                $Type : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'startDate',
            },
            {
                $Type : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'endDate',
            },
            {
                $Type : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'price',
            },
        ],
    }
    @Common.ValueListWithFixedValues : true
};

// Travelers List Page
annotate service.Travelers with @(UI : {
    SelectionFields : [
        firstName,
        lastName,
        email
    ],

    LineItem : [
        {
            $Type : 'UI.DataField',
            Label : 'First Name',
            Value : firstName
        },
        {
            $Type : 'UI.DataField',
            Label : 'Last Name',
            Value : lastName
        },
        {
            $Type : 'UI.DataField',
            Label : 'Email',
            Value : email
        },
        {
            $Type : 'UI.DataField',
            Label : 'Phone',
            Value : phone
        },
        {
            $Type : 'UI.DataField',
            Label : 'Balance',
            Value : balance
        }
    ]
});

// Travelers Object Page
annotate service.Travelers with @(UI : {
    HeaderInfo : {
        TypeName : 'Traveler',
        TypeNamePlural : 'Travelers',
        Title : {
            Value : firstName
        },
        Description : {
            Value : email
        }
    },

    Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Traveler Information',
            Target : '@UI.FieldGroup#TravelerInformation'
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Bookings',
            Target : 'bookings/@UI.LineItem'
        }
    ],

    FieldGroup #TravelerInformation : {
        Data : [
            {
                $Type : 'UI.DataField',
                Label : 'First Name',
                Value : firstName
            },
            {
                $Type : 'UI.DataField',
                Label : 'Last Name',
                Value : lastName
            },
            {
                $Type : 'UI.DataField',
                Label : 'Email',
                Value : email
            },
            {
                $Type : 'UI.DataField',
                Label : 'Phone',
                Value : phone
            },
            {
                $Type : 'UI.DataField',
                Label : 'Passport Number',
                Value : passportNumber
            },
            {
                $Type : 'UI.DataField',
                Label : 'Balance',
                Value : balance
            }
        ]
    }

});

// List Page for Trips
annotate service.Trips with @(UI : {
SelectionFields : [
        startDate
    ],
    LineItem : [
        {
            $Type : 'UI.DataField',
            Label : 'Trip',
            Value : title
        }
    ]
});

// Object Page for Trips
annotate service.Trips with @(UI : {
  HeaderInfo : {
        TypeName : 'Trip',
        TypeNamePlural : 'Trips',
        Title : {
            Value : title
        }
    },

    Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Trip Information',
            Target : '@UI.FieldGroup#TripInformation'
        }
    ],

    FieldGroup #TripInformation : {
        Data : [
            {
                $Type : 'UI.DataField',
                Label : 'Trip',
                Value : title
            },
            {
                $Type : 'UI.DataField',
                Label : 'Start Date',
                Value : startDate
            },
            {
                $Type : 'UI.DataField',
                Label : 'End Date',
                Value : endDate
            },
            {
                $Type : 'UI.DataField',
                Label : 'Price',
                Value : price
            },
            {
                $Type : 'UI.DataField',
                Label : 'Available Seats',
                Value : availableSeats
            },
            {
                $Type : 'UI.DataField',
                Label : 'Flight',
                Value : flight
            },
            {
                $Type : 'UI.DataField',
                Label : 'Hotel',
                Value : hotel
            }
        ]
    }
});

// Insurances List Page
annotate service.Insurances with @(UI : {
   SelectionFields : [
        policyNumber,
        provider,
        insuranceType
    ],

    LineItem : [
        {
            $Type : 'UI.DataField',
            Label : 'Policy Number',
            Value : policyNumber
        },
        {
            $Type : 'UI.DataField',
            Label : 'Provider',
            Value : provider
        },
    ],
});

// Insurances Object Page
annotate service.Insurances with @(UI : {
 HeaderInfo : {
        TypeName : 'Insurance',
        TypeNamePlural : 'Insurances',
        Title : {
            Value : policyNumber
        },
        Description : {
            Value : provider
        }
    },

    Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Insurance Information',
            Target : '@UI.FieldGroup#InsuranceInformation'
        },

        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Booking Information',
            Target : 'booking/@UI.FieldGroup#BookingInformation'
        }
    ],


    FieldGroup #InsuranceInformation : {
        Data : [
            {
                $Type : 'UI.DataField',
                Label : 'Policy Number',
                Value : policyNumber
            },

            {
                $Type : 'UI.DataField',
                Label : 'Provider',
                Value : provider
            },

            {
                $Type : 'UI.DataField',
                Label : 'Insurance Type',
                Value : insuranceType
            },

            {
                $Type : 'UI.DataField',
                Label : 'Coverage Amount',
                Value : coverageAmount
            },

            {
                $Type : 'UI.DataField',
                Label : 'Valid From',
                Value : validFrom
            },

            {
                $Type : 'UI.DataField',
                Label : 'Valid To',
                Value : validTo
            }
        ]
    }

});

// Destinations List Page 
annotate service.Destinations with @(UI : {
    SelectionFields  : [
        country
    ],

    LineItem : [
        {
            $Type : 'UI.DataField',
            Label : 'Country',
            Value : country
        },
    ],
});

// Destinations Object Page
annotate service.Destinations with @(UI : {
    HeaderInfo : {
        TypeName : 'Destination',
        TypeNamePlural : 'Destinations',
        Title : {
            Value : city
        }
    },

    Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Destination Information',
            Target : '@UI.FieldGroup#DestinationInformation'
        }
    ],

    FieldGroup #DestinationInformation : {
        Data : [

            {
                $Type : 'UI.DataField',
                Label : 'Country',
                Value : country
            },

            {
                $Type : 'UI.DataField',
                Label : 'City',
                Value : city
            }

        ]
    }

});  