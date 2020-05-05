package com.google.android.libraries.launcherclient;

interface ILauncherInterface {
    /*
        Calls are expected to adhere to a general standard, and provide several standard faculties:
        std:l3predictions should return a list of predicted apps, or an empty list and err = "e_notsupported"
        in the returned bundle if the action is not supported.
            arguments: "amt": the amount of predictions to retrieve
        std:l3actions should return a list of predicted shortcuts, or an empty list and err = "e_notsupported"
            arguments: "amt": the amount of predictions to retrieve
        if the action is not supported;
        std:sbn should subscribe a [ch.deletescape.lawnchair.feed.notifications.INotificationsChangedListener]
            arguments: "listener": listener

        The returned value should be named "retval" in the bundle, and shouldn't be null.
        If an error occours, the "err" field should be set in the bundle. A list of standard error codes
        can be found below:

        "e_unsupported": The standard call is not available or not supported by the launcher
        "e_invalargs": The options bundle is invalid
        "e_other": Unknown error, err_desc should be set

        If the e_other is returned, the field err_desc should be set in the returned bundle,
        and should contain a __human readable__ description of the error.
     */
    List<String> getSupportedCalls();
    Bundle call(String callName, in Bundle opt);
}
