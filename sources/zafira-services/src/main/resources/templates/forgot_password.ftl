<#assign header = "Forgot password?">
<#assign body = "<p style=\"margin-top: 2px; margin-bottom: 2px; line-height: 2;\">Click here to reset the old and choose a new one.</p>">
<#assign control = "<tbody>
                    <tr>
                      <td style=\"border:none;border-radius:none;color:#fff;cursor:auto;padding:10px 25px;\" align=\"center\" valign=\"middle\" bgcolor=\"#26a69a\"><a href=\"${workspaceURL}/#!/password/reset?token=${token}\" style=\"text-decoration:none;background:#26a69a;color:#fff;font-family:Roboto, Tahoma, sans-serif;font-size:13px;font-weight:normal;line-height:120%;text-transform:none;margin:0px;\" target=\"_blank\">Change password</a></td>
                    </tr>
                 </tbody>">
<#assign footer = "<p style=\"line-height: 1;\">In case you didn’t want to reset the old password or this message was sent to you by mistake, </p>
                   <p style=\"line-height: 1;\">ignore this e-mail, your password will stay the same.</p>">

<#include 'common.ftl'>