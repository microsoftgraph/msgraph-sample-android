// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.example.graphsample;

import com.microsoft.identity.client.exception.MsalException;

public interface IAuthenticationHelperCreatedListener {
    void onCreated(final AuthenticationHelper authHelper);
    void onError(final MsalException exception);
}

