### Users - Groups - Invitations (admin only)

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-1.png?raw=true)

“Users” tab accumulates all the users registered in one project.

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-2.png?raw=true)

The table of users consists of the following fields:
* ID (the index number of the user - the number is assigned to a user by the order of registration).
* Username (can it be changed?)
* E-mail (the one that was used to register in the system)
* First/Last name (editable through settings via admin or user)
* Status (Active/Pending/???)
* Source (Internal/LDAP)
* Registration date + The time of the last successful login

The search is available through all of the above-mentioned fields.

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-3.png?raw=true)

More options menu ⋮

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-4.png?raw=true)

1. Edit profile
Admin can edit all the profiles within one project. A user can edit their own profile.

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-5.png?raw=true)

To deactivate a user, click on the corresponding button and confirm your action in the pop-up:

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-6.png?raw=true)

2. Change password - not available for users registered via LDAP!

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-7.png?raw=true)

The password must contain from 5 to 50 characters. To see the password, click on the eye icon and hold it.
Click “Apply” when finished.

3. Performance - redirects to the “Dashboards” page and the “Personal” tab where the performance is displayed: Personal total rate; Total tests (Man-hours); Monthly test implementation progress.

Creating a new user
This is one of two ways of registering users in the system (another one is via LDAP integration - flow??)

Navigate to the “+New user”  button in the upper-right corner of the window:

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-8.png?raw=true)

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-9.png?raw=true)

Username and Password would further be used to log in the system. Note, that username can not be changedю

Groups (super admins only)

All the users on a single project are divided into several groups with certain permissions. The groups are admins, super admins, and users.

Permissions for admins (ROLE_ADMIN):

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-10.png?raw=true)
![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-11.png?raw=true)

The permissions for users (ROLE_USER) are practically the same, excluding the Users and Invitations options - they can’t be set for any of users. 
 
The permissions are set and edited by the space admin/superadmin?? .
To add a user to any group, just type their username/email in the corresponding field and click enter. The pop-up message in the lower-right corner of the window will appear. 

User "ldapuser" was added to group "Users"

Deleting a user: click on the X next to the user’s name. Note, that there would be no confirmation pop-ups for these actions.

User "ldapuser" was deleted from group "Users"

More options menu ⋮

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-12.png?raw=true)

Editing the group
You will be shown the window above with all the combinations of permissions. Press “Update” button to save the changes. If you click X to exit (or click on the space somewhere within the window) - the window would be closed, the changes you’ve made would not be saved.

Deleting the group
Confirm the action in a pop-up:

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-13.png?raw=true)

Invitations

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-14.png?raw=true)

The search is available by email only.

New users

There are two types of creating a new user in Zebrunner: internal and via LDAP.
To invite a new user, press the “+INVITE USER” button in the upper-right corner of the window:

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-15.png?raw=true)

Define the group and the role for a new user, and enter the email:

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-16.png?raw=true)

A pop-up message indicates that the email was sent or an error occurred.

Invitation was sent.

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-17.png?raw=true)

The user will be redirected to the credentials page, where it’s necessary to set the username and the password which would later be used for logging in.
 
LDAP

More options menu ⋮

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-18.png?raw=true)

Copy link - the link with the access token is copied to clipboard??

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-19.png?raw=true)

Resend - the invitation is sent once again. 
Confirm the action in the pop-up:

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-20.png?raw=true)

Revoke - the token sent in the invitation is revoked // the user would not be allowed to enter the system.
 Confirm the action in the pop-up:

![alt text](https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnn-usergroups-21.png?raw=true)
