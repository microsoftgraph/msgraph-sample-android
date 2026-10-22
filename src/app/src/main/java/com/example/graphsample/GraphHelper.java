// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.example.graphsample;

import androidx.annotation.NonNull;

import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.AttendeeType;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.EventCollectionResponse;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// Singleton class - the app only needs a single instance
// of the Graph client
public class GraphHelper {
    private static GraphHelper INSTANCE = null;
    private final GraphServiceClient mClient;

    private GraphHelper() {
        AuthenticationHelper authProvider = AuthenticationHelper.getInstance();

        mClient = new GraphServiceClient(authProvider);
    }

    public static synchronized GraphHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GraphHelper();
        }

        return INSTANCE;
    }

    public CompletableFuture<User> getUser() {
        // GET /me (logged in user)
        return CompletableFuture.supplyAsync(() -> mClient.me().get(requestConfiguration ->
                requestConfiguration.queryParameters.select = new String[] {
                        "displayName", "mail", "mailboxSettings", "userPrincipalName"
                }));
    }

    public CompletableFuture<List<Event>> getCalendarView(ZonedDateTime viewStart,
                                                          ZonedDateTime viewEnd,
                                                          String timeZone) {

        return CompletableFuture.supplyAsync(() -> {
            final List<Event> allEvents = new LinkedList<>();
            EventCollectionResponse eventPage = mClient.me().calendarView()
                    .get(requestConfiguration -> {
                        requestConfiguration.queryParameters.startDateTime =
                                viewStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        requestConfiguration.queryParameters.endDateTime =
                                viewEnd.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        requestConfiguration.queryParameters.select =
                                new String[] {"subject", "organizer", "start", "end"};
                        requestConfiguration.queryParameters.orderby =
                                new String[] {"start/dateTime"};
                        requestConfiguration.queryParameters.top = 5;
                        requestConfiguration.headers.add(
                                "Prefer", "outlook.timezone=\"" + timeZone + "\"");
                    });

            while (eventPage != null) {
                if (eventPage.getValue() != null) {
                    allEvents.addAll(eventPage.getValue());
                }

                String nextLink = eventPage.getOdataNextLink();
                if (nextLink == null) {
                    break;
                }

                eventPage = mClient.me().calendarView().withUrl(nextLink).get(requestConfiguration ->
                        requestConfiguration.headers.add(
                                "Prefer", "outlook.timezone=\"" + timeZone + "\""));
            }

            return allEvents;
        });
    }

    public CompletableFuture<Event> createEvent(String subject,
                                                @NonNull ZonedDateTime start,
                                                @NonNull ZonedDateTime end,
                                                String timeZone,
                                                @NonNull String[] attendees,
                                                String body) {
        Event newEvent = new Event();

        // Set properties on the event
        // Subject
        newEvent.setSubject(subject);

        // Start
        newEvent.setStart(new DateTimeTimeZone());
        // DateTimeTimeZone has two parts:
        // The date/time expressed as an ISO 8601 Local date/time
        // Local meaning there is no UTC or UTC offset designation
        // Example: 2020-01-12T09:00:00
        newEvent.getStart().setDateTime(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        // The time zone - can be either a Windows time zone name ("Pacific Standard Time")
        // or an IANA time zone identifier ("America/Los_Angeles")
        newEvent.getStart().setTimeZone(timeZone);

        // End
        newEvent.setEnd(new DateTimeTimeZone());
        newEvent.getEnd().setDateTime(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        newEvent.getEnd().setTimeZone(timeZone);

        // Add attendees if any were provided
        if (attendees.length > 0) {
            newEvent.setAttendees(new LinkedList<>());

            for (String attendeeEmail : attendees) {
                Attendee newAttendee = new Attendee();
                // Set the attendee type, in this case required
                newAttendee.setType(AttendeeType.REQUIRED);
                // Create a new EmailAddress object with the address
                // provided
                newAttendee.setEmailAddress(new EmailAddress());
                newAttendee.getEmailAddress().setAddress(attendeeEmail);

                newEvent.getAttendees().add(newAttendee);
            }
        }

        // Add body if provided
        if (!body.isEmpty()) {
            newEvent.setBody(new ItemBody());
            // Set the content
            newEvent.getBody().setContent(body);
            // Specify content is plain text
            newEvent.getBody().setContentType(BodyType.TEXT);
        }

        return CompletableFuture.supplyAsync(() -> mClient.me().events().post(newEvent));
    }
}
