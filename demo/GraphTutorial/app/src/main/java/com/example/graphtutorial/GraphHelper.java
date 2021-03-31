// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.example.graphtutorial;

import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.AttendeeType;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.EventCollectionPage;
import com.microsoft.graph.requests.EventCollectionRequestBuilder;
import com.microsoft.graph.requests.GraphServiceClient;

import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.LinkedList;
import java.util.List;

// Singleton class - the app only needs a single instance
// of the Graph client
public class GraphHelper {
    private static GraphHelper INSTANCE = null;
    private GraphServiceClient mClient = null;

    private GraphHelper() {

        AuthenticationHelper authProvider = AuthenticationHelper.getInstance();

        mClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider).buildClient();
    }

    public static synchronized GraphHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GraphHelper();
        }

        return INSTANCE;
    }

    public CompletableFuture<User> getUser() {
        // GET /me (logged in user)
        return mClient.me().buildRequest()
            .select("displayName,mail,mailboxSettings,userPrincipalName")
            .getAsync();
    }

    // <GetEventsSnippet>
    public CompletableFuture<List<Event>> getCalendarView(ZonedDateTime viewStart,
                                                          ZonedDateTime viewEnd,
                                                          String timeZone) {

        final List<Option> options = new LinkedList<Option>();
        options.add(new QueryOption("startDateTime",
            viewStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        options.add(new QueryOption("endDateTime",
            viewEnd.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        // Start and end times adjusted to user's time zone
        options.add(new HeaderOption("Prefer",
            "outlook.timezone=\"" + timeZone + "\""));

        final List<Event> allEvents = new LinkedList<Event>();
        // Create a separate list of options for the paging requests
        // paging request should not include the query parameters from the initial
        // request, but should include the headers.
        final List<Option> pagingOptions = new LinkedList<Option>();
        pagingOptions.add(new HeaderOption("Prefer",
            "outlook.timezone=\"" + timeZone + "\""));

        return mClient.me().calendarView()
            .buildRequest(options)
            .select("subject,organizer,start,end")
            .orderBy("start/dateTime")
            .top(5)
            .getAsync()
            .thenCompose(eventPage -> processPage(eventPage, allEvents, pagingOptions));
    }

    private CompletableFuture<List<Event>> processPage(EventCollectionPage currentPage,
                             List<Event> eventList,
                             List<Option> options) {
        eventList.addAll(currentPage.getCurrentPage());

        // Check if there is another page of results
        EventCollectionRequestBuilder nextPage = currentPage.getNextPage();
        if (nextPage != null) {
            // Request the next page and repeat
            return nextPage.buildRequest(options)
                .getAsync()
                .thenCompose(eventPage -> processPage(eventPage, eventList, options));
        } else {
            // No more pages, complete the future
            // with the complete list
            return CompletableFuture.completedFuture(eventList);
        }
    }

    // Debug function to get the JSON representation of a Graph
    // object
    public String serializeObject(Object object) {
        return mClient.getSerializer().serializeObject(object);
    }
    // </GetEventsSnippet>

    // <CreateEventSnippet>
    public CompletableFuture<Event> createEvent(String subject,
                                                ZonedDateTime start,
                                                ZonedDateTime end,
                                                String timeZone,
                                                String[] attendees,
                                                String body) {
        Event newEvent = new Event();

        // Set properties on the event
        // Subject
        newEvent.subject = subject;

        // Start
        newEvent.start = new DateTimeTimeZone();
        // DateTimeTimeZone has two parts:
        // The date/time expressed as an ISO 8601 Local date/time
        // Local meaning there is no UTC or UTC offset designation
        // Example: 2020-01-12T09:00:00
        newEvent.start.dateTime = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        // The time zone - can be either a Windows time zone name ("Pacific Standard Time")
        // or an IANA time zone identifier ("America/Los_Angeles")
        newEvent.start.timeZone = timeZone;

        // End
        newEvent.end = new DateTimeTimeZone();
        newEvent.end.dateTime = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        newEvent.end.timeZone = timeZone;

        // Add attendees if any were provided
        if (attendees.length > 0) {
            newEvent.attendees = new LinkedList<>();

            for (String attendeeEmail : attendees) {
                Attendee newAttendee = new Attendee();
                // Set the attendee type, in this case required
                newAttendee.type = AttendeeType.REQUIRED;
                // Create a new EmailAddress object with the address
                // provided
                newAttendee.emailAddress = new EmailAddress();
                newAttendee.emailAddress.address = attendeeEmail;

                newEvent.attendees.add(newAttendee);
            }
        }

        // Add body if provided
        if (!body.isEmpty()) {
            newEvent.body = new ItemBody();
            // Set the content
            newEvent.body.content = body;
            // Specify content is plain text
            newEvent.body.contentType = BodyType.TEXT;
        }

        return mClient.me().events().buildRequest()
                .postAsync(newEvent);
    }
    // </CreateEventSnippet>
}
