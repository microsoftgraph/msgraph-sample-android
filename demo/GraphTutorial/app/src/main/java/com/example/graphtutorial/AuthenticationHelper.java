// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

// <AuthHelperSnippet>
package com.example.graphtutorial;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;

// Singleton class - the app only needs a single instance
// of PublicClientApplication
public class AuthenticationHelper {
    private static AuthenticationHelper INSTANCE = null;
    private ISingleAccountPublicClientApplication mPCA = null;
    private String[] mScopes = { "User.Read", "MailboxSettings.Read", "Calendars.ReadWrite" };

    private AuthenticationHelper(Context ctx, final IAuthenticationHelperCreatedListener listener) {
        PublicClientApplication.createSingleAccountPublicClientApplication(ctx, R.raw.msal_config,
                new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        mPCA = application;
                        listener.onCreated(INSTANCE);
                    }

                    @Override
                    public void onError(MsalException exception) {
                        Log.e("AUTHHELPER", "Error creating MSAL application", exception);
                        listener.onError(exception);
                    }
                });
    }

    public static synchronized void getInstance(Context ctx, IAuthenticationHelperCreatedListener listener) {
        if (INSTANCE == null) {
            INSTANCE = new AuthenticationHelper(ctx, listener);
        } else {
            listener.onCreated(INSTANCE);
        }
    }

    // Version called from fragments. Does not create an
    // instance if one doesn't exist
    public static synchronized AuthenticationHelper getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException(
                    "AuthenticationHelper has not been initialized from MainActivity");
        }

        return INSTANCE;
    }

    public void acquireTokenInteractively(Activity activity, AuthenticationCallback callback) {
        mPCA.signIn(activity, null, mScopes, callback);
    }

    public void acquireTokenSilently(AuthenticationCallback callback) {
        // Get the authority from MSAL config
        String authority = mPCA.getConfiguration().getDefaultAuthority().getAuthorityURL().toString();
        mPCA.acquireTokenSilentAsync(mScopes, authority, callback);
    }

    public void signOut() {
        mPCA.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
            @Override
            public void onSignOut() {
                Log.d("AUTHHELPER", "Signed out");
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                Log.d("AUTHHELPER", "MSAL error signing out", exception);
            }
        });
    }
}
// </AuthHelperSnippet>
