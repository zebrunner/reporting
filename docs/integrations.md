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
  <img style="border: 1px solid grey;" width="280px" height="420px" src="../img/int_amazon.png">
</p>


#### Email
Zafira provides functionality for sending test results, widgets and dashboards via email. You have to specify correct SMTP credentials to enable this feature. We are mostly using Gmail for that purposes. You can use configuration below replacing email and password with your valid Gmail credentials.

* Turn on Email integration
* Specify correct SMTP host and port
* Specify valid Gmail credentials 
* Press save and refresh the page, green light indicates correct integration status

<p>
  <img style="border: 1px solid grey;"  width="280px" height="420px" src="../img/int_gmail.png">
</p>

#### Jenkins
Jenkins integration is used for triggering new builds and collecting test jobs configuration during the startup. Also Zafira provides remote debug with Jenkins integration enabled. For Jenkins integration follow the next steps:

* Create user with READ,RUN access for jobs
* Generate access token (read [instruction](https://support.cloudbees.com/hc/en-us/articles/115003090592-How-to-re-generate-my-Jenkins-user-token))
* Paste Jenkins URL, username and token to Zafira
* Press save and refresh the page, green light indicates correct integration status

<p>
  <img style="border: 1px solid grey;"  width="280px" height="420px" src="../img/int_jenkins.png">
</p>

#### Jira
Jira integration allows to track known issues status for failed test cases. When you assign known issue to contstantly failing test cases you may specify appropriate Jira ticket. In Jira integrationg is inabled, Zafira will check current ticket status and track failure as known issus if ticket opened and as unknown if it is closed, so you will never loose regression bugs.

* Paste Jira URL, username and password to Zafira
* List set of statuses that indicates that ticket is closed
* Press save and refresh the page, green light indicates correct integration status

<p>
  <img style="border: 1px solid grey;" width="280px" height="420px" src="../img/int_jira.png">
</p>

#### LDAP
Zafira supports LDAP authentication, in this case on first success login via LDAP Zafira will register user details in own database. Admin will be able to manage user permissions for every new user came via LDAP. 

* Use configuration below as a reference for your LDAP connection setup
* Press save and refresh the page, green light indicates correct integration status

<p>
  <img style="border: 1px solid grey;" width="280px" height="420px" src="../img/int_ldap.png">
</p>


#### SLACK
Zafira is capable to post automation results into specific Slack channels for better visibility.
If integration is set up right after test run is finished notification with run details will be sent into appropriate channel. Such  Slack notification contains base information on test run and also includes links to this run in Zafira and Jenkins.
After user marks some run as reviewed and Slack integration is configured for executed Jenkins job user will be proposed to send to Slack notification about reviewed run.
In order to setup Slack integration follow the next steps:

* Generate Slack web hook url and add it as parameter SLACK_WEB_HOOK_URL into SLACK block at Zafira integrations page
* For each Jenkins job you need integration for add parameters in Zafira using next pattern: **SLACK_NOTIF_CHANNEL_real_channel_name=JENKINS_JOB_1;JENKINS_JOB_2** where
  * real_channel_name - name of Slack channel to post notifications to
  * JENKINS_JOB_1 and JENKINS_JOB_2 - names of Jenkins jobs
* You may add as many integration as you need. For each new Slack channel create new parameter in SLACK section in Zafira
* Press save and refresh the page, green light indicates correct integration status

<p>
  <img style="border: 1px solid grey;" width="574px" height="236px" src="../img/int_slack.png">
</p>
