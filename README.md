# Reachability

The app aims to port iPhone's reachability feature (double tap on home brings down the screen). There's no simple API to achieve this on unrooted devices. But in theory this should be possible to do with the existing APIs for Lollipop and above. 

Currently the app implements an `AccessibilityService` which listens to a specific trigger and captures the current screen and mirrors it on a surface that is more "reachable". 

The app is written in kotlin. 


License
-------

    Copyright 2015 Workarounds

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
