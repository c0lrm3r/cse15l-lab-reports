_See https://c0lrm3r.github.io/cse15l-lab-reports/labs/week5_report_  
_2024-02-13_  
_CSE 15L Lab_  
_Week 5 Report_  

# Lab Report 3 (Week 5)

## Part 1 (Bugs)

As specified in the report outline, I will be using one of the buggy programs fromweek 4's lab. The specific example that will be used below is the broken LinkedListExample implementation.


To properly test the given implementation, I wrote test cases for each method; all tests created and populated the list independently of any method being tested, as to not skew results. Then, on compiling and running the tests, all passed except for one, which instead froze the program and required me to Ctrl+C to kill it. Below is the specific unit test that caused the program to crash:

```
@Test
public void testAppendThree() {
    LinkedList list = new LinkedList();
    list.append(0);
    list.append(1);
    list.append(2);
    assertEquals(0, list.root.value);
    assertEquals(1, list.root.next.value);
    assertEquals(2, list.root.next.next.value);
}
```

This test tests the `LinkedList.append(int value)` method, which should append the specified value as a new element at the end of the list. However, something is causing it to loop indefinitely. When running the tests, it ended up consuming ~7GB of my laptop's memory before I noticed; very good code. See below for the symptoms:

![Junit eating my memory](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week5/screen1.jpg)


It is of note that two other test cases for this method exist and did not fail:

```
@Test
public void testAppendEmpty() {
    LinkedList list = new LinkedList();
    list.append(0);
    assertEquals(0, list.root.value);
}
```

And

```
@Test
public void testAppendOne() {
    LinkedList list = new LinkedList();
    list.append(0);
    list.append(1);
    assertEquals(0, list.root.value);
    assertEquals(1, list.root.next.value);
}
```

These tests attempt to append to the list when the list is empty and when it only contains one previous element, respectively. In both these cases, the end of the list can be found without traversing it, and thus do not require a loop to run. With this knowledge, we can pry into the implementation of LinkedList to find the bug. Below is the relevat method:

```
/**
 * Adds the value to the _end_ of the list
 * @param value
 */
public void append(int value) {
    if(this.root == null) {
        this.root = new Node(value, null);
        return;
    }
    // If it's just one element, add if after that one
    Node n = this.root;
    if(n.next == null) {
        n.next = new Node(value, null);
        return;
    }
    // Otherwise, loop until the end and add at the end with a null
    while(n.next != null) {
        n = n.next;
        n.next = new Node(value, null);
    }
}
```

Our previous test cases have narrowed it down to an issue with the loop in this method, so we will examine that piece of code specifically. Before the loop is entered, *n* is set to the root of the list (let's call it node 0). Then, as long as the node after *n* (`n.next`) is not null, the method *should* traverse the list until it hits the end, in which it should append the node. Instead, it begins traversing the list, but begins inserting a new node after every next node. See the table below:

| Iteration   | n           | n.next      | n.value |
|-------------|-------------|-------------|---------|
| 0 (initial) | node 0      | node 1      | 0       |
| 1           | node 1      | new Node(2) | 1       |
| 2           | new Node(2) | new Node(2) | 2       |
| ...         | ...         | ...         | ...     |
| ∞           | new Node(2) | new Node(2) | 2       |

So this is where our infinite loop comes from, and the fix appears to be quite simple. the line `n.next = new Node(value, null);` should not be within the loop, but instead after it. This way, the method will traverse to the end of the list, and then append the new node to the end. Below is the updated append() method:
```
/**
 * Adds the value to the _end_ of the list
 * @param value
 */
public void append(int value) {
    if(this.root == null) {
        this.root = new Node(value, null);
        return;
    }
    // If it's just one element, add if after that one
    Node n = this.root;
    if(n.next == null) {
        n.next = new Node(value, null);
        return;
    }
    // Otherwise, loop until the end and add at the end with a null
    while(n.next != null) {
        n = n.next;
    }
    n.next = new Node(value, null);
}
```

This change fixes the infinite loop issue, as now there is a definite null reference at the end of the list, and it won't be overwritten by a new node every time the loop runs.

## Part 2: (Commands)

### The `grep` command

#### Method 1: Non-Matching filenames
Source: https://linuxhandbook.com/grep-command-examples/

We know that `grep` can be used to find files with lines that match a specific pattern, however we can also invert it to display files that do NOT match the given pattern. We can do this with the `-L` flag. See below:

```
# Note: directory and usernames have been altered for brevity. 
writeup@example:~/docsearch/technical$ pwd
/home/writeup/docsearch/technical
writeup@example:~/docsearch/technical$ grep "cheese" -r plos/
plos/journal.pbio.0020146.txt:        procedures, such as those for producing cheeses and wines, all of which produced foodstuffs
plos/journal.pbio.0020306.txt:        division. ‘The shell is rather like a Camembert cheese box or a petri dish’, explains
writeup@example:~/docsearch/technical$
```

This is the output without `-L`, the 'normal' output. Now, see the below example with `-L`:

```
writeup@example:~/docsearch/technical$ pwd
/home/writeup/docsearch/technical
writeup@example:~/docsearch/technical$ grep "cheese" -rL plos/
plos/pmed.0020274.txt
plos/pmed.0020257.txt
plos/pmed.0020065.txt
... Many, many lines omitted ...
plos/pmed.0020281.txt
writeup@example:~/docsearch/technical$
```

As you can see, `grep` has now returned a list of files that do not contain "cheese" in them. It is a very long list... Let's try this on a different set of files. If we search the files in `technical/911report/` for files that mention the 'CIA', but invert it with `-L`, we get these few files:

```
writeup@example:~/docsearch/technical$ pwd
/home/writeup/docsearch/technical
writeup@example:~/docsearch/technical$ grep "CIA" -rL 911report/
911report/chapter-2.txt
911report/chapter-12.txt
911report/chapter-9.txt
911report/chapter-7.txt
writeup@example:~/docsearch/technical$ 
```

What the command is doing when `-L` is specified is essentially suppressing the normal output and returning what is otherwise ignored. That way, we don't see any instances of our search string appearing in text, but we do see files that explicitly don't contain said search string. This could be useful when traversing a codebase, and attempting to find files that-- for some reason-- do not reference important confirguration fields. Perhaps there is a UI element that isn't properly changing colors along with everything else, you could use `grep -L ...` to list the source files which do not include code to update their colors.

#### Method 2: Invert a string search
Source: https://docs.oracle.com/cd/E19455-01/806-2902/6jc3b36dn/index.html

But what if you want to see every *line* that does not include a search term? The above method only displays files that do not contain a term, which doesn't work if you are attempting to search a large file containing important information, such as a database in a CSV format.

That is where the `-v` flag becomes useful. Instead displaying lines of a file that match a search pattern, it displays all lines that *do not* match the search pattern. See below:

```
writeup@example:~/docsearch/technical/plos$ grep -i "the" pmed.0020191.txt
        The excellent article by Jordan Paradise, Lori B. Andrews, and colleagues, “Ethics.
        Constructing Ethical Guidelines for Biohistory” [1], neither advocates nor argues against
        taking place without guidelines—ethical, scientific, moral, or religious. The question
        permission? Who is to decide what is “historically significant”? Not to mention the
        meta-question: who is to decide who is to decide? I apologize to the authors if my brief
        comments [2] implied that they took a position on this issue.
writeup@example:~/docsearch/technical/plos$ grep -iv the pmed.0020191.txt
... Omitted blank lines ...
        biohistorical research; instead, it points out that such investigations are currently
        remains: if such guidelines were to be established, what individuals, institutions,
        governments, medical examiners, family members, or intrepid biographers are to be given
... Omitted blank lines ...
writeup@example:~/docsearch/technical/plos$ pwd
/home/writeup/docsearch/technical/plos
writeup@example:~/docsearch/technical/plos$ 

```

Note that several blank lines were omitted in the `-v` output, they were present in the file and got matched by the inverted filter. However, I have omitted them here for the sake of space.

Similarly, if (for some reason) we want to return all lines that do not use a certain letter, we could:

```
writeup@example:~/docsearch/technical/government/Media$ pwd
/home/writeup/docsearch/technical/government/Media
writeup@example:~/docsearch/technical/government/Media$ grep -iv "t" Barnes_Volunteers.txt 
... Omitted blank lines ...
Wednesday, December 18, 2002
lawyers.
profession," he said.
million.
... Omitted blank lines ...
writeup@example:~/docsearch/technical/government/Media$ 
```

In files such as the ones above, this option may not appear that useful. However, in combination with other commands (such as `wc`, `ls`, etc.), this function of grep can be very useful in formatting data.

#### Method 3: Match to more than one pattern
Source: https://matthews.sites.wfu.edu/misc/linux/grep/grep.html

In a case where you want to search for more than one term at once, or include variants of a term, `grep` can be configured to do this. There is actually more than one way to do this, and we will cover two. See below for the first method:

Let's say we are trying to find references to anything *unix-y* in our database of text files. We could search just for 'unix', but then what about mentions of linux? Furthermore, what if somebody mentions POSIX? We need to consider this when crafting our search pattern. Luckly, we can combine patterns into one string in grep, using "\|", which acts as an 'OR' indicator in our search. See below:

```
writeup@example:~/docsearch/technical$ grep -ri "linux\|unix\|posix" *
biomed/gb-2003-4-2-r14.txt:          length (from UNIX wc command) divided by 3 was used to
... removed lines for brevity ...
biomed/1471-2148-3-18.txt:          http://www.arb-home.de/) running on a SuSE Linux 6.2
biomed/1471-2148-3-18.txt:          this manuscript. File requires the ARB package for Linux
biomed/1471-2164-3-4.txt:        http://www.stuffit.com/, or unzip utility for Unix. The
... removed lines for brevity ...
government/Gen_Account_Office/im814.txt:Interconnection Profile, Apr. 3, 1991. FIPS PUB 151-1-POSIX:
plos/journal.pbio.0020052.txt:        Genome Project. The proponents of such models point to the success of GNU/Linux in the
writeup@example:~/docsearch/technical$ 
```

For the other method, we can use `-e` when specifying our search terms. We can specify multiple `-e <term>`s in our command, and get the same functionality as above. See below:

```
writeup@example:~/docsearch/technical$ grep -ri -e "linux" -e "unix" -e "posix" *
biomed/gb-2003-4-2-r14.txt:          length (from UNIX wc command) divided by 3 was used to
... removed lines for brevity ...
biomed/1471-2148-3-18.txt:          http://www.arb-home.de/) running on a SuSE Linux 6.2
biomed/1471-2148-3-18.txt:          this manuscript. File requires the ARB package for Linux
biomed/1471-2164-3-4.txt:        http://www.stuffit.com/, or unzip utility for Unix. The
... removed lines for brevity ...
government/Gen_Account_Office/im814.txt:Interconnection Profile, Apr. 3, 1991. FIPS PUB 151-1-POSIX:
plos/journal.pbio.0020052.txt:        Genome Project. The proponents of such models point to the success of GNU/Linux in the
writeup@example:~/docsearch/technical$ 
```

Note that the output is the same. This is because each `-e` pattern is being checked, and if any of them are met, then the line is considered to be matching. 

#### Method 4: Finding the line number
Source: https://www.makeuseof.com/grep-command-practical-examples/

We can use grep to show lines that match our search pattern, however in large files it may be difficult to find these lines. Thankfully, `grep` has a flag that will result in the line number of the match being printed along with the match itself. Let's say that we wanted to find mentions of SARS (Severe Acute Respiratory Syndrome) in the `plos/` directory, for research. By including the `-n` flag, grep will now print out the line number of every match it finds. See below:

```
writeup@example:~/docsearch/technical/plos$ pwd
/home/writeup/docsearch/technical/plos
writeup@example:~/docsearch/technical/plos$ grep -n "SARS" *
journal.pbio.0020145.txt:150:        use of DNA microarrays and sequencing to identify the causative agent for SARS (Wang et al.
journal.pbio.0020272.txt:96:        Spanish flu, SARS, and HIV have just been early experiments.
pmed.0010056.txt:84:        SARS protein similar to mRNA cap-1 methyltransferases—a class of proteins with available
pmed.0010056.txt:85:        inhibitors—was recently identified by scanning proteins encoded by the SARS genome against
pmed.0020059.txt:7:        Nile virus and SARS outbreaks, have motivated many public health departments to develop
pmed.0020232.txt:6:        Like SARS, Ebola, and other emerging infectious diseases, antibiotic resistance in
pmed.0020239.txt:6:        When the SARS epidemic showed the first signs of waning, the World Health Organization
pmed.0020239.txt:10:        models of disease spread, even though SARS was a newly emerging disease, to help plan their
pmed.0020239.txt:13:        as Ebola, SARS, and West Nile virus, and multi-drug-resistant malaria—as well as the
writeup@example:~/docsearch/technical/plos$ 
```

Now, on the left after the file name, are the exact lines where grep has found a match to our pattern. Obviously, this can be incredibly useful for tracing variables or functions across a large codebase, as 1000+ line source files can get tiresome to dig through.

As another example, what if we wanted to learn more about immigration through government media? We can use `-n` to find the specific lines in documents that include the phrases "asylum" or "immigration". See below:

```
writeup@example:~/docsearch/technical/government/Media$ pwd
/home/writeup/docsearch/technical/government/Media
writeup@example:~/docsearch/technical/government/Media$ grep -in "asylum\|immigration" *
Advocate_for_Poor.txt:16:helping the working poor navigate the legal system. Immigration,
agency_expands.txt:25:immigration, housing, public benefits and labor legal services to
Civil_Matters.txt:11:domestic violence, immigration, financial or housing problems can
Coup_Reshapes_Legal_Aid.txt:34:for domestic-violence clinics, and renewed immigration and consumer

... Several lines omitted for brevity...

Workers_aid_center.txt:40:"Immigration status is irrelevant," Cervantes said. "This a
writeup@example:~/docsearch/technical/government/Media$
```

This is useful, as it gives us precise locations in often large files, saving us both time and headaches when we actually need to open said files.
