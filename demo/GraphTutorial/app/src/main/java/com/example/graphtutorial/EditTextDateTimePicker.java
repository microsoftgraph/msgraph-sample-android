// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

// <DateTimePickerSnippet>
package com.example.graphtutorial;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

// Class to wrap an EditText control to act as a date/time picker
// When the user taps it, a date picker is shown, followed by a time picker
// The values selected are combined to create a date/time value, which is then
// displayed in the EditText
public class EditTextDateTimePicker implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private Context mContext;
    private EditText mEditText;
    private ZonedDateTime mDateTime;

    EditTextDateTimePicker(Context context, EditText editText, ZoneId zoneId) {
        mContext = context;
        mEditText = editText;
        mEditText.setOnClickListener(this);

        // Initialize to now
        mDateTime = ZonedDateTime.now(zoneId).withSecond(0).withNano(0);

        // Round time to closest upcoming half-hour
        int offset = 30 - (mDateTime.getMinute() % 30);
        if (offset > 0) {
            mDateTime = mDateTime.plusMinutes(offset);
        }

        updateText();
    }

    @Override
    public void onClick(View v) {
        // First, show a date picker
        DatePickerDialog dialog = new DatePickerDialog(mContext,
            this,
            mDateTime.getYear(),
            mDateTime.getMonthValue(),
            mDateTime.getDayOfMonth());

        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Update the stored date/time with the new date
        mDateTime = mDateTime.withYear(year).withMonth(month).withDayOfMonth(dayOfMonth);

        // Show a time picker
        TimePickerDialog dialog = new TimePickerDialog(mContext,
            this,
            mDateTime.getHour(),
            mDateTime.getMinute(),
            false);

        dialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Update the stored date/time with the new time
        mDateTime = mDateTime.withHour(hourOfDay).withMinute(minute);
        // Update the text in the EditText
        updateText();
    }

    public ZonedDateTime getZonedDateTime() {
        return mDateTime;
    }

    private void updateText() {
        mEditText.setText(String.format("%s %s",
            mDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
            mDateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))));
    }
}
// </DateTimePickerSnippet>
