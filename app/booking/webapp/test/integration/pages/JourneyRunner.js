sap.ui.define([
    "sap/fe/test/JourneyRunner",
	"customer/bookings/booking/test/integration/pages/BookingsList.gen",
	"customer/bookings/booking/test/integration/pages/BookingsObjectPage.gen"
], function (JourneyRunner, BookingsListGenerated, BookingsObjectPageGenerated) {
    'use strict';

    var runner = new JourneyRunner({
        launchUrl: sap.ui.require.toUrl('customer/bookings/booking') + '/test/flpSandbox.html#customerbookingsbooking-tile',
        pages: {
			onTheBookingsListGenerated: BookingsListGenerated,
			onTheBookingsObjectPageGenerated: BookingsObjectPageGenerated
        },
        async: true
    });

    return runner;
});

