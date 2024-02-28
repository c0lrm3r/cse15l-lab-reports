_2024-02-27_  
_CSE 15L Lab_  
_Week 7 Report_  
_See https://c0lrm3r.github.io/cse15l-lab-reports/labs/week7_report_  

# Week 7 Lab Report

Gotta go fast!

## Part One (1)

First, we need to SSH in to ieng6. We do this below:

```
thonkpad@thonkpad:~$ ssh colarmer@ieng6-202.ucsd.edu
Last login: Tue Feb 27 15:03:23 2024 from 130.191.100.69
Hello colarmer, you are currently logged into ieng6-202.ucsd.edu

You are using 0% CPU on this system

Cluster Status 
Hostname     Time    #Users  Load  Averages  
ieng6-201   21:35:01   23  0.25,  0.23,  0.34
ieng6-202   21:35:01   25  0.03,  0.09,  0.14
ieng6-203   21:35:01   16  0.84,  0.38,  0.28


To begin work for one of your courses [ cs15lwi24 ], type its name 
at the command prompt.  (For example, "cs15lwi24", without the quotes).

To see all available software packages, type "prep -l" at the command prompt,
or "prep -h" for more options.
[colarmer@ieng6-202]:~:370$ 
```

The exact keys pressed to do this are `ssh colarmer@ieng6-202.ucsd.edu<Enter>`. This was typed in manually, and I specified ieng6-202, because while doing this assignment I ran in to an issue where `javac` didn't exist on ieng6-203 :(.

## Part Two (2)

Next, we need to clone our repository fork on to ieng6. For this, we will be
using GitHub's SSH authentication, and will get the repo URL as follows:

![Grabbing the repo URL](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week7/get_git.jpg)

Clicking on the button (or selecting the URL and using <Ctrl>+C), we can copy the address and use it in our command line. To do this, we type `git clone <Ctrl>+<Shift>+V<Enter>`. This pastes the context of our copy-paste buffer after `git clone`. The result of this command can be seen below:

![Cloning the Repo](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week7/git_clone.jpg)

It is important to note that using SSH authentication will save us from a headache, as we won't need to manually authenticate later on.

## Part Three (3)

Then, we need to enter our newly cloned directory. Because there are only a few files in the working directory of my ieng6 account, I can cheat and use `<Tab>` to make things go faster. The exact string of keys I pressed was: `cd la<Tab><Enter>`. This autocompletes 'la' to 'lab7/', giving us the result below:
![Cloning the Repo](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week7/cd.jpg)

## Part Four (4)

Now, it's time to run the tests (they should fail). In the `cd/` directory, the exact keypresses I used were: `bash t<Tab><Enter>`. There is only one file in lab7/ that starts with the letter 't', so autocomplete quickly finds the desired test.sh file:

![Running the failing test](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week7/test_fail.jpg)

## Part Five (5)

Ah, so it failed! Well, because I am simply the best computer scientist, I already know that the bug is located at line 44 in ListExamples.java. To quickly fix this, we use Vim to edit the file. There are multiple files in the repo that start with "ListExample", and autocomplete will only complete up to the first difference in these file names (Which just happens to be 'ListExamples'). The exact keypresses to open the correct file in Vim are: `vim ListE<Tab><Enter>`. This is just *a little* faster than manually typing out the full name.  

Now that we're in Vim, we need to efficiently patch the bug. To do this, I used the following sequence of commands: `G 6k e r2 :wq!`. See below for an explanation:

![How do I exit vim???](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week7/vim_annotated.jpg)

`G` directs vim (in normal mode) to the end of the file, then `6k` moves the cursor up 6 lines, to `index1 += 1;` (Line 44). `e` tells vim to move the cursor to the last character of the word right of the cursor, and then `r2` replaces that character with '2'. On this line, it moves the cursor to the 1 in `index1`, and then replaces it with 2. Finally `:wq!` saves the file and exits vim. 

## Part Six (6)

With our bug patched, it's time to re-run the tests to make sure. It's at moments like this that I really appreciate the existence of shell command histories. If you remember, we just ran the test command before we ran Vim, so we should be able to navigate back to it fairly easily. For this, we simply type the keys: `<Up><Up><Enter>`, which navigates to `bash test.sh` and re-runs it:

![Unlike my midterms, these tests passed](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week7/test_pass.jpg)

Hooray! They passed! Time to commit and push this patch to the git repo.

## Part Seven (7)

After all is done, we need to create a new commit, and then later push it to GitHub. Before any of that, we first need to tell git to track our new changes. To do this, we type `git add -A<Enter>` to tell git to track *everything* (because we're lazy). Then, we can create a new commit with a descriptive message through: `git commit -m "Waffles"<Enter>`. This tells git to store our changes in a new commit with the message of "Waffles". So far, everything that git has done has been in our local repository. To reflect these changes on GitHub, we need to push them to the remote repository with `git push<Enter>`. This is where using SSH authentication is nice, as it doesn't prompt us for a username or password. See below for the output of these commands:

![Saving our changes with git](https://raw.githubusercontent.com/c0lrm3r/cse15l-lab-reports/main/res/week7/push_git.jpg)

Now, GitHub reflects the changes we have made in this Lab.

## Final Words

With that, we have completed our assigned tasks, and another day of grueling CS work has passed. Is this really worth a six-figure salary? Probably. Anyways, after a few attempts, I was able to get this entire process down to under 30 seconds (27.721 seconds, but who's counting?). There are probably tricks to go faster, however I would like to refer to this quote by a genius regarding speed:

```
        The only problem with being faster than light is 
                      that you can only live in darkness.

                              -- Sonic the Hedgehog, Year unknown
```

