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

As `pwd` shows, our starting directory is `/home`. The `cd` command here attempts to move working directories to the path it recieved as an argument, but first resolves the `./` to be the current directory. After running the command, `pwd` shows that we are now in the `/home/lecture1` directory.

When attempting to `cd` with a file path as it's argument, we get the following error. This makes sense, as a file is NOT a directory. It makes sense to change your directory into another directory, but not to change it into a file!

```
[user@sahara ~]$ cd lecture1/Hello.java 
bash: cd: lecture1/Hello.java: Not a directory
[user@sahara ~]$ 
```
## "ls" Command
## "cat" Command
