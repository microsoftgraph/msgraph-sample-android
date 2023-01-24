// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.example.graphsample;

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
import java.util.Objects;

public class NewEventFragment extends Fragment {
    private static final String TIME_ZONE = "timeZone";

    private String mTimeZone;

    private TextInputLayout mSubject;
    private TextInputLayout mAttendees;
    private TextInputLayout mStartInputLayout;
    private TextInputLayout mEndInputLayout;
    private TextInputLayout mBody;
    private EditTextDateTimePicker mStartPicker;
    private EditTextDateTimePicker mEndPicker;

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
        String subject = Objects.requireNonNull(mSubject.getEditText()).getText().toString();
        String attendees = Objects.requireNonNull(mAttendees.getEditText()).getText().toString();
        String body = Objects.requireNonNull(mBody.getEditText()).getText().toString();

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
                        Snackbar.make(requireView(),
                                "Event created",
                                BaseTransientBottomBar.LENGTH_SHORT).show();
                    })
                    .exceptionally(exception -> {
                        hideProgressBar();
                        Log.e("GRAPH", "Error creating event", exception);
                        Snackbar.make(requireView(),
                                Objects.requireNonNull(exception.getMessage()),
                                BaseTransientBottomBar.LENGTH_LONG).show();
                        return null;
                    });
        }
    }
    // </CreateEventSnippet>

    // <ProgressBarSnippet>
    private void showProgressBar() {
        requireActivity().runOnUiThread(() -> {
            requireActivity().findViewById(R.id.progressbar)
                    .setVisibility(View.VISIBLE);
            requireActivity().findViewById(R.id.fragment_container)
                    .setVisibility(View.GONE);
        });
    }

    private void hideProgressBar() {
        requireActivity().runOnUiThread(() -> {
            requireActivity().findViewById(R.id.progressbar)
                    .setVisibility(View.GONE);
            requireActivity().findViewById(R.id.fragment_container)
                    .setVisibility(View.VISIBLE);
        });
    }
}

