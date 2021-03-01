**Mobile Application Development Project, By Benoît Cordier, IOS 1**

**Introduction**

During this project, we had to develop an Android application concerning a secure bank account, which can access online data, update information locally on the phone, and allow only the owner of the account to access his data. In this report, we will cover the main points of this project.

First of all, I’ve decided to do this project mainly in Kotlin (activities in Kotlin, Classes in Java as Enigma doesn’t support Kotlin yet) because I’ve already done some Android App Development in Java in the past, and this project was the opportunity to discover a new language.


**Description**

This application is composed of three activities: 


•	The “MainActivity”, which is the main page of the application, where the user has to authenticate himself through a fingerprint process. Once it’s done, he has access (widgets become visible) to a button to update his accounts (connected with MockAPI) and another button to go to a different activity which will display his accounts.






•	The “AccountsOff” activity, which displays the accounts of the user offline, retrieving data from the phone’s database, which have been updated before through the Main Activity. The user also has access to a button which gives access to the activity to add a new account. This activity contains a scrollview, with multiple Linear Layout, in order to scroll through the accounts, well presented with their informations.









•	The “AddAccount” activity, to create a new account in MockAPI, and which provides a few TextEdit widget to write information about this account. It also provides a Cancel Button, which goes back to the AccountsOff activity and an Add Button, to add the account and directly go back to the AccountsOff activity (only if the user is connected to Internet). Amount is a decimal type EditText. Finally, there is a check if the currency is a valid one, with the help of the Currency Class from Java. It directly updates the local database.




This application is also composed of two more Java classes:

•	“Crypto”, which only stands to store two String which will be encrypted by Enigma (as Enigma doesn’t work with Kotlin).

•	“DatabaseManager”, which is the class to manage the database and perform operations onto it (select, insert, update, …).



**Authenticate the user**

As we said in the previous part, the user needs first to authenticate through a fingerprint process.

First, we need to check if we can create this fingerprint prompt with checkBiometricSupport(). We first check that keyguard is secured by a PIN, pattern or password or a SIM card is currently locked, and then if the application has permissions to use biometric. It displays an error toast if not in both cases. It also checks if a fingerprint has been enrolled on the phone.

Then, when clicking on the authenticate button, the app creates a biometric prompt (with a title, a subtitle, a cancel button). When it receives a signal, it calls the function authenticate of the prompt with the authenticationCallback. Then, whether the authentication failed, and it displays a toast with the error, whether it succeed, and it set the visibility to visible to all the widgets of this activity, as the user can now have access to the accounts.


**Communication with the API and safety**

To update, read and create accounts, we need to communicate with MockAPI securely. All this communication takes place in the Main Activity, when clicking on the update button. This communication happens in two steps: retrieve the user, and then retrieving his data. In our case, we will suppose the user has the id 1.

To create a secure connection, we will need the class HttpsURLConnection. This class will, when creating a connection with a given URL with a certificate issued by a well-known CA, enable the handshake between the SSL client (application) and the server. The server needs to prove it has the private key by signing its certificate with public-key cryptography. As MockAPI used a known Certificate Authority, using HttpsURLConnection is sufficient (as said in the Android documentation). With this, we can communicate securely with the API.


Then, we want to read data from the account, in order to update if needed on the application. The default type of request is GET, so we don’t need to specify a type. We only need to call the function getInputStream () from the class HttpsURLConnection, which will return all the data as an inputStream. Then we convert it to String, and then to JSON, in order to retrieve data and classify it to update the offline database.

 

However, we also want to create accounts on MockAPI. To do so, we once again open a HTTPS connection with the URL of the accounts. We need to set the type of request to POST (we need to enable it on MockAPI). Then we build a string in a JSON format with the data coming from the EditText of the activity. Like this, the website will be able to read and to classify it as it is on the server. Before sending it, we need to provide the length of the string, the properties (“content-type” and notify that it is JSON). Then we can send it using outputStream and retrieve a validation with inputStream.



**Store data Offline and safety**

We want our app to be usable offline. So, the idea is to store accounts offline, on the phone, and to create a button which updates the account when the phone is online. To store the data, we will use the SQLite database integrated to Android Studio that I’ve used in my previous projects. This database is very safe, more than a file, because it can’t be accessed through the files browser of the phone, and useful, as we can interact with the database very easily.

We will interact with the database through the class “DatabaseManager”. With the constructor we instantiate the database, and then, Inside the onCreate of this class, we create the tables if they don’t exist.

 

Finally, we can interact with the database through various methods to read, update, and insert data. For example, the method insertAccount is called when the user adds an account. The account is added on the server, and at the same time on the local database with this method with an insert statement.



**Hide URL from source code**

In order to improve the security of our application, we don’t want anyone to have access to URL from the source code of our application, like the link to the accounts of the user. To hide them, we are going to use an obfuscator string encryption plugin, Enigma.

This plugin is very easy to use, as we just need to enable it and run it on our application. To integrate it, we just need to create a git repository of our application if hasn’t been made. Then we just need to add the maven repository add the classpath in the gradle of the project, and to add the plugin inside the gradle of the app. We can finally set the options of Enigma. In my app, I decided to inject fake keys, as it’s safer. Then, we ca compile the app: the strings are hided in a strange binary language.

