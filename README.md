**Mobile Application Development Project, By Benoît Cordier, IOS 1**

**Introduction**

During this project, we had to develop an Android application concerning a secure bank account, which can access online data, update information locally on the phone, and allow only the owner of the account to access his data. I created the app called "SafeBank", which responds to these expectations. In this report, we will cover the main points of this project.

![alt text](https://github.com/cordierben/Lab3_Mobile_App_Sec_Dev/blob/main/screen/safebank.png)

First of all, I’ve decided to do this project mainly in Kotlin (activities in Kotlin, Classes in Java as Enigma doesn’t support Kotlin yet) because I’ve already done some Android App Development in Java in the past, and this project was the opportunity to discover a new language. Before sending the application, I've analyzed it using smali/baksmali and APKTool.

**DISCLAIMER : STRINGS ARE NOT ENCRYPTED IN THE SRC FOLDER, BUT THEY ARE IN THE APK AND WILL APPEAR ENCRYPTED IF YOU REVERSE ENGINEERING THE APP. THIS IS ENIGMA PLUGIN WHICH MAKES ENCRYPTION TRANSPARENT FOR THE USER. I'VE PUT THE FILES IN THE ENCRYPTED VERSION IN THE ENCRYPTED_FILES FOLDER. **


**Description**

This application is composed of three activities: 


•	The “MainActivity”, which is the main page of the application, where the user has to authenticate himself through a fingerprint process. Once it’s done, he has access (widgets become visible) to a button to update his accounts (connected with MockAPI) and another button to go to a different activity which will display his accounts. It also displays a welcome message by retrieving informations about the user with a GET request on "config/1".


![alt text](https://github.com/cordierben/Lab3_Mobile_App_Sec_Dev/blob/main/screen/main.png)



•	The “AccountsOff” activity, which displays the accounts of the user offline, retrieving data from the phone’s database, which have been updated before through the Main Activity. The user also has access to a button which gives access to the activity to add a new account. This activity contains a scrollview, with multiple Linear Layout, in order to scroll through the accounts, well presented with their informations.


![alt text](https://github.com/cordierben/Lab3_Mobile_App_Sec_Dev/blob/main/screen/list.png)






•	The “AddAccount” activity, to create a new account in MockAPI, and which provides a few TextEdit widget to write information about this account. It also provides a Cancel Button, which goes back to the AccountsOff activity and an Add Button, to add the account and directly go back to the AccountsOff activity (only if the user is connected to Internet). Amount is a decimal type EditText. Finally, there is a check if the currency is a valid one, with the help of the Currency Class from Java. It directly updates the local database.

![alt text](https://github.com/cordierben/Lab3_Mobile_App_Sec_Dev/blob/main/screen/add.png)


This application is also composed of two more Java classes:

•	“Crypto”, which only stands to store two String which will be encrypted by Enigma (as Enigma doesn’t work with Kotlin).

•	“DatabaseManager”, which is the class to manage the database and perform operations onto it (select, insert, update, …), and encrypt and decrypt data.

Every time you leave the app, the app closes for security reasons.



**Authenticate the user**

As we said in the previous part, the user needs first to authenticate through a fingerprint process.

First, we need to check if we can create this fingerprint prompt with checkBiometricSupport(). We first check if the Android version is sufficient, then if there is a hardware, if it's available and finally if a fingerprint has been enrolled into the settings. I DECIDED TO NOT DEVELOP ANOTHER WAY TO AUTHENTIFY (IF ONE OF THESE CASE IS NOT MET) AS IT WON'T BE SECURED ENOUGH FOR THIS APP.

Then, when clicking on the authenticate button, the app creates a biometric prompt (with a title, a subtitle, a cancel button). When it receives a signal, it calls the function authenticate of the prompt with the authenticationCallback. Then, whether the authentication failed, and it displays a toast with the error, whether it succeed, and it set the visibility to visible to all the widgets of this activity, as the user can now have access to the accounts.

On most recent version, you can even choose between facial recognition or fingerprint!

![alt text](https://github.com/cordierben/Lab3_Mobile_App_Sec_Dev/blob/main/screen/fingerprint.png)





![alt text](https://github.com/cordierben/Lab3_Mobile_App_Sec_Dev/blob/main/screen/facialRecognition.jpg)

**Communication with the API and safety**

To update, read and create accounts, we need to communicate with MockAPI securely. All this communication takes place in the Main Activity, when clicking on the update button. This communication happens in two steps: retrieve the user, and then retrieving his data. In our case, we will suppose the user has the id 1.

To create a secure connection, we will need the class HttpsURLConnection. This class will, when creating a connection with a given URL with a certificate issued by a well-known CA, enable the handshake between the TLS client (application) and the server. The server needs to prove it has the private key by signing its certificate with public-key cryptography. Using HttpsURLConnection is sufficient (as said in the Android documentation).
I've decided to add a network security configuration, to trust a certain set of CA, using a network security configuration file in the manifest.


Then, we want to read data from the account, in order to update if needed on the application. The default type of request is GET, so we don’t need to specify a type. We only need to call the function getInputStream () from the class HttpsURLConnection, which will return all the data send by the server. (Have you noticed that little animation when clicking on the refresh button?).


However, we also want to create accounts on MockAPI. To do so, we once again open a HTTPS connection with the URL of the accounts. We need to set the type of request to POST (we need to enable it on MockAPI). Like this, the website will be able to read and to classify the JSON on the server. Then we can send it using outputStream and retrieve a validation with inputStream.



**Store data Offline and safety**

We want our app to be usable offline. So, the idea is to store accounts offline, on the phone, and to create a button which updates the account when the phone is online. To store the data, we will use the SQLite database integrated to Android Studio that I’ve used in my previous projects. This database is very safe, more than a file, because it can’t be accessed through the files browser of the phone or any other app, and useful, as we can interact with the database very easily.

The security of this part is composed of three layers!

•	First layer : the database is crypted and can't be accessed from the phone

•	Second layer : data inside the database is crypted with a secret custom key based on a password which I'm the only one to know

•	Third layer : this password is himself crypted...

Therefore, data is completely protected.We will interact with the database through the class “DatabaseManager”, to create tables, insert or select data.

![alt text](https://github.com/cordierben/Lab3_Mobile_App_Sec_Dev/blob/main/screen/db.png)

**Hide URL from source code**

In order to improve the security of our application and fight against reverse engineering, we don’t want anyone to have access to URL from the source code of our application, like the link to the accounts of the user. To hide them, we are going to use an obfuscator string encryption plugin, Enigma.

This plugin is very easy to use, as we just need to enable it and run it on our application. To integrate it, we just need to create a git repository of our application if hasn’t been made. Then we just need to add the maven repository add the classpath in the gradle of the project, and to add the plugin inside the gradle of the app. We can finally set the options of Enigma. In my app, I decided to inject fake keys, as it’s safer. Then, we ca compile the app: the strings are hided in a strange binary language.

![alt text](https://github.com/cordierben/Lab3_Mobile_App_Sec_Dev/blob/main/screen/enigma.png)

