// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.example.graphsample;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SignInParameters;
import com.microsoft.identity.client.exception.MsalException;

import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

// Singleton class - the app only needs a single instance
// of PublicClientApplication
public class AuthenticationHelper extends BaseAuthenticationProvider {
    private static AuthenticationHelper INSTANCE = null;
    private ISingleAccountPublicClientApplication mPCA = null;
    private final String[] mScopes = { "User.Read", "MailboxSettings.Read", "Calendars.ReadWrite" };

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
                        Log.e("AUTH_HELPER", "Error creating MSAL application", exception);
                        listener.onError(exception);
                    }
                });
    }

    public static synchronized CompletableFuture<AuthenticationHelper> getInstance(Context ctx) {

        if (INSTANCE == null) {
            CompletableFuture<AuthenticationHelper> future = new CompletableFuture<>();
            INSTANCE = new AuthenticationHelper(ctx, new IAuthenticationHelperCreatedListener() {
                @Override
                public void onCreated(AuthenticationHelper authHelper) {
                    future.complete(authHelper);
                }

                @Override
                public void onError(MsalException exception) {
                    future.completeExceptionally(exception);
                }
            });

            return future;
        } else {
            return CompletableFuture.completedFuture(INSTANCE);
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

    public CompletableFuture<IAuthenticationResult> acquireTokenInteractively(Activity activity) {
        CompletableFuture<IAuthenticationResult> future = new CompletableFuture<>();
        SignInParameters parameters = SignInParameters.builder()
                .withActivity(activity)
                .withScopes(Arrays.asList(mScopes))
                .withCallback(getAuthenticationCallback(future))
                .build();
        mPCA.signIn(parameters);

        return future;
    }

    public CompletableFuture<IAuthenticationResult> acquireTokenSilently() {
        // Get the authority from MSAL config
        String authority = mPCA.getConfiguration()
                .getDefaultAuthority().getAuthorityURL().toString();

        CompletableFuture<IAuthenticationResult> future = new CompletableFuture<>();

        //AcquireTokenSilentParameters parameters = new AcquireTokenSilentParameters.Builder()
        //        .fromAuthority(authority)
        //        .withScopes(Arrays.asList(mScopes))
        //        .withCallback(getAuthenticationCallback(future))
        //        .build();
        //mPCA.acquireTokenSilentAsync(parameters);

        // Currently the non-deprecated version has a bug
        // regarding account matching
        // https://github.com/AzureAD/microsoft-authentication-library-for-android/issues/1742
        //noinspection deprecation
        mPCA.acquireTokenSilentAsync(mScopes, authority, getAuthenticationCallback(future));
        return future;
    }

    public void signOut() {
        mPCA.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
            @Override
            public void onSignOut() {
                Log.d("AUTH_HELPER", "Signed out");
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                Log.d("AUTH_HELPER", "MSAL error signing out", exception);
            }
        });
    }

    private AuthenticationCallback getAuthenticationCallback(
            CompletableFuture<IAuthenticationResult> future) {
        return new AuthenticationCallback() {
            @Override
            public void onCancel() {
                future.cancel(true);
            }

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                future.complete(authenticationResult);
            }

            @Override
            public void onError(MsalException exception) {
                future.completeExceptionally(exception);
            }
        };
    }

    @NonNull
    @Override
    public CompletableFuture<String> getAuthorizationTokenAsync(@NonNull URL requestUrl) {
        if (shouldAuthenticateRequestWithUrl(requestUrl)) {
            return acquireTokenSilently()
                    .thenApply(IAuthenticationResult::getAccessToken);
        }

        return CompletableFuture.completedFuture(null);
    }
}

