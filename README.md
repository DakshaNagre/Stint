# Stint - A Task Management Application

# Documentation  
This document provides the instructions to run the application on your machine.

### Clone the project
```
git clone https://github.iu.edu/P532Fall19/Task-Management.git
```
### Change to project directory
```
cd Task-Management/
```
# Running each module:
## 1. Backend
This micro-service provides backend services for entire application.
### Navigate to backend Folder
```
cd backend/
```
### Pre-requisites
```
1) python3 or higher
2) pip3 or higher
```

### Install required libraries
```
1) Linux or mac:
	python3 -m venv env
	venv ./env/bin/activate
	pip3 install -r dependencies.txt
2) Windows:
	py -m venv env
	env\Scripts\activate
	pip3 install -r dependencies.txt
```

### Run the code
```
Linux or mac:
	python3.7 -m app
Windows:
	python -m app
```
~~~
The backend service will run in port 5000
~~~

## 2.  Website
Provides a web Interface for the application.
### Open a new terminal and navigate to web
```
cd web/
```
### Pre-requisites
```
1) node
2) npm
Refer: https://nodejs.org/en/download/
```
### Install required libraries
```
npm install
```
### Run the code
```
npm start
```
~~~
The Website will run in port 3000
~~~

## Accessing the application on Browser
```
Goto -> 'http://localhost:3000/sign-in' in browser
```

## 3.  Android app
Provides a mobile Interface for the application.
### Pre-requisites
```
1) Java
2) Android Studio
Refer: [https://developer.android.com/studio](https://developer.android.com/studio)
```
### Android Studio
Open Android Studio and follow below instructions
```
Go to File present in top menu bar and click on open

A directory chooser dialog box will appear.
Choose the 'android' folder from the cloned repository
```
### Install required libraries
```
Click on Gradle sync button to fetch all the dependencies.
```
### Run application
```
Go to Run present in top menu bar and click on "Run 'app'"
The application will run in your emulator
```
