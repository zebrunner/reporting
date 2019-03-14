# Client setup 

#### Access token
Zafira provides REST API to track test automation results (use [Swagger](http://localhost:8080/zafira-ws/swagger-ui.html) to learn API). You have different options of integration of your test client. [Carina automation framework](https://github.com/qaprosoft/carina) has integration with Zafira under the hood. Regardless of integration flow you selected, first of all you will need access token generated. Navigate to **Username** > **Profile** in top navigation menu:

<p align="center">
  <img src="../img/menu_profile.png">
</p>

Scroll down and generate new access token:

<p align="center">
  <img src="../img/access_token.png">
</p>

Zafira uses stateless authentication using [JWT](https://en.wikipedia.org/wiki/JSON_Web_Token) technology. In general clients stores refresh token that is used to generate access token with 5-hours expiration term. 
```
POST /api/auth/refresh
{
  "refreshToken": "<access_token>"
}

Response:
{
  "type": "Bearer",
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMi9...",
  "expiresIn": 300
}
```

All HTTP calls that requires authorization context should contain header:
```
Authorizarion: Bearer <auth_token>
```

#### Integration with Carina
Carina automation framework tacks test results in Zafira by default, all you need is valid access token in [zafira.properties](https://github.com/qaprosoft/carina-demo/blob/master/src/main/resources/zafira.properties) file.
```
zafira_enabled=true
zafira_service_url=http://localhost:8080/zafira-ws
zafira_access_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWI...
zafira_project=UNKNOWN
zafira_rerun_failures=false
zafira_report_emails=
zafira_configurator=com.qaprosoft.carina.core.foundation.report.ZafiraConfigurator
```
Verify following properties:

* zafira_enabled=true
* zafira_service_url=YOUR_ZAFIRA_URL

By default **zafira_project=UNKNOWN** but using admin user you are capable to create multiple projects via Zafira UI (Top menu > Project > Create). When new project is created you may override **zafira_project** property and track results in appropriate context.

<p align="center">
  <img src="../img/flow_uml.png">
</p>

#### Integration with TestNG
If you are implementing your own TestNG-based automation project you can easily setup integration with Zafira using [TestNG listener](https://github.com/qaprosoft/zafira/blob/master/sources/zafira-client/src/main/java/com/qaprosoft/zafira/listener/ZafiraListener.java).

* Add Zafira client as Maven dependency:

```
<dependency>
    <groupId>com.qaprosoft</groupId>
    <artifactId>zafira-client</artifactId>
    <version>latest</version>
</dependency>
```

* Create [zafira.properties](https://github.com/qaprosoft/carina-demo/blob/master/src/main/resources/zafira.properties) and place in resource folder, update configuration
* Include com.qaprosoft.zafira.listener.ZafiraListener as TestNG listener:
```
<suite>
    [...]
      <listeners>
        <listener class-name="com.qaprosoft.zafira.listener.ZafiraListener"/>
      </listeners>
    [...]
</suite>
```

or

```
@Listeners({ZafiraListener.class})
public class LoginTest {
[...]
}
```

#### Languages and frameworks supported
* [Java - TestNG guide](https://github.com/qaprosoft/zafira-testng)
* [Ruby - Cucumber](https://github.com/qaprosoft/zafira-ruby#cucumber-usage)
* [Ruby - RSpec](https://github.com/qaprosoft/zafira-ruby#rspec-usage)
* [C# - NUnit](https://github.com/qaprosoft/zafira-nunit)
