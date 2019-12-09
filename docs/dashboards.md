## Dashboards

  General is the main page that is displayed when you open Zebrunner.

By default, it accumulates 4 widgets - 30 days total, 30 days pass rate by platform, 30 days test details, and 30 days tests trend.

<img src="https://github.com/qaprosoft/zafira/blob/develop/docs/img/dashboards_1.png?raw=true" alt="" width="900" />

The upper bar of the window holds several icons with the actions that can be performed with the dashboard and its widgets:

* Sending the dashboard via email
* Widget placement / resizing / rearrangement
* Dashboard editing
* Adding new widget to the dashboard

#### Sending the dashboard via email 

To send the current dashboard via email:

1. Click on the arrow icon in the upper-right corner of the window:

<img src="https://github.com/qaprosoft/zafira/blob/develop/docs/img/dashboards_2.jpg?raw=true" alt="" width="900" />

2. The following pop-up would appear:

<img src="https://github.com/qaprosoft/zafira/blob/develop/docs/img/dashboards_3.jpg?raw=true" alt="" width="500" />

3. “**Name**” and “**Text**” fields are filled by default, the text is changeable. Fill in the “**Recipients**” field (unlimited number of emails per time), and press the “**Send**” button.

4. Pop-up message “**Email was successfully sent**” would appear in the lower-right corner of the window.

5. Email view of the General Dashboards page:

<img src="https://github.com/qaprosoft/zafira/blob/develop/docs/img/dashboards_4.png?raw=true" alt="" width="900" />

#### Editing the dashboard

To edit the dashboard, click on the pencil icon in the upper-right corner of the window:

<img src="https://github.com/APGorobets/mkdocks1/blob/master/images/03-01.jpg?raw=true" alt="" width="900" />


The following pop-up will appear:
<img src="https://github.com/APGorobets/mkdocks1/blob/master/images/edit_dashboards_popup.jpeg?raw=true" alt="" width="700" />

Fill in the *Key* and *Value* fields and click the "**Save**" button.

*Please, note that neither the name of the General dashboard nor "**Hidden**" status off cannot be changed - they are set by default. These options are available for custom dashboards. **(Permissions here)***


#### Creating a new dashboard

To create a new dashboard, navigate to the sidebar menu and click on the “**Dashboards**” icon and then on the “**+Add dashboard**" button:

<img src="https://github.com/APGorobets/mkdocks1/blob/master/images/05-01.jpg?raw=true" alt="" width="900" />

The following pop-up will appear:

<img src="https://github.com/APGorobets/mkdocks1/blob/master/images/new_dashboad_popup.jpeg?raw=true" alt="" width="700" />

Create the name for the dashboard, choose whether it would be hidden or seen to other users, and click the "**Create**" button.


## Working with widgets

To change the widget placement or to resize the widget, click on the “**Widgets**” icon in the upper-right corner of the window:

<img src="https://github.com/qaprosoft/zafira/blob/develop/docs/img/dashboards_5.jpg?raw=true" alt="" width="900" />

In this mode, the size of all the widgets can be changed (the arrows in the lower corners of the widget - the same principle as with the system windows):

<img src="https://github.com/qaprosoft/zafira/blob/develop/docs/img/dashboards_6.jpg?raw=true" alt="" width="500" />

All the widgets on the dashboard could be arranged the way you prefer using the drag and drop method:
<img src="https://github.com/qaprosoft/zafira/blob/develop/docs/img/widget%20placement_resize.gif?raw=true" alt="" width="900" />

Once all the necessary changes are done, click on the “**Apply**” button in the upper-right corner of the page:
<img src="https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnnr_widgets_actions_apply-cancel.jpeg?raw=true" alt="" width="900" />

If you’ve changed your mind, click on the “**Cancel**” button. The last saved version of the widgets set and placement will be displayed.

Widgets are grouped into the dashboards.

There are two types of widgets: default and custom. You can choose between 14 types of pre-configured widgets or 11 types of configurable templates.

#### Creating a new widget
To create a new widget perform the following steps:

1. Navigate to the "**Dashboards**" (a.k.a “General”) page and click the "**+ NEW WIDGET**" button in the top-right corner of the page:

<img src="https://github.com/APGorobets/mkdocks1/blob/master/images/04-01.jpg?raw=true" alt="" width="900" />

2. The “**Choose template**” pop-up would appear, where you can choose one or several of the following configurable templates:
 
<img src="https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnnr-widgets-2.png?raw=true" alt="" width="500" />
 
<img src="https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnnr-widgets-3.png?raw=true" alt="" width="500" />

* Application issues (blockers) count (A number of unique application bugs discovered and submitted by automation)
* Application issues (blocker) details (Detailed information about known issues and blockers)
* Milestone details (Consolidated test status trend with the ability to specify 10+ extra filters and grouping by hours, days, month, etc.)
* Monthly test implementation progress (A number of new automated cases per month)
* Pass rate (Consolidated test status information with the ability to specify 10+ extra filters including daily, weekly, monthly, etc. period.)
* Pass rate (%) - Pass rate percent with an extra grouping by project, owner, etc.)
* Pass rate trend (Consolidated test status trend with the ability to specify 10+ extra filters and grouping by hours, days, months, etc.)
* Test cases by stability (Shows all test cases with low stability percent rate per appropriate period (default - less than 10%)
* Test execution ROI (Man-hours) (Monthly team/personal automation ROI by test executions)
* Tests failures by reason (Summarized information about test failures grouped by reason)
* Tests summary (Detailed information about passed, failed, skipped, etc. tests)

or choose from various existing widgets:

<img src="https://github.com/APGorobets/mkdocks1/blob/master/images/choose_widget_1.jpg?raw=true" alt="" width="500" />

<img src="https://github.com/APGorobets/mkdocks1/blob/master/images/choose_widgets_2.jpg?raw=true" alt="" width="500" />

* 30 days pass rate by platform (%) (Pass rate by platform (%))
* 30 days test details (Detailed information about passed, failed, skipped, etc. tests for the last 30 days)
* 30 days personal trend (Consolidated personal test status trend for the last 30 days)
* 30 days tests trend (Consolidated test status trend for the last 30 days)
* 30 days total (Consolidated test status information for the last 30 days)
* Last 24 hours personal (Consolidated personal information for the last 24 hours)
* Last 24 hours personal failures (Summarized personal information about tests failures grouped by reason)
* Last 30 days personal (Consolidated personal information for the last 30 days)
* Last 7 days personal (Consolidated personal information for the last 7 days)
* Monthly test implementation progress (Number of new automated cases per month)
* Personal total rate (Totally consolidated personal test status information)
* Personal total tests (man-hours) (Monthly personal automation ROI by tests execution. 160+hours for UI tests indicates that your execution ROI…….)
* Total personal tests trend (Totally consolidated personal tests status trend)

3. You can add an unlimited number of widgets to the dashboard.
To add the widget, click on it, and when you have selected all the widgets you need, click the "**Add**" button.

4. If you’ve changed your mind and don’t want to add one or several of the already selected widgets, click on the additional options menu icon and choose "**Delete**", all the selected elements will be deleted.
When you’ve added a widget to the dashboard, it would be marked with the word "**Added**" and the tick and a checkmark:

<img src="https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnnr-widgets-17.png?raw=true" alt="" width="500" />

## Search panel

<img src="https://github.com/APGorobets/mkdocks1/blob/master/images/zbrnnr_dashboards_search.png?raw=true" alt="" width="150" />

Search criteria:

* The name/a part of the name of the dashboard

The list of default Dashboards (with widgets):

* Personal
  + Last 24 hours
  + 7 days/ 30 days personal
  + 30 days tests personal trend
  + Last 24 hours personal + failures
  
<img src="../img/dashboards/zbrnnr_dashboards_personal_filled.png"> alt="" width="900" />

* Failures Analysis
  + Last 24 hours failure count
  + Last 24 hours personal details
  
* User Performance
  + Personal Total Rate
  + Personal Total Tests (Man-hours)
  + Monthly Test Implementation Progress (Number Of Test Methods Implemented By Person)
  + Total Personal Tests Trend
  
* Stability
  + Test Case Stability (%)
  + Testcase info
  + Test Case Stability Trend (%)
  + Test Case Duration Details (Sec)
