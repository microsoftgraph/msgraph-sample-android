<!-- markdownlint-disable MD002 MD041 -->

In this exercise you will extend the application from the previous exercise to support authentication with Azure AD. This is required to obtain the necessary OAuth access token to call the Microsoft Graph. To do this, you will integrate the [Microsoft Authentication Library (MSAL) for Android](https://github.com/AzureAD/microsoft-authentication-library-for-android) into the application.

1. Right-click the **res** folder and select **New**, then **Android Resource Directory**.

1. Change the **Resource type** to `raw` and select **OK**.

1. Right-click the new **raw** folder and select **New**, then **File**.

1. Name the file `msal_config.json` and select **OK**.

1. Add the following to the **msal_config.json** file.

    :::code language="json" source="../demo/GraphTutorial/msal_config.json.example":::

1. Replace `YOUR_APP_ID_HERE` with the app ID from your app registration, and replace `com.example.graphtutorial` with your project's package name.

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

1. Right-click the **app/java/com.example.graphtutorial** folder and select **New**, then **Java Class**. Change the **Kind** to **Interface**. Name the interface `IAuthenticationHelperCreatedListener` and select **OK**.

1. Open the new file and replace its contents with the following.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/IAuthenticationHelperCreatedListener.java" id="ListenerSnippet":::

1. Right-click the **app/java/com.example.graphtutorial** folder and select **New**, then **Java Class**. Name the class `AuthenticationHelper` and select **OK**.

1. Open the new file and replace its contents with the following.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/AuthenticationHelper.java" id="AuthHelperSnippet":::

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

1. Add the following member properties to the `MainActivity` class.

    ```java
    private AuthenticationHelper mAuthHelper = null;
    private boolean mAttemptInteractiveSignIn = false;
    ```

1. Add the following code to the end of the `onCreate` function.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/MainActivity.java" id="InitialLoginSnippet":::

1. Add the following functions to the `MainActivity` class.

    ```java
    // Silently sign in - used if there is already a
    // user account in the MSAL cache
    private void doSilentSignIn(boolean shouldAttemptInteractive) {
        mAttemptInteractiveSignIn = shouldAttemptInteractive;
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
                    if (mAttemptInteractiveSignIn) {
                        doInteractiveSignIn();
                    }
                } else if (exception instanceof MsalClientException) {
                    if (exception.getErrorCode() == "no_current_account" ||
                        exception.getErrorCode() == "no_account_found") {
                        Log.d("AUTH", "No current account, interactive login required");
                        if (mAttemptInteractiveSignIn) {
                            doInteractiveSignIn();
                        }
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

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/MainActivity.java" id="SignInAndOutSnippet":::

    > [!NOTE]
    > Notice that the `signIn` method does a silent sign-in (via `doSilentSignIn`). The callback for this method will do an interactive sign-in if the silent one fails. This avoids having to prompt the user every time they launch the app.

1. Save your changes and run the app.

1. When you tap the **Sign in** menu item, a browser opens to the Azure AD login page. Sign in with your account.

Once the app resumes, you should see an access token printed in the debug log in Android Studio.

![A screenshot of the Logcat window in Android Studio](./images/debugger-access-token.png)

## Get user details

In this section you will create a helper class to hold all of the calls to Microsoft Graph and update the `MainActivity` class to use this new class to get the logged-in user.

1. Right-click the **app/java/com.example.graphtutorial** folder and select **New**, then **Java Class**. Name the class `GraphHelper` and select **OK**.

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
            mClient.me().buildRequest()
                    .select("displayName,mail,mailboxSettings,userPrincipalName")
                    .get(callback);
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
    import com.microsoft.graph.models.extensions.User;
    ```

1. Add the following function to the `MainActivity` class to generate an `ICallback` for the Graph call.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/MainActivity.java" id="GetUserCallbackSnippet":::

1. Remove the following lines that set the user name and email:

    ```java
    // For testing
    mUserName = "Megan Bowen";
    mUserEmail = "meganb@contoso.com";
    ```

1. Replace the `onSuccess` override in the `AuthenticationCallback` with the following.

    :::code language="java" source="../demo/GraphTutorial/app/src/main/java/com/example/graphtutorial/MainActivity.java" id="OnSuccessSnippet":::

1. Save your changes and run the app. After sign-in the UI is updated with the user's display name and email address.
