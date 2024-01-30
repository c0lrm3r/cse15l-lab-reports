_See https://c0lrm3r.github.io/cse15l-lab-reports/labs/week1_report_
_2024-01-10_  
_CSE 15L Lab_  
_Week 1 Report_  

# Week 1 Lab Report

Here we go!

## "cd" Command

The command without arguments simply returns the user to their home directory. See the example below:

```
[user@sahara ~/lecture1/messages]$ pwd
/home/lecture1/messages
[user@sahara ~/lecture1/messages]$ cd
[user@sahara ~]$ pwd
/home
[user@sahara ~]$
```

In this example, our original working directory was `/home/lecture1/messages`. After running the `cd` command, it brought us back to the set `/home` directory, which serves as the "default" directory and is used to replace `~/` when resolving a path. This is no error!

Moving on, when `cd` is given a directory path as an argument (relative or absolute), it attempts to set the user's current working directory to the path that it resolved. See the example below:

```
[user@sahara ~]$ pwd
/home
[user@sahara ~]$ cd ./lecture1/
[user@sahara ~/lecture1]$ pwd
/home/lecture1
[user@sahara ~/lecture1]$ 
```

As `pwd` shows, our starting directory is `/home`. The `cd` command here attempts to move working directories to the path it recieved as an argument, but first resolves the `./` to be the current directory. After running the command, `pwd` shows that we are now in the `/home/lecture1` directory. This is correct functionality and not an error.

When attempting to `cd` with a file path as it's argument, we get the following error. This makes sense, as a file is NOT a directory. It makes sense to change your directory into another directory, but not to change it into a file!

```
[user@sahara ~]$ cd lecture1/Hello.java 
bash: cd: lecture1/Hello.java: Not a directory
[user@sahara ~]$ 
```
## "ls" Command

Moving on, we have the `ls` command, which **L**i**S**ts directories and files. It is one of the main ways that we- human people- are able to explore the filesystem through a terminal. Below is an example of running the command with no arguments:

```
[user@sahara ~]$ pwd
/home
[user@sahara ~]$ ls
lecture1
[user@sahara ~]$
```

Notice that nothing has changed, we are still in the `/home` directory. All the command has done is list the contents of the current directory, and this is what it is supposed to do when there are no arguments. We can specify a specific directoy to list by passing a relative (or absolute) path to the command as an argument. See below:  

```
[user@sahara ~]$ pwd
/home
[user@sahara ~]$ ls lecture1/
Hello.class  Hello.java  messages  README
[user@sahara ~]$ 
```

Now, instead of printing the contents of the current directoy, `ls` has printed the contents of the `lecture1/` directory as we had told it to. Our current working directory remains unchanged. However, if we specify a _file_ to list instead of a directory, `ls` will simply list the name of that file, if it exists. This is also correct functionality, and does not indicate any errors.

```
[user@sahara ~]$ pwd
/home
[user@sahara ~]$ ls lecture1/Hello.java 
lecture1/Hello.java
[user@sahara ~]$ 
```

If the file (or directory) argument DOESN'T exist, the command produces an error message like the one below, indicating that it could not find the path specified.

```
[user@sahara ~]$ ls Shrek-5.TorrentGalaxy.tz.mp5
ls: cannot access 'Shrek-5.TorrentGalaxy.tz.mp5': No such file or directory
[user@sahara ~]$ 
```

_Although it would be pretty great if there was a Shrek 5, huh?_

## "cat" Command

(woof)  

The `cat` command is used to print the contents of one or more files to the terminal. In that way, it can con**CAT**inate multiple files together. However, if the command is run without a file path specified, it simply echos the Standard Input (stdin) stream to the terminal. See below:

```
[user@sahara ~]$ pwd
/home
[user@sahara ~]$ cat
abcdefg
abcdefg
1234567
1234567
^C
[user@sahara ~]$ pwd
/home
[user@sahara ~]$

```

Note how the strings "abcdefg" and "1234567" are printed twice. `cat` is simply echoing them back to the user. Note that `cat` does not change the current working directory at all. However, if a directory (not a file) is specified, the command will return the following error:

```
[user@sahara ~]$ pwd
/home
[user@sahara ~]$ cat lecture1/
cat: lecture1/: Is a directory
[user@sahara ~]$ pwd
/home
[user@sahara ~]$
```

The command simply whines and doesn't do anything, since a directory cannot be opened and read in the same way that a file can. If a file was specified as an argument (the correct usage of the command), then it simply prints the contents of that file to the terminal. This is correct usage of the command:

```
[user@sahara ~]$ pwd
/home
[user@sahara ~]$ cat lecture1/messages/en-us.txt 
Hello World!
[user@sahara ~]$ pwd
/home
[user@sahara ~]$ 
```

This is useful, as `cat` is much lighter and simpler than a text editor, and allows for a preview of a file without modifying it or your working directory.

_Still no actual cats, though :(_
