# Travel Perfect (Capstone-Project)
Are you going on an exotic trip? Or maybe a short city trip? Do you feel overwhelmed with packing your belongings? Are you anxious until you arrive at your destination, in case you forgot something? No more! With travel perfect, even the beginning of the trip is relaxing. It will not only help you organise, but also remind you when you should be leaving, last minute check to be sure you didn’t forget your toothbrush and assist you packing.

## Why?
The most stressful moments during a trip are at the start and at the end. You are afraid of missing your train/plane and asking yourself if you packed everything needed. Well no more. This app provides a compact way to organise a trip to never forget anything at home.


## How to build
To be able to build this project, two configurations have to be made:
1. Rename default_config.gradle to config.gradle
2 Replace dummy Places API key. You can get your own from [here](https://developers.google.com/places/android-api/)
	```groovy
	android {
		defaultConfig {
			resValue "string", "places_api_key", "PLACES_API_KEY"
		}
	}
	```


## License
Copyright 2017 Caro Vaquero

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
