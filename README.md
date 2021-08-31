# COACH_RenderCards
Command-line utility to render COACH recommendation cards based off internal spreadsheet rows

# To Run
1. Install Apache Maven : https://maven.apache.org/install.html
1. Retrieve a copy of the "HBP CDS Cards" Excel spreadsheet from OHSU CMP Box folder (hereafter *&lt;file&gt;*)
1. Identify the Recommendation Number from **Column A** in the spreadsheet (hereafter *&lt;recNo&gt;*) 
1. In the root of this folder, run:
   
```
mvn exec:java "-Dexec.args=\"<file>\" <recNo>"
```

Note the double quotes around the **-Dexec.args** block, this is necessary to ensure both parameters get passed into the program correctly.

If *&lt;file&gt;* contains spaces, it must be wrapped in \ escaped double quotes. 

# Credit
This project is forked from https://github.com/ellieychang/OHSUHBP_CardToDisplay
