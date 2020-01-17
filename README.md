# DREAM - ploc
[![js-standard-style](https://img.shields.io/badge/BSD%20license-3--Clause-green)](https://opensource.org/licenses/BSD-3-Clause) 
[![js-standard-style](https://img.shields.io/badge/android%20sdk-API%20level%20%3E%2024-red)](https://developer.android.com/about/versions/nougat/android-7.0.html)   
[![js-standard-style](https://img.shields.io/badge/open-access-yellow)](https://open-access.network/startseite)
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.1468906.svg)](https://doi.org/10.5281/zenodo.1468906)
![js-standard-style](https://img.shields.io/badge/research-prototype-red.svg)


Ploc is a prototypical android application and part of the research project Data REseArch Mining 
(DREAM). It is used to evaluate new ideas on how Open Access publications can be made available on 
mobile devices. The application is only executable with the corresponding backend [GoZer](https://github.com/fzi-forschungszentrum-informatik/dream-gozer).

Main features:

* Define a interest profile with keywords.
* Personalized publication and expert feed based on the interest profile.
* Detail views for publications and experts.
* Bookmark functionality for publications and experts.
* Sort the bookmarked publications in collections.
* Simple feedback process with decentralized public storage.

<p float="left">
  <img src="/img/screenshots/menu_framed.png" width="200" />
  <img src="/img/screenshots/like_framed.png" width="200" /> 
  <img src="/img/screenshots/experts_framed.png" width="200" />
  <img src="/img/screenshots/bookmarks_framed.png" width="200"/>
</p>

## Getting Started

These instructions will get you a copy of the project up and running on your local mobile device for 
development and testing purposes. The project is not intended for productive use as it does not
have the necessary security standards (HTTPS, random password generation). There are two ways to 
build the application, either via a local installed Android and Java SDK on your machine or with the
provided Docker file.

### Prerequisites

* Android Device or Android Emulator (Min API level 24)
* Running GoZer instance
* Enabled [developer options](https://developer.android.com/studio/debug/dev-options) and the debug mode on your Android device
* Allow your Android device to install apps from unknown sources

**Deploy with local installed SDK:**

* Android SDK Tools (API level 28)
* JAVA SDK 8
* Android Studio (Optional)

**Deploy with docker:**

* Docker

## Deployment

**Deploy with local installed SDK:**

To successfully deploy the application you need the address (IP or FQDN) of 
your running GoZer instance. This address needs to be set as an environment
variable through the gradle wrapper. Replace the <...> with your server
information and run the command from the project's root folder.

Command for building the .apk (Windows Powershell):
```
./gradlew assembleDebug -PGOZER_ADDRESS='\"http://<GOZER SERVER ADDRESS>:<PORT>\"'
```

Command for deploy the .apk and directly on a connected android device (Windows Powershell):
```
./gradlew installDebug -PGOZER_ADDRESS='\"http://<GOZER SERVER ADDRESS>:<PORT>\"'
```

To deploy the application directly with Android Studio, the following line must be entered under 
File -> Settings -> Build, Execution, Deployment -> Compiler -> Command line options:
```
-PGOZER_ADDRESS="\"http://<GOZER SERVER ADDRESS>:<PORT>\""
```

NOTE: Take care when escaping the quotes, the shown commands are working on Windows 10 machines.

**Build with docker:**

You can also create the apk file by using a docker SDK container.
To successfully build the application with docker you also need the address (IP or FQDN) of your
running GoZer instance. This address needs to be set as an environment variable for the docker
build process. 

Command for building the docker image (Windows Powershell):

```
docker build -t ploc_build --build-arg GOZER_ADDRESS_VAR="http://<GOZER SERVER ADDRESS>:<PORT>" .
```

The following command will run the docker container from the previously created docker image and
 builds an android .apk to the specified path on your local machine. Your docker instance needs
 to have write permission to this path.

```
docker run --name ploc --rm -v ~<LOCAL APK OUTPUT DIR>:/usr/local/ploc-app/output/ ploc_build
```

**Deploy via GoZer:**

Follow the deployment instructions from [GoZer](https://github.com/fzi-forschungszentrum-informatik/dream-gozer). GoZer builds ploc and its apk file on 
initialization once. This file can be downloaded in via GoZer directly (`http://<GoZer IP:PORT>/download/ploc`). Please open the URL from your
mobile phone's Web browser and download the apk file. Go to your download directory and start the 
installation process. 


## Development

### Architecture

This android application is build with the Android Jetpack libraries which are part of the [AndroidX
packages](https://developer.android.com/jetpack/androidx). The architecture of the application is based on the [Android Jetpack guide to app architecture](https://developer.android.com/jetpack/docs/guide).
We use the [Model–View–Viewmodel](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) (MVVM) pattern to get the data from the sources to the GUI. 
In our case, we have several repositories (profile, publication, feedback, subject) and the corresponding data sources. In addition, there are sources for 
the endless feed of publications and experts that are created using the [Android Paging Library](https://developer.android.com/topic/libraries/architecture/paging/).

![The Android Jetpack Architecture](/img/overview/jetpack_architecture.png "The Android Jetpack Architecture")
*The Android Jetpack Architecture (by Google, [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0))*

### Structure of Activities and Fragments

The entry point is actually the MainActivity, but before that the LoadingScreenActivity and on condition 
the OnboardingActivity are executed. LoadingScreenActivity and OnboardingActivity manage, as the name suggests, the start and 
inital onboarding process of the application. The MainActivity is our logical entry point, because 
it contains the three basic functions - publication, expert and feedback - as well as the navigation 
in the Drawer and the BottomMenu.

The following graphic provides an overview of the activities and their associated fragments.
The same naming convention is used not only for the fragments and activities but in the whole application, like for adapters and holders too.

![ploc - Structure](/img/overview/application_structure.png "ploc - Structure") 

## Build JavaDoc

This source code is documented with JavaDoc comments. It is possible to generate a static HTML version of the documentation
 for getting insights into this implementation. 

**Generate JavaDoc with Android Studio:**

To build the JavaDocs with Android Studio you need to open this project and got to "Tools -> Generate JavaDoc".

**Generate JavaDoc with command line tools:**

```
javadoc -d <PATH TO DESTINATION DIRECTORY>
```

## Further information
To get more information about this project see [FZI Project Page](https://www.fzi.de/forschung/projekt-details/dream-digital-research-mining/), [HIIG Project Page](https://www.hiig.de/en/project/dream-digital-research-mining/) or
take a look at the overview posters published on Zenodo [Link](https://zenodo.org/record/1468906).

## Authors

* **Felix Melcher** (FZI Research Center for Information Technology)

## License

This open source software is licensed under the 3-Clause BSD License - see the [LICENSE.md](LICENSE.md) file for details.

All remaining android source files are originaly and re-published under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Acknowledgments

The DREAM project and its source code are the result of a joint project of the [Humboldt Institute for Internet and Society](https://www.hiig.de/) (HIIG) and the [FZI Research Center for Information Technology](https://www.fzi.de/). The project was funded by the [Federal Ministry of Education and Research Germany](https://www.bmbf.de/) (BMBF).

We also like to thank:

* [Bielefeld Academic Search Engine](https://base-search.net/) and [EconStor](https://www.econstor.eu/) for giving access to their metadata collections

... and all the open source developers for their time and their great work!                                                                                                                                                                                                                                                                         
