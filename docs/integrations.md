# Integration

Zafira is integrated with multiple 3rd-party tools like Amazon, Slack, Gmail etc. You are able to add new integration in working Zafira without restart of web application. Navigate to **Username > Integrations** in top navigation menu:

<p align="center">
  <img src="../img/menu_profile.png">
</p>

On the integrations page you will find multiple blocks responsible for different integration modules. Pay attention to the fact that security model HTTP/HTTPS may affect your integration URL.

<p align="center">
  <img src="../img/feature_integrations.png">
</p>


#### Amazon
Zafira uses integration with Amazon S3 service to store user profile photos and company logo. First of all we will need to create new S3 bucket and generate access token to provide API access to Amazon S3 from Zafira.

* Create new S3 bucket in region you prefer 
* Create new IAM user with **Programmatic access**
* Grant read/write permissions for new user (read [detailed guide](https://aws.amazon.com/blogs/security/writing-iam-policies-how-to-grant-access-to-an-amazon-s3-bucket/))
* Generate new access/secret keys
* Turn on Amazon integration and provide access/secret keys and name of the bucket you've created
* Press save and refresh the page, green light indicates correct integration status

<p>
  <img style="border: 1px solid grey;" width="210px" height="310px" src="../img/int_amazon.png">
</p>


#### Email
Zafira provides functionality for sending test results, widgets and dashboards via email. You have to specify correct SMTP credentials to enable this feature. We are mostly using Gmail for that purposes. You can use configuration below replacing email and password with your valid Gmail credentials.

* Turn on Email integration
* Specify correct SMTP host and port
* Specify valid Gmail credentials 
* Press save and refresh the page, green light indicates correct integration status

<p align="center">
  <img style="border: 1px solid grey;"  width="210px" height="310px" src="../img/int_gmail.png">
</p>

#### Jenkins
Jenkins integration is used for triggering new builds and collecting test jobs configuration during the startup. Also Zafira provides remote debug with Jenkins integration enabled. For Jenkins integration follow the next steps:
* Create user with READ,RUN access for jobs
* Generate access token (read [instruction](https://support.cloudbees.com/hc/en-us/articles/115003090592-How-to-re-generate-my-Jenkins-user-token))
* Paste Jenkins URL, username and token to Zafira
* Press save and refresh the page, green light indicates correct integration status

<p align="center">
  <img style="border: 1px solid grey;"  width="420px" height="620px" src="../img/int_jenkins.png">
</p>

#### Jira
Jira integration allows to track known issues status for failed test cases. When you assign known issue to 


<p align="center">
  <img style="border: 1px solid grey;" width="420px" height="620px" src="../img/int_jira.png">
</p>

#### LDAP

<p align="center">
  <img style="border: 1px solid grey;" width="420px" height="620px" src="../img/int_ldap.png">
</p>
