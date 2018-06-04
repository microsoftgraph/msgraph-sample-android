## Extend the Android App for Azure AD Authentication

With the application created, now extend it to support authentication with Azure AD. This is required to obtain the necessary OAuth access token to call the Microsoft Graph. In this step you will integrate the Microsoft Authentication Library (MSAL) into the application.

> This demo builds off the final product from the previous demo.

1. Add the required MSAL dependencies to the project using Gradle:

    1. Open the **Gradle Scripts > build.gradle (Module: app)** file.
    1. Add the following code to the top of the `dependencies` section, immediately before the dependencies added in the previous section:

        ```gradle
        implementation('com.microsoft.identity.client:msal:0.1.3') {
            exclude group: 'com.android.support', module: 'appcompat-v7'
            exclude group: 'com.google.code.gson'
        }
        implementation 'com.android.volley:volley:1.0.0'
        ```

    1. Sync the dependencies with the project by selecting **File > Sync Project with Gradle Files**.

1. Configure the application with necessary permissions and configurations for MSAL.

    1. Open the **app > manifests > AndroidManifest.xml** file.
    1. Grant the application permissions to access the internet and network state from the Android platform by adding the following two lines immediately after the opening `<manifest>` element:

        ```xml
        <uses-permission android:name="android.permission.INTERNET" />
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        ```

    1. Add an activity for the application in the **AndroidManifest.xml** file to allow the MSAL library to use the browser for prompting the user to authenticate with Azure AD. Add the following after the existing `<activity>` element:

        ```xml
        <activity
            android:name="com.microsoft.identity.client.BrowserTabActivity">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="msal{{REPLACE_WITH_APP_ID}}"
                      android:host="auth" />
            </intent-filter>
        </activity>
        ```

    1. Replace the `{{REPLACE_WITH_APP_ID}}` in the markup above with the value of the Azure AD application copied when creating a the Azure AD application in a previous demo. Do not remove the **msal** prefix in the markup above.

      > The MSAL Android SDK does not require you to enter the redirect URI provided by the App Registration Portal. It can use the information provided in this step to dynamically generate the redirect URI for you.

1. Add an class to declare a new callback type that you will use:
    1. In the **Android** tool window, right-click the **app > java > com.microsoft.nativeo365calendarevents** and select **New > Java Class**:

        ![Screenshot adding a new Java class](./Images/as-create-aad-01.png)

    1. Name the class **Constants** and select **OK**.

        ```java
        public static final String[] SCOPES = {"openid", "User.Read", "Calendars.Read"};
        public static final String CLIENT_ID = "{{REPLACE_WITH_APP_ID}}";
        ```

    1. Replace the `{{REPLACE_WITH_APP_ID}}` in the markup above with the value of the Azure AD application copied when creating a the Azure AD application in a previous demo. Do not remove the **msal** prefix in the markup above.

1. Add an interface to declare a new callback type that you will use:
    1. In the **Android** tool window, right-click the **app > java > com.microsoft.nativeo365calendarevents** and select **New > Java Class**:
    1. Name the class **MSALAuthenticationCallback**, set the **Kind** to **Interface** and select **OK**.
    1. Add the following `import` statements to the existing `import` statements:

        ```java
        import com.microsoft.identity.client.AuthenticationResult;
        import com.microsoft.identity.client.MsalException;
        ```

    1. Add the following code to the `MSALAuthenticationCallback` interface:

        ```java
        void onMsalAuthSuccess(AuthenticationResult authenticationResult);
        void onMsalAuthError(MsalException exception);
        void onMsalAuthError(Exception exception);
        void onMsalAuthCancel();
        ```

1. Add an authentication helper class:
    1. In the **Android** tool window, right-click the **app > java > com.microsoft.nativeo365calendarevents** and select **New > Java Class**:
    1. Name the class **AuthenticationController** and select **OK**.
    1. Add the following `import` statements to the existing `import` statements:

        ```java
        import android.app.Activity;
        import android.content.Context;
        import android.util.Log;

        import com.microsoft.identity.client.AuthenticationCallback;
        import com.microsoft.identity.client.AuthenticationResult;
        import com.microsoft.identity.client.MsalException;
        import com.microsoft.identity.client.PublicClientApplication;
        ```

    1. Add the following members to the `AuthenticationController` class that will be used throughout this class:

        ```java
        private final String TAG = AuthenticationController.class.getSimpleName();
        private static AuthenticationController INSTANCE;
        private static PublicClientApplication mApplication;
        private AuthenticationResult mAuthResult;
        private static Context context;

        private MSALAuthenticationCallback mActivityCallback;
        ```

    1. Add the following code to the `AuthenticationController` class to implement a builder pattern:

        ```java
        private AuthenticationController() {
        }

        public static synchronized AuthenticationController getInstance(Context ctx) {
          context = ctx;

          if (INSTANCE == null) {
            INSTANCE = new AuthenticationController();
            if (mApplication == null) {
              mApplication = new PublicClientApplication(context, Constants.CLIENT_ID);
            }
          }
          return INSTANCE;
        }
        ```

    1. Add the following code to the `AuthenticationController` class to provide options for getting in instance of the Azure AD public client and access token:

        ```java
        public String getAccessToken() {
          return mAuthResult.getAccessToken();
        }

        public PublicClientApplication getPublicClient() {
          return mApplication;
        }
        ```

    1. Add the following code to the `AuthenticationController` class. One method (`doAcquireToken()`) will trigger the interactive authentication process with Azure AD. It passes an `AuthenticationCallback` that defines what happens after a successful, failed or cancelled authentication.

        ```java
        public void doAcquireToken(Activity activity, final MSALAuthenticationCallback msalCallback) {
          mActivityCallback = msalCallback;
          mApplication.acquireToken(activity, Constants.SCOPES, getAuthInteractiveCallback());
        }

        private AuthenticationCallback getAuthInteractiveCallback() {
          return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
              mAuthResult = authenticationResult;
              if (mActivityCallback != null) {
                mActivityCallback.onMsalAuthSuccess(mAuthResult);
              }
            }

            @Override
            public void onError(MsalException exception) {
              if (mActivityCallback != null) {
                mActivityCallback.onMsalAuthError(exception);
              }
            }

            @Override
            public void onCancel() {
              if (mActivityCallback != null) {
                mActivityCallback.onMsalAuthCancel();
              }
            }
          };
        }
        ```

1. The callback added in the last step calls the provided callback based on the results of the authentication prompt. While there are many ways to implement the callback, this application will implement it on the **MainActivity**.
    1. In the **Android** tool window, locate and open the file **app > java > com.microsoft.nativeo365calendarevents > MainActivity**.
    1. Update the `MainActivity` class to implement the `MSALAuthenticationCallback`:

        ```java
        public class MainActivity extends AppCompatActivity implements MSALAuthenticationCallback
        ```

    1. Add the following `import` statements to the existing `import` statements:

        ```java
        import android.content.Intent;
        import android.util.Log;

        import com.microsoft.identity.client.AuthenticationResult;
        import com.microsoft.identity.client.MsalException;
        import com.microsoft.identity.client.User;
        ```

    1. Add the following methods to implement the `MSALAuthenticationCallback` interface.

        ```java
        //region MSALAuthenticationCallback() implementation
        // these methods are called by the AuthenticationController
        @Override
        public void onMsalAuthSuccess(AuthenticationResult authenticationResult) {
          User user = authenticationResult.getUser();

          Toast.makeText(MainActivity.this, "Hello " + user.getName() 
            + " (" + user.getDisplayableId() + ")!", Toast.LENGTH_LONG
            ).show();

          setPanelVisibility(false, true, false);
        }

        @Override
        public void onMsalAuthError(MsalException exception) {
          Log.e(TAG, "Error authenticated", exception);
        }

        @Override
        public void onMsalAuthError(Exception exception) {
          Log.e(TAG, "Error authenticated", exception);
        }

        @Override
        public void onMsalAuthCancel() {
          Log.d(TAG, "Cancel authenticated");
        }
        //endregion
        ```

1. Wire up the signin button to the authentication process:
    1. Replace the contents of the `onSignin()` method to the following code:

        ```java
        private void onSignin() {
          AuthenticationController authController = AuthenticationController.getInstance(this);
          authController.doAcquireToken(this, this);
        }
        ```

1. Add the following method to the `MainActivity` class for the UI to update when there's an update to the activity, as in this case when the Android application receives control back from the browser that handled the authentication process:

    ```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (AuthenticationController.getInstance(this).getPublicClient() != null) {
        AuthenticationController.getInstance(this).getPublicClient().handleInteractiveRequestRedirect(requestCode, resultCode, data);
      }
    }
    ```

1. Test the user interface changes to ensure the flow off the application is working.
    1. Select **Run > Run 'app'**.
    1. In the **Select Deployment Target** dialog, select a device to target and then select **OK**.
    1. When the application loads in the simulator, select the **Signin** button.
    1. The application will load the Azure AD authentication page. Login with your Office 365 Azure AD credentials.

        ![Screenshot of the Azure AD login in the Android simulator](./Images/android-demo-04.png)

        After successfully logging in, you may be prompted to consent to the permissions requested by the application. If prompted, agree to the consent dialog.

    1. After completing the authentication and consent process, you will be taken back to the Android application where a toast message will appear with your Azure AD details in it.

        ![Screenshot of the Azure AD login in the Android simulator](./Images/android-demo-05.png)
