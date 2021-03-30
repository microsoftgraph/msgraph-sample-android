<!-- markdownlint-disable MD002 MD041 -->

In this exercise you will incorporate the Microsoft Graph into the application. For this application, you will use the [Microsoft Graph SDK for Java](https://github.com/microsoftgraph/msgraph-sdk-java) to make calls to Microsoft Graph.

## Get calendar events from Outlook

In this section you will extend the `GraphHelper` class to add a function to get the user's events for the current week and update `CalendarFragment` to use these new functions.

1. Open **GraphHelper** and add the following `import` statements to the top of the file.

    ```java
    import com.microsoft.graph.options.Option;
    import com.microsoft.graph.options.HeaderOption;
    import com.microsoft.graph.options.QueryOption;
    import com.microsoft.graph.requests.EventCollectionPage;
    import com.microsoft.graph.requests.EventCollectionRequestBuilder;
    import java.time.ZonedDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.LinkedList;
    import java.util.List;
    import java.util.concurrent.CompletableFuture;
    ```

1. Add the following functions to the `GraphHelper` class.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/GraphHelper.java" id="GetEventsSnippet":::

    > [!NOTE]
    > Consider what the code in `getCalendarView` is doing.
    >
    > - The URL that will be called is `/v1.0/me/calendarview`.
    >   - The `startDateTime` and `endDateTime` query parameters define the start and end of the calendar view.
    >   - the `Prefer: outlook.timezone` header causes the Microsoft Graph to return the start and end times of each event in the user's time zone.
    >   - The `select` function limits the fields returned for each events to just those the view will actually use.
    >   - The `orderby` function sorts the results by start time.
    >   - The `top` function requests 25 results per page.
    > - The `processPage` function checks if there are more results available and requests additional pages if needed.

1. Right-click the **app/java/com.example.graphtutorial** folder and select **New**, then **Java Class**. Name the class `GraphToIana` and select **OK**.

1. Open the new file and replace its contents with the following.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/GraphToIana.java" id="GraphToIanaSnippet":::

1. Add the following `import` statements to the top of the **CalendarFragment** file.

    ```java
    import android.util.Log;
    import android.widget.ListView;
    import com.google.android.material.snackbar.BaseTransientBottomBar;
    import com.google.android.material.snackbar.Snackbar;
    import com.microsoft.graph.core.ClientException;
    import com.microsoft.graph.models.Event;
    import com.microsoft.identity.client.AuthenticationCallback;
    import com.microsoft.identity.client.IAuthenticationResult;
    import com.microsoft.identity.client.exception.MsalException;
    import java.time.DayOfWeek;
    import java.time.ZoneId;
    import java.time.ZonedDateTime;
    import java.time.temporal.ChronoUnit;
    import java.time.temporal.TemporalAdjusters;
    import java.util.List;
    ```

1. Add the following member to the `CalendarFragment` class.

    ```java
    private List<Event> mEventList = null;
    ```

1. Add the following functions to the `CalendarFragment` class to hide and show the progress bar.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/CalendarFragment.java" id="ProgressBarSnippet":::

1. Add the following function to output the event list for debugging purposes.

    ```java
    private void addEventsToList() {
        // Temporary for debugging
        String jsonEvents = GraphHelper.getInstance().serializeObject(mEventList);
        Log.d("GRAPH", jsonEvents);
    }
    ```

1. Replace the existing `onCreateView` function in the `CalendarFragment` class with the following.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/CalendarFragment.java" id="OnCreateViewSnippet":::

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

1. Open the **CalendarFragment** class and replace the existing `addEventsToList` function with the following.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/CalendarFragment.java" id="AddEventsToListSnippet":::

1. Run the app, sign in, and tap the **Calendar** navigation item. You should see the list of events.

    ![A screenshot of the table of events](./images/calendar-list.png)
