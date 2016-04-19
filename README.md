# RocketWeatherChallenge
Code exercise for interview

<b>Requirements:</b>

Generate an app which displays a 5 day forcast starting with current day

Each day will get a view which should be swipable (viewpager)
- Display the temperature and a short description
- Centered 

Weather determined from users current location
- Default to 60661
- Disable geolocation via option menu using android permissions

Use the weather.gov JSON feed
- http://forecast.weather.gov/MapClick.php?lat=41.885575&lon=-87.644408&FcstType=json
- Must use network activity no preloaded data (cheating) 

Tight scope 
- no icons or loading screens

Android 4.0.4 (Ice Cream Sandwich) target 

Delivery
- Send single APK and a Zip file of the code or point to github
- Tested on Android tablet running Marshmallow and phone running Lollipop
- 48 hour time limit

<b>Classes</b>
- Location/Fallback 
- Weather JSON Reader
- Asnc Communication
- Main Activity
- Options

<b>Notes</b>
- The API is delivers the data differently if you are viewing it between 8AM and 6PM or later. Needed to include cases to handle it
- Used the Android Studio template for an UI with a viewpager. Generally I'd code this myself but the requirements were clear 
about keeping things simple. Take a look at my Guild Ball app for how I'd implement it (it's in the Player Info segment)
- uses-permission android:name=android.permission.INTERNET was needed in the manifest
- Using Otto library for bus communication. I find the throw/catch way of communication more flexable and easier to debug then using interfaces. 
