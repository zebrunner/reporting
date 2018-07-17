# Integration

#### Amazon
Zafira uses integration with Amazon S3 service for storage of user profile photos and customer logos. First of all we will need to create new S3 bucket and generate access token to provide API access to Amazon S3 from Zafira.

* Create new S3 bucket in region you prefer 
* Create new IAM user with **Programmatic access**
* Grant AmazonS3FullAccess for new user (read [detailed guide](https://aws.amazon.com/blogs/security/writing-iam-policies-how-to-grant-access-to-an-amazon-s3-bucket/))
* Generate new access/secret keys
* Navigate to **Username > Integrations** in Zafira top navigation menu

<p align="center">
  <img src="../img/menu_profile.png">
</p>

* Turn on Amazon integration and provide access/secret keys and name of the bucket you created
* Press save and refresh the page, green light indicates correct integration

<p align="center">
  <img style="border: 1px solid grey;" width="420px" height="620px" src="../img/int_amazon.png">
</p>


#### Email
Zafira provides functionality for sending test results, widgets and dashboards via email. You have to specify correct SMTP credentials to enable this feature. We are mostly using Gmail for that purposes. You can use configuration below replacing email and password with your valid Gmail credentials.

* Turn on Email integration
* Specify correct SMTP host and port
* Specify valid Gmail credentials 
* Press save and refresh the page, green light indicates correct integration

<p align="center">
  <img style="border: 1px solid grey;"  width="420px" height="620px" src="../img/int_gmail.png">
</p>

#### Jenkins
Jenkins integration is used for triggering new builds and collecting test jobs configuration during the startup. Also Zafira provides remote debug with Jenkins integration enabled.

<p align="center">
  <img style="border: 1px solid grey;"  width="420px" height="620px" src="../img/int_jenkins.png">
</p>

#### Jira

<p align="center">
  <img style="border: 1px solid grey;" width="420px" height="620px" src="../img/int_jira.png">
</p>

#### LDAP

<p align="center">
  <img style="border: 1px solid grey;" width="420px" height="620px" src="../img/int_ldap.png">
</p>
