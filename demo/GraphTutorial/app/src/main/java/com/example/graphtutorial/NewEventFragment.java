// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.example.graphtutorial;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class NewEventFragment extends Fragment {
    private static final String TIME_ZONE = "timeZone";

    private String mTimeZone;

    // <InputsSnippet>
    private TextInputLayout mSubject;
    private TextInputLayout mAttendees;
    private TextInputLayout mStartInputLayout;
    private TextInputLayout mEndInputLayout;
    private TextInputLayout mBody;
    private EditTextDateTimePicker mStartPicker;
    private EditTextDateTimePicker mEndPicker;
    // </InputsSnippet>

    public NewEventFragment() {}

    public static NewEventFragment createInstance(String timeZone) {
        NewEventFragment fragment = new NewEventFragment();

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
        View newEventView = inflater.inflate(R.layout.fragment_new_event, container, false);

        ZoneId userTimeZone = GraphToIana.getZoneIdFromWindows(mTimeZone);

        mSubject = newEventView.findViewById(R.id.neweventsubject);
        mAttendees = newEventView.findViewById(R.id.neweventattendees);
        mBody = newEventView.findViewById(R.id.neweventbody);

        mStartInputLayout = newEventView.findViewById(R.id.neweventstartdatetime);
        mStartPicker = new EditTextDateTimePicker(getContext(),
            mStartInputLayout.getEditText(),
            userTimeZone);

        mEndInputLayout = newEventView.findViewById(R.id.neweventenddatetime);
        mEndPicker = new EditTextDateTimePicker(getContext(),
            mEndInputLayout.getEditText(),
            userTimeZone);

        Button createButton = newEventView.findViewById(R.id.createevent);
        createButton.setOnClickListener(v -> {
            // Clear any errors
            mSubject.setErrorEnabled(false);
            mEndInputLayout.setErrorEnabled(false);

            showProgressBar();

            createEvent();
        });

        return newEventView;
    }
    // </OnCreateViewSnippet>

    // <CreateEventSnippet>
    private void createEvent() {
        String subject = mSubject.getEditText().getText().toString();
        String attendees = mAttendees.getEditText().getText().toString();
        String body = mBody.getEditText().getText().toString();

        ZonedDateTime startDateTime = mStartPicker.getZonedDateTime();
        ZonedDateTime endDateTime = mEndPicker.getZonedDateTime();

        // Validate
        boolean isValid = true;
        // Subject is required
        if (subject.isEmpty()) {
            isValid = false;
            mSubject.setError("You must set a subject");
        }

        // End must be after start
        if (!endDateTime.isAfter(startDateTime)) {
            isValid = false;
            mEndInputLayout.setError("The end must be after the start");
        }

        if (isValid) {
            // Split the attendees string into an array
            String[] attendeeArray = attendees.split(";");

            GraphHelper.getInstance()
                .createEvent(subject,
                    startDateTime,
                    endDateTime,
                    mTimeZone,
                    attendeeArray,
                    body)
                .thenAccept(newEvent -> {
                    hideProgressBar();
                    Snackbar.make(getView(),
                        "Event created",
                        BaseTransientBottomBar.LENGTH_SHORT).show();
                })
                .exceptionally(exception -> {
                    hideProgressBar();
                    Log.e("GRAPH", "Error creating event", exception);
                    Snackbar.make(getView(),
                        exception.getMessage(),
                        BaseTransientBottomBar.LENGTH_LONG).show();
                    return null;
                });
        }
    }
    // </CreateEventSnippet>

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
}
