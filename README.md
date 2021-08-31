# COACH_RenderCards
Command-line utility to render COACH recommendation cards based off internal spreadsheet rows

## Setup (Install Prerequisites)
1. **IF YOU DON'T HAVE A JAVA 11 JDK INSTALLED**, install OpenJ9 JDK for Java 11: 
   
   - go here: https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=openj9
   - leave settings alone and click "Latest release" to download the latest release
   - install wherever you like
   - be sure to check "set JAVA_HOME variable" when installing

1. **IF YOU DON'T HAVE APACHE MAVEN INSTALLED**, Install Apache Maven: 
   
   - go here : https://maven.apache.org/download.cgi
   - download this: apache-maven-3.8.2-bin.zip
   - follow instructions at: https://maven.apache.org/install.html
   - extract to C:\Users\YourUsername\
    
1. Retrieve a copy of the "HBP CDS Cards" Excel spreadsheet from OHSU CMP Box folder (hereafter *&lt;file&gt;*)

## Build the Project

1. Open a Windows command prompt and navigate to the folder where this project is cloned
2. Build the project by running the following command:

```
mvn clean compile package
```

## Running the Program

1. Identify the Recommendation Number from **Column A** in the spreadsheet (hereafter *&lt;recNo&gt;*) for which to render cards
1. In the root of this folder, run:
   
```
mvn exec:java "-Dexec.args=\"<file>\" <recNo>"
```

For example:
```
mvn exec:java "-Dexec.args=Cards.xlsx 23c"
mvn exec:java "-Dexec.args='other cards with spaces in the filename.xlsx' 13"
```

Note the double quotes around the **-Dexec.args** block, this is necessary to ensure both parameters get passed into the program correctly.

If *&lt;file&gt;* contains spaces, it must be wrapped in single quotes, as seen in the second example above.

The following is an example of a successful execution:

```
C:\git\COACH_RenderCards>mvn exec:java "-Dexec.args='HBP CDS Cards v5.xlsx' 23c"
[INFO] Scanning for projects...
[INFO]
[INFO] ----------------< edu.ohsu.dmice.cmp:COACH_RenderCards >----------------
[INFO] Building COACH_RenderCards 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- exec-maven-plugin:3.0.0:java (default-cli) @ COACH_RenderCards ---
Writing file 23c.html
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.439 s
[INFO] Finished at: 2021-08-31T15:57:29-07:00
[INFO] ------------------------------------------------------------------------

C:\git\COACH_RenderCards>
```
You can see from the output above that it completed successfully, and wrote a file "23c.html" which contains the 
rendered card for Recommendation 23c.

# Credit
This project is forked from https://github.com/ellieychang/OHSUHBP_CardToDisplay
