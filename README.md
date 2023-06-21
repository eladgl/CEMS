# CEMS
##Centralized Exam Managing System

###This project is built with a FullStack approach. We have a remote server managing a database called CEMS. We also have three types of clients that can connect: Students, Lecturers, and Heads of Departments.

Following are the operations each can perform:

**Lecturers:**
- Have their own question and test banks that they can manage.
- Can take questions and exams from other banks and reuse them.
- Open exams for conduct, setting a password for them.
- Grade exams.
- Ask the head department for a time extension for an exam (only once).
- Can be related to many courses and subjects.
- Can stop an exam that is currently being conducted.

**Heads of Departments:**
- See statistics comparing lecturers with other lecturers, students with other students, and tests with other tests - view histograms, means, and medians.
- Approve time extensions for tests and also reject them.
- Look at tests and questions that exist in the database.

**Students:**
- Take exams. This is separated into two types: Computerized exams and manual exams.
  - For manual exams, the student must download the exam, convert raw data from the database into a Word document, and then upload it.
  - Computerized exams only ask the user to insert their ID.
- View their grades.
- Get a copy of a test in Word format.
- There is an SMS simulation that sends an SMS message to the student when a grade has been approved.

Also, there is a registration simulation that can be done to register a user into the system. There is no validation that checks if a user can be a head of department, lecturer, etc.

This entire project is built upon an OCSF project, taken from [source]. It utilizes their abstract OCSF Server-Client model and implements our own version.


## Here are some screens from the project:
### The server:
![image](https://github.com/eladgl/CEMS/assets/59554824/f243d3b6-aa0e-4751-a80a-e95eaf53c626)

When someone logs in, it looks like:
![image](https://github.com/eladgl/CEMS/assets/59554824/6eb52082-d7de-421c-93f0-16f3dcf3410c)

Login page:
![image](https://github.com/eladgl/CEMS/assets/59554824/837b56b1-c681-49b0-b77b-f0f53922e624)

Head Department view:
![image](https://github.com/eladgl/CEMS/assets/59554824/2ac9d9f3-a186-4903-a0de-29818f17acfa)

Here, we can see the various options a Head Department has.

Lecturer view:
![image](https://github.com/eladgl/CEMS/assets/59554824/7657d3be-8043-461e-8c1e-b3b896c4fdad)

Student view:
![image](https://github.com/eladgl/CEMS/assets/59554824/18bdc31b-2d07-48c7-aba7-bf1d5c9c6fa3)

Here, we can see an example of a test being done in a computerized way:
![image](https://github.com/eladgl/CEMS/assets/59554824/b7ae2fbf-d2aa-4e41-b784-3b6ad585ed18)

And here, for example, we can see a view of tests being conducted:
![image](https://github.com/eladgl/CEMS/assets/59554824/b7c555ba-3e96-4868-b5f2-45b2a5efc3d6)

Next is a view of the statistics that the Head Department can see, where almost everything is *clickable* to get more information!
![image](https://github.com/eladgl/CEMS/assets/59554824/cad4df85-40fb-49b3-84da-fae0e6508196)

Lastly, a view to see how one can approve time extensions:
![image](https://github.com/eladgl/CEMS/assets/59554824/1c25d45e-8138-4a10-81ca-61b240b25022)

A few things worth noting about this project:
1. The connection to the server is implemented as a singleton.
2. We used the command design pattern, where each click that required information from the database triggered a message handle method event that looked for the specific command.

The entire design used a FullStack approach with TCP/IP protocols based on the OCSF implementation. We utilized JavaFX 17 jars and jars for creating Word documents.
