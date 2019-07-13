# datapuppy
Simple Android application that monitors CPU, RAM and Battery at any given time.


Known issues.

The CPU usage is made using the `proc` file system. Google has made several undocumented changes, restricting the access to the file on Android 7.0, and completely blocking it in Android 8.0+. Running the app on root devices does not make any difference.

Additional efforts of R+D are needed in order to bypass these restrictions (if possible, at all)

Icons by @Kiranshastry: https://www.flaticon.com/authors/kiranshastry