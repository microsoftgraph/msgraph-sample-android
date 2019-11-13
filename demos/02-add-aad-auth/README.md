# Completed module: Add Azure AD authentication

The version of the project in this directory reflects completing the tutorial up through [Add Azure AD authentication](https://docs.microsoft.com/graph/tutorials/android?tutorial-step=3). If you use this version of the project, you need to complete the rest of the tutorial starting at [Get calendar data](https://docs.microsoft.com/graph/tutorials/android?tutorial-step=4).

> **Note:** It is assumed that you have already registered an application in the Azure portal as specified in [Register the app in the portal](https://docs.microsoft.com/graph/tutorials/android?tutorial-step=2). You need to configure this version of the sample as follows:
>
> 1. Rename the `./GraphTutorial/msal_config.json.example` file to `msal_config.json`.
> 1. Move the `msal_config.json` file to the `./GraphTutorial/app/src/main/res/raw` directory.
> 1. Edit the `msal_config.json` file and make the following changes.
>     1. Replace `YOUR_APP_ID_HERE` with the **Application Id** you got from the Azure portal.
