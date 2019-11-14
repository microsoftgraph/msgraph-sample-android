<!-- markdownlint-disable MD002 MD041 -->

In this exercise you will extend the application from the previous exercise to support authentication with Azure AD. This is required to obtain the necessary OAuth access token to call the Microsoft Graph. To do this, you will integrate the [Microsoft Authentication Library (MSAL) for Android](https://github.com/AzureAD/microsoft-authentication-library-for-android) into the application.

1. Right-click the **res** folder and select **New**, then **Android Resource Directory**.

1. Change the **Resource type** to `raw` and select **OK**.

1. Right-click the new **raw** folder and select **New**, then **File**.

1. Name the file `msal_config.json` and select **OK**.

1. Add the following to the **msal_config.json** file.

    ```json
    {
      "client_id" : "YOUR_APP_ID_HERE",
      "redirect_uri" : "msauth://YOUR_PACKAGE_NAME_HERE/callback",
      "broker_redirect_uri_registered": false,
      "account_mode": "SINGLE",
      "authorities" : [
        {
          "type": "AAD",
          "audience": {
            "type": "AzureADandPersonalMicrosoftAccount"
          },
          "default": true
        }
      ]
    }
    ```

    Replace `YOUR_APP_ID_HERE` with the app ID from your app registration, and replace `YOUR_PACKAGE_NAME_HERE` with your project's package name.

> [!IMPORTANT]
> If you're using source control such as git, now would be a good time to exclude the `msal_config.json` file from source control to avoid inadvertently leaking your app ID.

## Implement sign-in

In this section you will update the manifest to allow MSAL to use a browser to authenticate the user, register your redirect URI as being handled by the app, create an authentication helper class, and update the app to sign in and sign out.

1. Expand the **app/manifests** folder and open **AndroidManifest.xml**. Add the following elements above the `application` element.

    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    ```

    > [!NOTE]
    > These permissions are required in order for the MSAL library to authenticate the user.

1. Add the following element inside the `application` element, replacing the `YOUR_PACKAGE_NAME_HERE` string with your package name.

    ```xml
    <!--Intent filter to capture authorization code response from the default browser on the
        device calling back to the app after interactive sign in -->
    <activity
        android:name="com.microsoft.identity.client.BrowserTabActivity">
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data
                android:scheme="msauth"
                android:host="YOUR_PACKAGE_NAME_HERE"
                android:path="/callback" />
        </intent-filter>
    </activity>
    ```

1. Right-click the **app/java/com.example.graphtutorial** folder and select **New**, then **Java Class**. Name the class `AuthenticationHelper` and select **OK**.

1. Open the new file and replace its contents with the following.

    ```java
    package com.example.graphtutorial;

    import android.app.Activity;
    import android.content.Context;
    import android.util.Log;
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
        private String[] mScopes = { "User.Read", "Calendars.Read" };

        private AuthenticationHelper(Context ctx) {
            PublicClientApplication.createSingleAccountPublicClientApplication(ctx, R.raw.msal_config,
                new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        mPCA = application;
                    }

                    @Override
                    public void onError(MsalException exception) {
                        Log.e("AUTHHELPER", "Error creating MSAL application", exception);
                    }
                });
        }

        public static synchronized AuthenticationHelper getInstance(Context ctx) {
            if (INSTANCE == null) {
                INSTANCE = new AuthenticationHelper(ctx);
            }

            return INSTANCE;
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
    ```

1. Open **MainActivity** and add the following `import` statements.

    ```java
    import android.util.Log;

    import com.microsoft.identity.client.AuthenticationCallback;
    import com.microsoft.identity.client.IAuthenticationResult;
    import com.microsoft.identity.client.exception.MsalClientException;
    import com.microsoft.identity.client.exception.MsalException;
    import com.microsoft.identity.client.exception.MsalServiceException;
    import com.microsoft.identity.client.exception.MsalUiRequiredException;
    ```

1. Add the following member property to the `MainActivity` class.

    ```java
    private AuthenticationHelper mAuthHelper = null;
    ```

1. Add the following to the end of the `onCreate` function.

    ```java
    // Get the authentication helper
    mAuthHelper = AuthenticationHelper.getInstance(getApplicationContext());
    ```

1. Add the following functions to the `MainActivity` class.

    ```java
    // Silently sign in - used if there is already a
    // user account in the MSAL cache
    private void doSilentSignIn() {
        mAuthHelper.acquireTokenSilently(getAuthCallback());
    }

    // Prompt the user to sign in
    private void doInteractiveSignIn() {
        mAuthHelper.acquireTokenInteractively(this, getAuthCallback());
    }

    // Handles the authentication result
    public AuthenticationCallback getAuthCallback() {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                // Log the token for debug purposes
                String accessToken = authenticationResult.getAccessToken();
                Log.d("AUTH", String.format("Access token: %s", accessToken));
                hideProgressBar();

                setSignedInState(true);
                openHomeFragment(mUserName);
            }

            @Override
            public void onError(MsalException exception) {
                // Check the type of exception and handle appropriately
                if (exception instanceof MsalUiRequiredException) {
                    Log.d("AUTH", "Interactive login required");
                    doInteractiveSignIn();

                } else if (exception instanceof MsalClientException) {
                    if (exception.getErrorCode() == "no_current_account") {
                        Log.d("AUTH", "No current account, interactive login required");
                        doInteractiveSignIn();
                    } else {
                        // Exception inside MSAL, more info inside MsalError.java
                        Log.e("AUTH", "Client error authenticating", exception);
                    }
                } else if (exception instanceof MsalServiceException) {
                    // Exception when communicating with the auth server, likely config issue
                    Log.e("AUTH", "Service error authenticating", exception);
                }
                hideProgressBar();
            }

            @Override
            public void onCancel() {
                // User canceled the authentication
                Log.d("AUTH", "Authentication canceled");
                hideProgressBar();
            }
        };
    }
    ```

1. Replace the existing `signIn` and `signOut` functions with the following.

    ```java
    private void signIn() {
        showProgressBar();
        // Attempt silent sign in first
        // if this fails, the callback will handle doing
        // interactive sign in
        doSilentSignIn();
    }

    private void signOut() {
        mAuthHelper.signOut();

        setSignedInState(false);
        openHomeFragment(mUserName);
    }
    ```

    > [!NOTE]
    > Notice that the `signIn` method first checks if there is a user account already in the MSAL cache. If there is, it attempts to refresh its tokens silently, avoiding having to prompt the user every time they launch the app.

1. Save your changes and run the app.

1. When you tap the **Sign in** menu item, a browser opens to the Azure AD login page. Sign in with your account.

Once the app resumes, you should see an access token printed in the debug log in Android Studio.

![A screenshot of the Logcat window in Android Studio](./images/debugger-access-token.png)

## Get user details

In this section you will create a helper class to hold all of the calls to Microsoft Graph and update the `MainActivity` class to use this new class to get the logged-in user.

1. Right-click the **app/java/com.example.graphtutorial** folder and select **New**, then **Java Class**.

1. Name the class `GraphHelper` and select **OK**.

1. Open the new file and replace its contents with the following.

    ```java
    package com.example.graphtutorial;

    import com.microsoft.graph.authentication.IAuthenticationProvider;
    import com.microsoft.graph.concurrency.ICallback;
    import com.microsoft.graph.http.IHttpRequest;
    import com.microsoft.graph.models.extensions.IGraphServiceClient;
    import com.microsoft.graph.models.extensions.User;
    import com.microsoft.graph.requests.extensions.GraphServiceClient;

    // Singleton class - the app only needs a single instance
    // of the Graph client
    public class GraphHelper implements IAuthenticationProvider {
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
            mClient.me().buildRequest().get(callback);
        }
    }
    ```

    > [!NOTE]
    > Consider what this code does.
    >
    > - It implements the `IAuthenticationProvider` interface to insert the access token in the `Authorization` header on outgoing HTTP requests.
    > - It exposes a `getUser` function to get the logged-in user's information from the `/me` Graph endpoint.

1. Add the following `import` statements to the top of the **MainActivity** file.

    ```java
    import com.microsoft.graph.concurrency.ICallback;
    import com.microsoft.graph.core.ClientException;
    import com.microsoft.graph.models.extensions.IGraphServiceClient;
    import com.microsoft.graph.models.extensions.User;
    ```

1. Add the following function to the `MainActivity` class to generate an `ICallback` for the Graph call.

    ```java
    private ICallback<User> getUserCallback() {
        return new ICallback<User>() {
            @Override
            public void success(User user) {
                Log.d("AUTH", "User: " + user.displayName);

                mUserName = user.displayName;
                mUserEmail = user.mail == null ? user.userPrincipalName : user.mail;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();

                        setSignedInState(true);
                        openHomeFragment(mUserName);
                    }
                });

            }

            @Override
            public void failure(ClientException ex) {
                Log.e("AUTH", "Error getting /me", ex);
                mUserName = "ERROR";
                mUserEmail = "ERROR";

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();

                        setSignedInState(true);
                        openHomeFragment(mUserName);
                    }
                });
            }
        };
    }
    ```

1. Remove the following lines that set the user name and email:

    ```java
    // For testing
    mUserName = "Megan Bowen";
    mUserEmail = "meganb@contoso.com";
    ```

1. Replace the `onSuccess` override in the `AuthenticationCallback` with the following.

    ```java
    @Override
    public void onSuccess(AuthenticationResult authenticationResult) {
        // Log the token for debug purposes
        String accessToken = authenticationResult.getAccessToken();
        Log.d("AUTH", String.format("Access token: %s", accessToken));

        // Get Graph client and get user
        GraphHelper graphHelper = GraphHelper.getInstance();
        graphHelper.getUser(accessToken, getUserCallback());
    }
    ```

If you save your changes and run the app now, after sign-in the UI is updated with the user's display name and email address.
