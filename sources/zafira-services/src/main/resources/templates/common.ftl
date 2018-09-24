<#--
    Required:
        1. header
        2. messages
        3. body
        4. subMessages
-->

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
<head>
    <title></title>
    <!--[if !mso]><!-- -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!--<![endif]-->
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style type="text/css">  #outlook a { padding: 0; }  .ReadMsgBody { width: 100%; }  .ExternalClass { width: 100%; }  .ExternalClass * { line-height:100%; }  body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }  table, td { border-collapse:collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }  img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }  p { display: block; margin: 13px 0; }</style>
    <!--[if !mso]><!-->
    <style type="text/css">  @media only screen and (max-width:480px) {    @-ms-viewport { width:320px; }    @viewport { width:320px; }  }</style>
    <!--<![endif]--><!--[if mso]>
    <xml>
        <o:OfficeDocumentSettings>
            <o:AllowPNG/>
            <o:PixelsPerInch>96</o:PixelsPerInch>
        </o:OfficeDocumentSettings>
    </xml>
    <![endif]--><!--[if lte mso 11]>
    <style type="text/css">  .outlook-group-fix {    width:100% !important;  }</style>
    <![endif]--><!--[if !mso]><!-->
    <link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700" rel="stylesheet" type="text/css">
    <style type="text/css">        @import url(https://fonts.googleapis.com/css?family=Roboto);  @import url(https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700);    </style>
    <!--<![endif]-->
    <style type="text/css">  @media only screen and (min-width:480px) {    .mj-column-per-100 { width:100%!important; }  }</style>
</head>
<body style="background: #FFFFFF;">
<div class="mj-container" style="background-color:#FFFFFF;">
    <!--[if mso | IE]>
    <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="600" align="center" style="width:600px;">
        <tr>
            <td style="line-height:0px;font-size:0px;mso-line-height-rule:exactly;">
    <![endif]-->
    <div style="margin:0px auto;max-width:600px;">
        <table role="presentation" cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;" align="center" border="0">
            <tbody>
            <tr>
                <td style="text-align:center;vertical-align:top;direction:ltr;font-size:0px;padding:9px 0px 9px 0px;">
                    <!--[if mso | IE]>
                    <table role="presentation" border="0" cellpadding="0" cellspacing="0">
                        <tr>
                            <td style="vertical-align:top;width:600px;">
                    <![endif]-->
                    <div class="mj-column-per-100 outlook-group-fix" style="vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:left;width:100%;">
                        <table role="presentation" cellpadding="0" cellspacing="0" width="100%" border="0">
                            <tbody>
                            <tr>
                                <td style="word-wrap:break-word;font-size:0px;padding:20px 20px 20px 20px;" align="left">
                                    <table role="presentation" cellpadding="0" cellspacing="0" style="border-collapse:collapse;border-spacing:0px;" align="left" border="0">
                                        <tbody>
                                        <tr>
                                            <td style="width:36px;"><img alt="" title="" height="auto" src="${zafiraLogoURL}" style="border:none;border-radius:0px;display:block;font-size:13px;outline:none;text-decoration:none;width:100%;height:auto;" width="36"></td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                            <tr>
                                <td style="word-wrap:break-word;font-size:0px;padding:0px 20px 0px 20px;" align="left">
                                    <div style="cursor:auto;color:#000000;font-family:Roboto, Tahoma, sans-serif;font-size:11px;line-height:22px;text-align:left;">
                                        <h1 style="font-family: Roboto, Tahoma, sans-serif; line-height: 100%;"><strong><#if header ??>${header}<#else>${subject}</#if></strong></h1>
                                    </div>
                                </td>
                            </tr>
                            <#if body ??>
                            <tr>
                                <td style="word-wrap:break-word;font-size:0px;padding:0px 20px 0px 20px;" align="left">
                                    <div style="cursor:auto;color:#000000;font-family:Roboto, Tahoma, sans-serif;font-size:11px;line-height:22px;text-align:left;">
                                        ${body}
                                    </div>
                                </td>
                            </tr>
                            </#if>
                            <#if control ??>
                            <tr>
                                <td style="word-wrap:break-word;font-size:0px;padding:10px 20px 10px 20px;padding-top:10px;padding-left:20px;" align="left">
                                    <table role="presentation" cellpadding="0" cellspacing="0" style="border-collapse:separate;" align="left" border="0">
                                        ${control}
                                    </table>
                                </td>
                            </tr>
                            </#if>
                            <#if footer ??>
                            <tr>
                                <td style="word-wrap:break-word;font-size:0px;padding:0px 20px 0px 20px;" align="left">
                                    <div style="cursor:auto;color:#000000;font-family:Roboto, Tahoma, sans-serif;font-size:11px;line-height:22px;text-align:left;">
                                        ${footer}
                                    </div>
                                </td>
                            </tr>
                            </#if>
                            <tr>
                                <td style="word-wrap:break-word;font-size:0px;padding:20px 0 10px 10px;">
                                    <p style="font-size:1px;margin:0px auto;border-top:1px solid #A3A3A3;width:100%;"></p>
                                    <!--[if mso | IE]>
                                    <table role="presentation" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size:1px;margin:0px auto;border-top:1px solid #A3A3A3;width:100%;" width="600">
                                        <tr>
                                            <td style="height:0;line-height:0;"> </td>
                                        </tr>
                                    </table>
                                    <![endif]-->
                                </td>
                            </tr>
                            <#if companyLogoURL ?? && workspaceURL ??>
                            <tr>
                                <td style="word-wrap:break-word;font-size:0px;padding:0 20px 10px 25px;" align="left">
                                    <table role="presentation" cellpadding="0" cellspacing="0" style="border-collapse:separate;" align="left" border="0">
                                        <tbody>
                                        <tr>
                                            <td style="border:none;border-radius:none;cursor:auto;width: 30px;" align="center" valign="middle">
                                                <img alt="" title="" height="auto" src="${companyLogoURL}" style="border:none;border-radius:50%;display:block;font-size:13px;outline:none;text-decoration:none;width:100%;height:auto;" width="30">
                                            </td>
                                            <td style="border:none;border-radius:none;cursor:auto;" align="center" valign="middle">
                                                <div style="cursor:auto;color:#000000;font-family:Ubuntu, Helvetica, Arial, sans-serif;font-size:11px;line-height:22px;text-align:left;">
                                                    <p style="padding: 0 0 0 20px;">Workspace URL&#xA0;<a href="${workspaceURL}" target="_blank">${workspaceURL}</a></p>
                                                </div>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                            <#elseif workspaceURL ??>
                            <tr>
                                <td style="word-wrap:break-word;font-size:0px;padding:0 20px 10px 25px;" align="left">
                                    <table role="presentation" cellpadding="0" cellspacing="0" style="border-collapse:separate;" align="left" border="0">
                                        <tbody>
                                        <tr>
                                            <td style="border:none;border-radius:none;cursor:auto;width: 30px;" align="center" valign="middle">
                                                <p style="padding: 0 0 0 20px;">Workspace URL&#xA0;<a href="${workspaceURL}" target="_blank">${workspaceURL}</a></p>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                            </#if>
                            </tbody>
                        </table>
                    </div>
                    <!--[if mso | IE]>
                    </td>
                    </tr>
                    </table>
                    <![endif]-->
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <!--[if mso | IE]>
    </td>
    </tr>
    </table>
    <![endif]-->      <!--[if mso | IE]>
    <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="600" align="center" style="width:600px;">
        <tr>
            <td style="line-height:0px;font-size:0px;mso-line-height-rule:exactly;">
                <!--[if mso | IE]>
                </td>
                </tr>
                </table>
<![endif]-->      <!--[if mso | IE]>
    <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="600" align="center" style="width:600px;">
        <tr>
            <td style="line-height:0px;font-size:0px;mso-line-height-rule:exactly;">
    <![endif]-->
    <!--[if mso | IE]>
    </td>
    </tr>
    </table>
    <![endif]-->
</div>
</body>
</html>