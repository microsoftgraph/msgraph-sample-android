// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.example.graphtutorial;

import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.User;
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
}