// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.example.graphtutorial;

import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.models.extensions.Attendee;
import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.EmailAddress;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.ItemBody;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.models.generated.AttendeeType;
import com.microsoft.graph.models.generated.BodyType;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.IEventCollectionPage;
import com.microsoft.graph.requests.extensions.IEventCollectionRequestBuilder;
import com.microsoft.graph.requests.extensions.GraphServiceClient;

import java.util.LinkedList;
import java.util.List;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// Singleton class - the app only needs a single instance
// of the Graph client
// Add suppression here because IAuthenticationProvider
// has been marked deprecated, but is still the type expected
// by the GraphServiceClient
@SuppressWarnings( "deprecation" )
public class GraphHelper implements com.microsoft.graph.authentication.IAuthenticationProvider {
    private static GraphHelper INSTANCE = null;
    private IGraphServiceClient mClient = null;
    private String mAccessToken = null;

    private GraphHelper() {
        mClient = GraphServiceClient.builder()
                .authenticationProvider(this).buildClient();
    }

    public static synchronized GraphHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GraphHelper();
        }

        return INSTANCE;
    }

    // Part of the Graph IAuthenticationProvider interface
    // This method is called before sending the HTTP request
    @Override
    public void authenticateRequest(IHttpRequest request) {
        // Add the access token in the Authorization header
        request.addHeader("Authorization", "Bearer " + mAccessToken);
    }

    public void getUser(String accessToken, ICallback<User> callback) {
        mAccessToken = accessToken;

        // GET /me (logged in user)
        mClient.me().buildRequest()
                .select("displayName,mail,mailboxSettings,userPrincipalName")
                .get(callback);
    }

    // <GetEventsSnippet>
    public void getCalendarView(String accessToken,
                                ZonedDateTime viewStart,
                                ZonedDateTime viewEnd,
                                String timeZone,
                                final ICallback<List<Event>> callback) {
        mAccessToken = accessToken;

        final List<Option> options = new LinkedList<Option>();
        options.add(new QueryOption("startDateTime", viewStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        options.add(new QueryOption("endDateTime", viewEnd.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        // Start and end times adjusted to user's time zone
        options.add(new HeaderOption("Prefer", "outlook.timezone=\"" + timeZone + "\""));

        final List<Event> allEvents = new LinkedList<Event>();
        // Create a separate list of options for the paging requests
        // paging request should not include the query parameters from the initial
        // request, but should include the headers.
        final List<Option> pagingOptions = new LinkedList<Option>();
        pagingOptions.add(new HeaderOption("Prefer", "outlook.timezone=\"" + timeZone + "\""));

        final ICallback<IEventCollectionPage> pagingCallback = new ICallback<IEventCollectionPage>() {
            @Override
            public void success(IEventCollectionPage eventCollectionPage) {
                allEvents.addAll(eventCollectionPage.getCurrentPage());

                IEventCollectionRequestBuilder nextPage =
                        eventCollectionPage.getNextPage();

                if (nextPage == null) {
                    callback.success(allEvents);
                }
                else{
                    nextPage.buildRequest(pagingOptions)
                            .get(this);
                }
            }

            @Override
            public void failure(ClientException ex) {
                callback.failure(ex);
            }
        };

        mClient.me().calendarView().buildRequest(options)
                .select("subject,organizer,start,end")
                .orderBy("start/dateTime")
                .top(25)
                .get(pagingCallback);
    }

    // Debug function to get the JSON representation of a Graph
    // object
    public String serializeObject(Object object) {
        return mClient.getSerializer().serializeObject(object);
    }
    // </GetEventsSnippet>

    // <CreateEventSnippet>
    public void createEvent(String accessToken,
                            String subject,
                            ZonedDateTime start,
                            ZonedDateTime end,
                            String timeZone,
                            String[] attendees,
                            String body,
                            final ICallback<Event> callback) {
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

        mClient.me().events().buildRequest()
                .post(newEvent, callback);
    }
    // </CreateEventSnippet>
}
