imap-dupe-delete
================

Deletes duplicate mails within a specified IMAP folder. If no folder is specified, then list all
folders.

Background
----------

I built this program for my own use and thought it might be useful for others. _Use it at your
own risk_. In my case, my server was using the Dovecot implementation on a Linux server. I didn't do
anything special outside of standard IMAP usage, though, but from what I hear, GMail's IMAP is a
little different than normal IMAP. I've not tested against GMail so use caution there.

I started this program because I have some problem with Thunderbird that when I move 1000s of
messages between folders, for some messages I get 2 or 3 duplicates of each. I haven't figured out
why or when this occurs, but it has each time I've done this (a few times). I wrote this program to
clean up the mess.

Usage
-----

Use Maven to build and run the application. There are 5 arguments, the last is optional:

- Protocol (imap or imaps)
- Hostname (server name)
- user name
- password
- folder name

If folder name is left out, the program lists folders.

When all 5 parameters are given, the program operates full. But efore deleting messages, the program
will display a summary of the number of messages in the folder, the number of duplicates, and give
you a chance to list the headers of the messages to be deleted. Only after the "delete" command is
entered are the messages deleted.

### Example

List folders:

```shell
mvn clean compile exec:java -Dexec.args="imaps mailhost.example.com johndoe password123"
```

Detect and delete duplicates on "INBOX.Archives":

```shell
mvn clean compile exec:java -Dexec.args="imaps mailhost.example.com johndoe password123 INBOX.Archives"
```

Other Uses
----------

This application is a pretty simple example of JavaMail usage. Since it iterates folders and
scans messages, it could be altered to delete or find messages based on any criteria you can code.

License
-------

Apache 2 license (http://www.apache.org/licenses/LICENSE-2.0)