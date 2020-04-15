<!-- markdownlint-disable MD002 MD041 -->

In this exercise you will incorporate the Microsoft Graph into the application. For this application, you will use the [Microsoft Graph SDK for Java](https://github.com/microsoftgraph/msgraph-sdk-java) to make calls to Microsoft Graph.

## Get calendar events from Outlook

In this section you will extend the `GraphHelper` class to add a function to get the user's events and update `CalendarFragment` to use these new functions.

1. Open the **GraphHelper** file and add the following `import` statements to the top of the file.

    ```java
    import com.microsoft.graph.options.Option;
    import com.microsoft.graph.options.QueryOption;
    import com.microsoft.graph.requests.extensions.IEventCollectionPage;
    import java.util.LinkedList;
    import java.util.List;
    ```

1. Add the following functions to the `GraphHelper` class.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/GraphHelper.java" id="GetEventsSnippet":::

    > [!NOTE]
    > Consider what the code in `getEvents` is doing.
    >
    > - The URL that will be called is `/v1.0/me/events`.
    > - The `select` function limits the fields returned for each events to just those the view will actually use.
    > - The `QueryOption` named `orderby` is used to sort the results by the date and time they were created, with the most recent item being first.

1. Add the following `import` statements to the top of the **CalendarFragment** file.

    ```java
    import android.util.Log;
    import android.widget.ListView;
    import com.microsoft.graph.concurrency.ICallback;
    import com.microsoft.graph.core.ClientException;
    import com.microsoft.graph.models.extensions.Event;
    import com.microsoft.graph.requests.extensions.IEventCollectionPage;
    import com.microsoft.identity.client.AuthenticationCallback;
    import com.microsoft.identity.client.IAuthenticationResult;
    import com.microsoft.identity.client.exception.MsalException;
    import java.util.List;
    ```

1. Add the following member to the `CalendarFragment` class.

    ```java
    private List<Event> mEventList = null;
    ```

1. Add the following functions to the `CalendarFragment` class to hide and show the progress bar.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/CalendarFragment.java" id="ProgressBarSnippet":::

1. Add the following function to provide a callback for the `getEvents` function in `GraphHelper`.

    ```java
    private ICallback<IEventCollectionPage> getEventsCallback() {
        return new ICallback<IEventCollectionPage>() {
            @Override
            public void success(IEventCollectionPage iEventCollectionPage) {
                mEventList = iEventCollectionPage.getCurrentPage();

                // Temporary for debugging
                String jsonEvents = GraphHelper.getInstance().serializeObject(mEventList);
                Log.d("GRAPH", jsonEvents);

                hideProgressBar();
            }

            @Override
            public void failure(ClientException ex) {
                Log.e("GRAPH", "Error getting events", ex);
                hideProgressBar();
            }
        };
    }
    ```

1. Replace the existing `onCreateView` function in the `CalendarFragment` class with the following.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/CalendarFragment.java" id="OnCreateViewSnippet":::

    Notice what this code does. First, it calls `acquireTokenSilently` to get the access token. Calling this method every time an access token is needed is a best practice because it takes advantage of MSAL's caching and token refresh abilities. Internally, MSAL checks for a cached token, then checks if it is expired. If the token is present and not expired, it just returns the cached token. If it is expired, it attempts to refresh the token before returning it.

    Once the token is retrieved, the code then calls the `getEvents` method to get the user's events.

1. Run the app, sign in, and tap the **Calendar** navigation item in the menu. You should see a JSON dump of the events in the debug log in Android Studio.

## Display the results

Now you can replace the JSON dump with something to display the results in a user-friendly manner. In this section, you will add a `ListView` to the calendar fragment, create a layout for each item in the `ListView`, and create a custom list adapter for the `ListView` that maps the fields of each `Event` to the appropriate `TextView` in the view.

1. Replace the `TextView` in **app/res/layout/fragment_calendar.xml** with a `ListView`.

    :::code language="xml" source="../demo/GraphTutorial/app/src/main/res/layout/fragment_calendar.xml" highlight="6-11":::

1. Right-click the **app/res/layout** folder and select **New**, then **Layout resource file**.

1. Name the file `event_list_item`, change the **Root element** to `RelativeLayout`, and select **OK**.

1. Open the **event_list_item.xml** file and replace its contents with the following.

    :::code language="xml" source="../demo/GraphTutorial/app/src/main/res/layout/event_list_item.xml":::

1. Right-click the **app/java/com.example.graphtutorial** folder and select **New**, then **Java Class**.

1. Name the class `EventListAdapter` and select **OK**.

1. Open the **EventListAdapter** file and replace its contents with the following.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/EventListAdapter.java" id="EventListAdapterSnippet":::

1. Open the **CalendarFragment** class and add the following function to the class.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/CalendarFragment.java" id="AddEventsToListSnippet":::

1. Replace the temporary debugging code from the `success` override with `addEventsToList();`.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/CalendarFragment.java" id="SuccessSnippet" highlight="5":::

1. Run the app, sign in, and tap the **Calendar** navigation item. You should see the list of events.

    ![A screenshot of the table of events](./images/calendar-list.png)
