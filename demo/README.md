# How to run the completed project

## Prerequisites

To run the completed project in this folder, you need the following:

- [Android Studio](https://developer.android.com/studio/) installed on your development machine. (**Note:** This tutorial was written with Android Studio version 4.1.3 and the Android 10.0 SDK. The steps in this guide may work with other versions, but that has not been tested.)
- Either a personal Microsoft account with a mailbox on Outlook.com, or a Microsoft work or school account.

If you don't have a Microsoft account, there are a couple of options to get a free account:

- You can [sign up for a new personal Microsoft account](https://signup.live.com/signup?wa=wsignin1.0&rpsnv=12&ct=1454618383&rver=6.4.6456.0&wp=MBI_SSL_SHARED&wreply=https://mail.live.com/default.aspx&id=64855&cbcxt=mai&bk=1454618383&uiflavor=web&uaid=b213a65b4fdc484382b6622b3ecaa547&mkt=E-US&lc=1033&lic=1).
- You can [sign up for the Office 365 Developer Program](https://developer.microsoft.com/office/dev-program) to get a free Office 365 subscription.

## Register an application with the Azure Portal

1. Open a browser and navigate to the [Azure Active Directory admin center](https://aad.portal.azure.com) and login using a **personal account** (aka: Microsoft Account) or **Work or School Account**.

1. Select **Azure Active Directory** in the left-hand navigation, then select **App registrations** under **Manage**.

    ![A screenshot of the App registrations ](../tutorial/images/aad-portal-app-registrations.png)

1. Select **New registration**. On the **Register an application** page, set the values as follows.

    - Set **Name** to `Android Graph Tutorial`.
    - Set **Supported account types** to **Accounts in any organizational directory and personal Microsoft accounts**.
    - Under **Redirect URI**, set the dropdown to **Public client/native (mobile & desktop)** and set the value to `msauth://YOUR_PACKAGE_NAME/callback`, replacing `YOUR_PACKAGE_NAME` with your project's package name.

    ![A screenshot of the Register an application page](../tutorial/images/aad-register-an-app.png)

1. Select **Register**. On the **Android Graph Tutorial** page, copy the value of the **Application (client) ID** and save it, you will need it in the next step.

    ![A screenshot of the application ID of the new app registration](../tutorial/images/aad-application-id.png)

## Configure the sample

1. Rename the `msal_config.json.example` file to `msal_config.json` and move the file into the `GraphTutorial/app/src/main/res/raw` directory.
1. Edit the `msal_config.json` file and make the following changes.
    1. Replace `YOUR_APP_ID_HERE` with the **Application Id** you got from the Azure portal.
    1. Replace `com.example.graphtutorial` with your package name.

## Run the sample

In Android Studio, select **Run 'app'** on the **Run** menu.
