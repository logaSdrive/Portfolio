From:
Alexander Zhilokov


Working model representation:

http://3.17.73.191:8080/Store/login.html

To get logged:

names        passwords  types (Case sensetive!):

admin         11111     Admin    - super admin
admin         1234      Admin    - admin
Avi             1       Customer - some customer
Goldbricks     111      Company  - some company  		   


------------------------------------------------------------------------------------
Source files:

coreSystem - provides all interactions needed for dialoge with database (mysql)

Server - provides all interactions needed between admins, supliers, regular customers and the server.

Simplex - some tool which i wrote to help myself with the project and which may be usefull for a general purposes.

Simplex.doc.txt - short tutorial about Simplex (that tool).

Readme.txt - you know.
