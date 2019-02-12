# Create an Android native application

In this demo you will create an Android application and wire up the different screens.

1. Open **Android Studio**
1. Select **Start a new Android Studio project**.
1. In the **Choose your project** dialog, under the **Phone and Tablet** tab, select **Basic Activity** and select **Next**:

    ![Screenshot selecting Basic Activity in the Choose your project dialog](../../Images/as-createproject-03.png)

1. In the **Configure your project** dialog, set the following values:
    * **Name:** NativeO365CalendarEvents
    * **Package name:** com.microsoft.nativeo365calendarevents
    * **Language:** Java
    * **Minimum API Level:** API 23: Android 6.0 (Marshmallow)

    ![Screenshot of the new project dialog in Android Studio](../../Images/as-createproject-01.png)

1. Add the necessary dependencies to the project:
    1. In the **Android** tool window, locate and open the file **Gradle Scripts > build.gradle (Module: app)**:

        ![Screenshot of the build.gradle file in the Android file explorer tool window](../../Images/as-configproject-01.png)

    1. Add the following implementations to the top of the existing `dependencies` section:

        ```gradle
        implementation 'com.google.code.gson:gson:2.8.2'
        implementation 'com.google.guava:guava:25.1-android'
        ```

    1. Sync the dependencies with the project by selecting **File > Sync Project with Gradle Files**.

### Create the Application User Interface

The first step is to create the shell of the user experience; creating a workable storyboard.

1. In the **Android** tool window, locate and open the file **app > res > layout > content_main.xml**.
1. At the bottom of the **content_main.xml** file, select the **Text** tab to switch to the code view of the layout:

    ![Screenshot of the content_main.xml file in code view](../../Images/as-create-ux-01.png)

1. Replace the entire contents of the **content_main.xml** file with the following markup:

    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="vertical"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/panel_signIn"
            android:paddingTop="60dp"
            android:visibility="visible"
            android:orientation="vertical">
            <Button
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Sign In"
                android:id="@+id/btn_signIn"
                android:layout_gravity="center"
                android:paddingTop="5dp"
                android:paddingBottom="5dp"
                android:paddingLeft="35dp"
                android:paddingRight="35dp"
                />
        </LinearLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/panel_loadEvent"
            android:paddingTop="60dp"
            android:visibility="gone"
            android:orientation="vertical">
            <Button
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Load Events"
                android:id="@+id/btn_loadEvent"
                android:layout_gravity="center"
                android:paddingTop="5dp"
                android:paddingBottom="5dp"
                android:paddingLeft="35dp"
                android:paddingRight="35dp"
              />
            <Button
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Sign out"
                android:id="@+id/btn_signOut"
                android:layout_gravity="center"
                android:paddingTop="5dp"
                android:paddingBottom="5dp"
                android:paddingLeft="35dp"
                android:paddingRight="35dp"
              />
        </LinearLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/panel_events"
            android:visibility="gone"
            android:orientation="vertical">
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:paddingTop="5dp"
                android:paddingBottom="5dp"
                android:paddingLeft="5dp"
                android:textSize="14sp"
                android:text="Events"/>
            <ListView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:id="@+id/list_events"
                android:padding="0dp"
                android:paddingLeft="0dp"
                android:paddingTop="0dp"
                android:paddingRight="0dp"
                android:paddingBottom="0dp" />
        </LinearLayout>
    </LinearLayout>
    ```

1. In the **Android** tool window, locate and open the file **app > java > com.microsoft.nativeo365calendarevents > MainActivity**.
    1. Add the following `import` statements to the existing `import` statements:

        ```java
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.Toast;
        import android.app.ProgressDialog;
        ```

    1. Add the following members to the `MainActivity` class:

        ```java
        private final static String TAG = MainActivity.class.getSimpleName();

        private ProgressDialog progress;
        private ListView listEvents;
        private LinearLayout panelSignIn;
        private LinearLayout panelEvents;
        private LinearLayout panelLoadEvent;
        ```

    1. Add the following methods to the `MainActivity` class:

        ```java
        private void onSignin() {
          Toast.makeText(MainActivity.this, "Hello <user>!", Toast.LENGTH_LONG).show();

          setPanelVisibility(false,true,false);
        }

        private void onSignout() {
          setPanelVisibility(true, false, false);
        }

        private void onLoadEvents() {
          Toast.makeText(MainActivity.this, 
            "Successfully loaded events from Office 365 calendar", 
            Toast.LENGTH_LONG
          ).show();
        }

        private void setPanelVisibility(Boolean showSignIn, Boolean showLoadEvents, Boolean showList) {
          panelSignIn.setVisibility(showSignIn ? View.VISIBLE : View.GONE);
          panelLoadEvent.setVisibility(showLoadEvents ? View.VISIBLE : View.GONE);
          panelEvents.setVisibility(showList ? View.VISIBLE : View.GONE);
        }
        ```

    1. Replace the existing code in the `onCreate()` method with the following code:

        ```java
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listEvents = findViewById(R.id.list_events);
        panelSignIn = findViewById(R.id.panel_signIn);
        panelEvents = findViewById(R.id.panel_events);
        panelLoadEvent = findViewById(R.id.panel_loadEvent);

        (findViewById(R.id.btn_signIn)).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            onSignin();
          }
        });

        (findViewById(R.id.btn_signOut)).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            onSignout();
          }
        });

        (findViewById(R.id.btn_loadEvent)).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            onLoadEvents();
          }
        });

        setPanelVisibility(true, false, false);
        ```

1. Test the user interface changes to ensure the flow off the application is working.

    Select **Run > Run 'app'**.

    In the **Select Deployment Target** dialog, select a device to target and then select **OK**:

    ![Screenshot selecting a connected device to test the application in a simulator](../../Images/as-create-ux-02.png).

    > If there are no **Connected Devices** available, select the **Create New Virtual Device** and select an option, such as the **Nexus 5X** as shown in the previous screenshot.

    1. After a moment the simulator will start and Android Studio will deploy and start the application.

        > If you get an error, it is likely an issue where the simulator didn't boot up fast enough for Android Studio. Simply stop the application in Android Studio (**Run > Stop 'app'**) but leave the simulator running. Then start the application again.

          ![Screenshot showing working application in the simulator](../../Images/android-demo-01.png)

        When the application loads, select the **Sign In** button. Notice the toast message displayed at the bottom of the screen:

          ![Screenshot showing working application in the simulator](../../Images/android-demo-02.png)

        Next, select the **Load Events** button. Notice the toast message displayed at the bottom of the screen:

          ![Screenshot showing working application in the simulator](../../Images/android-demo-03.png)
