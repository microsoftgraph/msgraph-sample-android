// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.example.graphtutorial;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class CalendarFragment extends Fragment {
    private static final String TIME_ZONE = "timeZone";

    private String mTimeZone;
    private List<Event> mEventList = null;

    public CalendarFragment() {}

    public static CalendarFragment createInstance(String timeZone) {
        CalendarFragment fragment = new CalendarFragment();

        // Add the provided time zone to the fragment's arguments
        Bundle args = new Bundle();
        args.putString(TIME_ZONE, timeZone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTimeZone = getArguments().getString(TIME_ZONE);
        }
    }

    // <OnCreateViewSnippet>
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        showProgressBar();

        // Get a current access token
        AuthenticationHelper.getInstance()
                .acquireTokenSilently(new AuthenticationCallback() {
                    @Override
                    public void onSuccess(IAuthenticationResult authenticationResult) {
                        final GraphHelper graphHelper = GraphHelper.getInstance();

                        ZoneId tzId = GraphToIana.getZoneIdFromWindows(mTimeZone);
                        // Get midnight of the first day of the week (assumed Sunday)
                        // in the user's timezone, then convert to UTC
                        ZonedDateTime startOfWeek = ZonedDateTime.now(tzId)
                                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                                .truncatedTo(ChronoUnit.DAYS)
                                .withZoneSameInstant(ZoneId.of("UTC"));

                        // Add 7 days to get the end of the week
                        ZonedDateTime endOfWeek = startOfWeek.plusDays(7);

                        // Get the user's events
                        graphHelper.getCalendarView(authenticationResult.getAccessToken(),
                                startOfWeek,
                                endOfWeek,
                                mTimeZone,
                                getCalendarViewCallback());
                    }

                    @Override
                    public void onError(MsalException exception) {
                        Log.e("AUTH", "Could not get token silently", exception);
                        hideProgressBar();
                    }

                    @Override
                    public void onCancel() {
                        hideProgressBar();
                    }
                });

        return view;
    }
    // </OnCreateViewSnippet>

    // <ProgressBarSnippet>
    private void showProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().findViewById(R.id.progressbar)
                    .setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.fragment_container)
                    .setVisibility(View.GONE);
            }
        });
    }

    private void hideProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().findViewById(R.id.progressbar)
                    .setVisibility(View.GONE);
                getActivity().findViewById(R.id.fragment_container)
                    .setVisibility(View.VISIBLE);
            }
        });
    }
    // </ProgressBarSnippet>

    // <AddEventsToListSnippet>
    private void addEventsToList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView eventListView = getView().findViewById(R.id.eventlist);

                EventListAdapter listAdapter = new EventListAdapter(getActivity(),
                        R.layout.event_list_item, mEventList);

                eventListView.setAdapter(listAdapter);
            }
        });
    }
    // </AddEventsToListSnippet>

    private ICallback<List<Event>> getCalendarViewCallback() {
        return new ICallback<List<Event>>() {
            // <SuccessSnippet>
            @Override
            public void success(List<Event> eventList) {
                mEventList = eventList;

                addEventsToList();
                hideProgressBar();
            }
            // </SuccessSnippet>

            @Override
            public void failure(ClientException ex) {
                hideProgressBar();
                Log.e("GRAPH", "Error getting events", ex);
                Snackbar.make(getView(),
                    ex.getMessage(),
                    BaseTransientBottomBar.LENGTH_LONG).show();
            }
        };
    }
}
