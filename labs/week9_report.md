_2024-03-11_  
_CSE 15L Lab_  
_Week 9 Report_  
_See https://c0lrm3r.github.io/cse15l-lab-reports/labs/week9_report_  

# Week 9 Lab Report

In this scenario, our student has written some code to download and parse data from the UCSD Waitz page, which tracks and gives information about how crowded locations around campus are. Unfortunately it doesn't quite work. Here is their java program:

## The Java Program

```
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * WaitzTracker class. Implements some basic scraping functionality to pull
 * population data (and others) about UCSD's various locations and formatts it
 * in plaintext.
 */
public class WaitzTracker {
    private static final String WAITZ_REQ_URL = "https://waitz.io/live/ucsd";

    /**
     * Nested class to store extracted data about a target location
     */
    static class TargetData {
        public String name              = null;
        public Double busyPercent       = null;
        public Integer people           = null;
        public Boolean isAvailable      = null;
        public Integer capacity         = null;
        public String hourSummary       = null;
        public Boolean isOpen           = null;

        /**
         * toString method. Formats the contents of the calling TargetData
         * instance into a nice, pretty, printable string for the user.
         *
         * @return a formatted string containing the target's information.
         */
        public String toString() {
            String fClr         = "\033[0m";
            String fBld         = "\033[1m";
            String cYellow      = "\033[33m";
            String cGreen       = "\033[32m";
            String cRed         = "\033[31m";
            String retStr       = "";
            String openStatus  = cYellow + "No Data." + fClr;
            String availStatus = cYellow + "No Data." + fClr;

            if(isOpen != null)
                openStatus = (isOpen ? cGreen : cRed) + hourSummary + fClr;
            if(isAvailable != null)
                availStatus = (isAvailable ? "" : cYellow)
                    + (isAvailable ? "Available for use." : "Not Available.")
                    + fClr;
            retStr += "["+name+"] ("+ openStatus +")\n";
            int tmpLen = retStr.length();
            for(int i = 0; i < tmpLen; i++)
                retStr += "_";
            retStr += "\n\n";
            retStr += fBld + "\tPercent Full: " + fClr 
                + busyPercent + "%\n\n";
            retStr += fBld + "\tHeadcount: " + fClr 
                + people + " / "+capacity + "\n\n";
            retStr += "\t" + availStatus + "\n\n";

            if(name == null ||
                busyPercent == null ||
                people == null ||
                capacity == null || 
                hourSummary == null)
                retStr = (fBld + cYellow + "\n!!! Some Data is Inaccurate !!!"
                     + fClr + "\n\n") + retStr;
                
            
            return retStr;
        }
    }

    /**
     * Extracts data about a target from a raw JSON string and stores
     * it in a new object
     *
     * @param json The raw JSON string to parse
     * @param target The target to search for
     * @return An object containing relevant info from Waitz
     */
    public TargetData extractTargetData(String json, String target) {
        TargetData data = new TargetData();
        
        // Too lazy to parse JSON haha. There really should only be one
        // instance of this string in the return data anyway.
        String startPattern = "\"name\":\"" + target +"\"";
        String endPattern   = "Html\"";
        int startIdx = json.indexOf(startPattern); 
        if(startIdx == -1) return null;
        int endIdx = json.indexOf(endPattern, startIdx);
        String[] fields = json.substring(startIdx, endIdx)
            .replace("\"", "")
            .split(",");
        // This is really cursed, but it should only grad the first instance
        // of each field in the array, disregarding any duplicates with bad
        // data spawned from the horrible code above.
        for(String field : fields) {
            String[] property = field.split(":");
            if(data.name == null 
                && property[0].equals("name"))
                data.name = property[1];
            else if (data.busyPercent == null 
                && property[0].equals("busyness"))
                data.busyPercent = Integer.parseInt(property[1])/100.0;
            else if (data.people == null 
                && property[0].equals("people"))
                data.people = Integer.parseInt(property[1]);
            else if (data.isAvailable == null 
                && property[0].equals("isAvailable"))
                data.isAvailable = Boolean.parseBoolean(property[1]);
            else if (data.capacity == null 
                && property[0].equals("capacity"))
                data.capacity = Integer.parseInt(property[1]);
            else if (data.hourSummary == null
                && property[0].equals("hourSummary"))
                data.hourSummary = property[1];
            else if (data.isOpen == null
                && property[0].equals("isOpen"))
                data.isOpen = Boolean.parseBoolean(property[1]);
        }
        return data;
    }
    
    /**
     * Returns a string containing the raw JSON data from Waitz
     */
    public String getWaitzData() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(WaitzTracker.WAITZ_REQ_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            int resp = conn.getResponseCode();
            if(resp == HttpURLConnection.HTTP_OK) {
                BufferedReader inStream = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
                );
            // Reponse is one line
            return inStream.readLine();
            }
        } catch (Exception e) {
            System.out.println("Failed to grab Waitz data!");
        }
        return null;
    }    

    /**
     * Main method. handles command-line interaction and overall program
     * control.
     */
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println(
            "\nUsage: java WaitzTracker \"<Location Name>\"" +
            "\"<External JSON>\"\nPossible Locations (not exhaustive):\n\n" +
                "\t- Geisel Library\n" +
                "\t- Price Center\n" +
                "\t- Price Center East\n" +
                "\t- Price Center West\n" +
                "\t- 64 Degrees\n" +
                "\t- 7th Market\n" +
                "\t- Cafe Ventanas\n" +
                "\t- Canyon Vista\n" +
                "\t- Foodworx\n" +
                "\t- Main Gym\n" +
                "\t- OceanView Terrace\n" +
                "\t- Pines\n" +
                "\t- RIMAC Fitness Gym\n" +
                "\t- Student Services Center\n" +
                "\t- Campus Card Office\n" +
                "\t- Cashier's Office\n" +
                "\t- Financial Aid Office\n" +
                "\t- The Bistro\n" +
                "\t- WongAvery Library\n" +
                "\nNote: External JSON is optional, and bypasses the \n" +
                "program's internal Http request code.");
            System.exit(1);
        }
        String targetLocation = args[0];
        String rawJSON = null;
        WaitzTracker instance = new WaitzTracker();
        WaitzTracker.TargetData targetStatus;

        // Parse external JSON
        if(args.length > 1) rawJSON = args[1];
        else                rawJSON = instance.getWaitzData();
        targetStatus = instance.extractTargetData(rawJSON, targetLocation);
        if(targetStatus == null) {
            System.out.println("Could not retrieve information for \'"
                + targetLocation + ".\' Does it exist?");
            System.exit(1);
        }
        System.out.println(targetStatus.toString());
    }
}

```

The student wanted to extract this data and print it in the terminal, so that they could feel like a cool hacker. However, having only extensively programmed in PowerPC Assembly, they were having a lot of trouble with Java and networking. As a result, their code to download data from the server didn't work. Instead of trying to fix it, they decided to just use CURL (a command-line tool for retrieving data from servers) to download the data and pass it as an argument to the java program. They did this with a bash script:

## The BASH Band-Aid

```
#!/bin/sh
JSON=$(curl -s https://waitz.io/live/ucsd)
if [[ ! $? -eq 0 ]]; then
    echo "There was an error using cURL to download the latest Waitz data."
    exit 1
fi
if [[ ! -f WaitzTracker.java ]]; then
    echo "Could not find source code (WaitzTracker.java)!"
    exit 1
fi
if [[ ! -f WaitzTracker.class ]]; then
    javac WaitzTracker.java
    if [[ ! $? -eq 0 ]]; then
        echo "Failed to compile source code (WaitzTracker.java)!"
        exit 1
    fi
fi
if [[ ! $# -eq 1 ]]; then
    java WaitzTracker
else
    java WaitzTracker $1 $JSON
fi
if [[ -f WaitzTracker.class ]]; then
    rm WaitzTracker.class
fi

```
Unfortunately, they ended up spending 5 hours trying to get the script working right. Eventually, they took their issue to the EdStem forums:

![Q1](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/q1.jpg)

In truth, the full error was:

```
TimApple@2006-Macbook:~/$ ./Waitz "Geisel Library"
./Waitz: 5: [[: not found
./Waitz: 9: [[: not found
./Waitz: 13: [[: not found
./Waitz: 20: [[: not found
Could not retrieve information for 'Geisel.' Does it exist?
./Waitz: 28: [[: not found
```

However, the student just wanted to focus on the top errors first. The full thread that developed is as follows:

![Q1](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/q1.jpg)
![R1](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/r1.jpg)
![Q2](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/q2.jpg)
![R2](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/r2.jpg)
![Q3](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/q3.jpg)

As it turns out, `sh` and `bash` aren't the same thing. And running a script made for bash with sh might work-- depending on the script-- or it might totally break. By changing the first line of the script "`#!/bin/sh`" to "`#!/bin/bash`", the system runs the script with bash, fixing the syntax error. Alternatively, the programmer ran the script explicitly with bash, which recognized the syntax correctly.

_Author's Note: This issue may seem like a simplistic syntax error, but it actually cost me a Skill Demonstration to discover the first time around, so... yea._  
  
However, to the dismay of our poor programmer, that wasn't the only problem with this script. After implementing the above fix, the errors go away, but the java program still doesn't work right.  

```
TimApple@2006-Macbook:~/$ bash Waitz "Geisel Library"
Could not retrieve information for 'Geisel.' Does it exist?
```

Although the programmer specified "Geisel Library" as the argument, it seems like the program is only getting the first word? Is this a Bash issue? A Java issue? A skill issue, even? Back to EdStem:  

![Q3](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/q3.jpg)
![R3](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/r3.jpg)
![R3](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/r3.jpg)
![Q4](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/q4.jpg)

What is happening here? Well looking at the script, `JSON=$(curl -s https://waitz.io/live/ucsd)` fetches some data and stores it in `$JSON`, and then the string "Geisel Library" is stored as an argument in `$1`. These are then passed into the java program with the line `java WaitzTracker $1 $JSON`. But... what about the quotes that the user provided to make sure that "Geisel Library" was parsed as one argument? They aren't present in that line, so maybe the Java program is interpreting it as two arguments, a "Geisel" and a "Library" (and the JSON data). Thankfully, the professor caught this on EdStem:

![R4](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/r4.jpg)

_Author's Note: This is an incredibly lucky fix, because if they didn't suggest surrounding both $1 and $JSON with quotes, the spaces in the JSON string means it would have been parsed as multiple arguments. Because of the way the Java program is written, it wouldn't have caught this, and the program would more or less give the same error, because **technically** a string was provided in the position where the program expects a JSON string, and it would have just rolled with it. This is overall bad design on the programmer's (cough cough me) part._

All in all, the errors were found, and they all lived happily ever after:

![Q5](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/q5.jpg)

But why didn't the internal Http request method work? Why did the programmer even need to use CURL? This is beyond the EdStem section, but it all comes down to HTTP `GET` and `POST`. To put it simply, GET is used to request data (what the programmer wanted), and POST is used to send or update data. The programmer, following the first StackOverflow post they could find, copied most of the code, and left the `conn.setRequestMethod("POST");` line in without checking whether it was really correct. Changing "POST" to "GET" fixes the getWaitzData() method, negating the need for the script at all.  

Unfortunately, our beloved programmer isn't going to find this bug, and will spend their life writing increasingly complex and shameful Bash scripts instead of learning how to use a debugger or read Oracle documentation.  


## The whole thing, fixed:

```
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * WaitzTracker class. Implements some basic scraping functionality to pull
 * population data (and others) about UCSD's various locations and formatts it
 * in plaintext.
 */
public class WaitzTracker {
    private static final String WAITZ_REQ_URL = "https://waitz.io/live/ucsd";

    /**
     * Nested class to store extracted data about a target location
     */
    static class TargetData {
        public String name              = null;
        public Double busyPercent       = null;
        public Integer people           = null;
        public Boolean isAvailable      = null;
        public Integer capacity         = null;
        public String hourSummary       = null;
        public Boolean isOpen           = null;

        /**
         * toString method. Formats the contents of the calling TargetData
         * instance into a nice, pretty, printable string for the user.
         *
         * @return a formatted string containing the target's information.
         */
        public String toString() {
            String fClr         = "\033[0m";
            String fBld         = "\033[1m";
            String cYellow      = "\033[33m";
            String cGreen       = "\033[32m";
            String cRed         = "\033[31m";
            String retStr       = "";
            String openStatus  = cYellow + "No Data." + fClr;
            String availStatus = cYellow + "No Data." + fClr;

            if(isOpen != null)
                openStatus = (isOpen ? cGreen : cRed) + hourSummary + fClr;
            if(isAvailable != null)
                availStatus = (isAvailable ? "" : cYellow)
                    + (isAvailable ? "Available for use." : "Not Available.")
                    + fClr;
            retStr += "["+name+"] ("+ openStatus +")\n";
            int tmpLen = retStr.length();
            for(int i = 0; i < tmpLen; i++)
                retStr += "_";
            retStr += "\n\n";
            retStr += fBld + "\tPercent Full: " + fClr 
                + busyPercent + "%\n\n";
            retStr += fBld + "\tHeadcount: " + fClr 
                + people + " / "+capacity + "\n\n";
            retStr += "\t" + availStatus + "\n\n";

            if(name == null ||
                busyPercent == null ||
                people == null ||
                capacity == null || 
                hourSummary == null)
                retStr = (fBld + cYellow + "\n!!! Some Data is Inaccurate !!!"
                     + fClr + "\n\n") + retStr;
                
            
            return retStr;
        }
    }

    /**
     * Extracts data about a target from a raw JSON string and stores
     * it in a new object
     *
     * @param json The raw JSON string to parse
     * @param target The target to search for
     * @return An object containing relevant info from Waitz
     */
    public TargetData extractTargetData(String json, String target) {
        TargetData data = new TargetData();
        
        // Too lazy to parse JSON haha. There really should only be one
        // instance of this string in the return data anyway.
        String startPattern = "\"name\":\"" + target +"\"";
        String endPattern   = "Html\"";
        int startIdx = json.indexOf(startPattern); 
        if(startIdx == -1) return null;
        int endIdx = json.indexOf(endPattern, startIdx);
        String[] fields = json.substring(startIdx, endIdx)
            .replace("\"", "")
            .split(",");
        // This is really cursed, but it should only grad the first instance
        // of each field in the array, disregarding any duplicates with bad
        // data spawned from the horrible code above.
        for(String field : fields) {
            String[] property = field.split(":");
            if(data.name == null 
                && property[0].equals("name"))
                data.name = property[1];
            else if (data.busyPercent == null 
                && property[0].equals("busyness"))
                data.busyPercent = Integer.parseInt(property[1])/100.0;
            else if (data.people == null 
                && property[0].equals("people"))
                data.people = Integer.parseInt(property[1]);
            else if (data.isAvailable == null 
                && property[0].equals("isAvailable"))
                data.isAvailable = Boolean.parseBoolean(property[1]);
            else if (data.capacity == null 
                && property[0].equals("capacity"))
                data.capacity = Integer.parseInt(property[1]);
            else if (data.hourSummary == null
                && property[0].equals("hourSummary"))
                data.hourSummary = property[1];
            else if (data.isOpen == null
                && property[0].equals("isOpen"))
                data.isOpen = Boolean.parseBoolean(property[1]);
        }
        return data;
    }
    
    /**
     * Returns a string containing the raw JSON data from Waitz
     */
    public String getWaitzData() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(WaitzTracker.WAITZ_REQ_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            int resp = conn.getResponseCode();
            if(resp == HttpURLConnection.HTTP_OK) {
                BufferedReader inStream = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
                );
            // Reponse is one line
            return inStream.readLine();
            }
        } catch (Exception e) {
            System.out.println("Failed to grab Waitz data!");
        }
        return null;
    }    

    /**
     * Main method. handles command-line interaction and overall program
     * control.
     */
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println(
            "\nUsage: java WaitzTracker \"<Location Name>\"" +
            "\"<External JSON>\"\nPossible Locations (not exhaustive):\n\n" +
                "\t- Geisel Library\n" +
                "\t- Price Center\n" +
                "\t- Price Center East\n" +
                "\t- Price Center West\n" +
                "\t- 64 Degrees\n" +
                "\t- 7th Market\n" +
                "\t- Cafe Ventanas\n" +
                "\t- Canyon Vista\n" +
                "\t- Foodworx\n" +
                "\t- Main Gym\n" +
                "\t- OceanView Terrace\n" +
                "\t- Pines\n" +
                "\t- RIMAC Fitness Gym\n" +
                "\t- Student Services Center\n" +
                "\t- Campus Card Office\n" +
                "\t- Cashier's Office\n" +
                "\t- Financial Aid Office\n" +
                "\t- The Bistro\n" +
                "\t- WongAvery Library\n" +
                "\nNote: External JSON is optional, and bypasses the \n" +
                "program's internal Http request code.");
            System.exit(1);
        }
        String targetLocation = args[0];
        String rawJSON = null;
        WaitzTracker instance = new WaitzTracker();
        WaitzTracker.TargetData targetStatus;

        // Parse external JSON
        if(args.length > 1) rawJSON = args[1];
        else                rawJSON = instance.getWaitzData();
        targetStatus = instance.extractTargetData(rawJSON, targetLocation);
        if(targetStatus == null) {
            System.out.println("Could not retrieve information for \'"
                + targetLocation + ".\' Does it exist?");
            System.exit(1);
        }
        System.out.println(targetStatus.toString());
    }
}

```

```
#!/bin/bash
JSON=$(curl -s https://waitz.io/live/ucsd)
if [[ ! $? -eq 0 ]]; then
    echo "There was an error using cURL to download the latest Waitz data."
    exit 1
fi
if [[ ! -f WaitzTracker.java ]]; then
    echo "Could not find source code (WaitzTracker.java)!"
    exit 1
fi
if [[ ! -f WaitzTracker.class ]]; then
    javac WaitzTracker.java
    if [[ ! $? -eq 0 ]]; then
        echo "Failed to compile source code (WaitzTracker.java)!"
        exit 1
    fi
fi
if [[ ! $# -eq 1 ]]; then
    java WaitzTracker
else
    java WaitzTracker "$1" "$JSON"
fi
if [[ -f WaitzTracker.class ]]; then
    rm WaitzTracker.class
fi
```

Some examples of it working (and not working):

![EX1](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/ex1.jpg)
![EX2](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/ex2.jpg)
![EX3](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/ex3.jpg)
![EX4](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week9/ex4.jpg)

JSON is hard, and I'm lazy... okay?


# Reflection

