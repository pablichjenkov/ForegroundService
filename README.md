# ForegroundService
Since Android Oreo the system imposed some restrictions in the amount of CPU time a Background Service would get after the containig Application Process enters the Background or Cached state. The new rules will allow sometime for the service to work but will close the process completely after certain time. The exact time has not been confirmed and it probably varies per phone brand as well.

Such Background or Deamon Services have been used for Android developers since the platform early days. The change will affect a lot of applications but it seems that is the way to go. IOS operating system has had these constraints since the beginning, thats why some people say the Iphone battery last longer than Android or their Iphones connect faster to the network. Certainly the new rules will increase the performance of the hardware since less processes will have access the CPU at the same time.

This project basically explore the new Foreground Services API and the JobScheduler API, which are the two solutions Google propose for those developers whom still need long running Services for their app to work.

Open the app and stare at the logcat, the app will print all the events pertaining to each services, background and foreground ones.
