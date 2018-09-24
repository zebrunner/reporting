<#assign header = "Forgot password?">
<#assign body = "<p style=\"margin-top: 2px; margin-bottom: 2px; line-height: 2;\">You told us you forgot your password. If you</p>
                <p style=\"margin-top: 2px; margin-bottom: 2px; line-height: 2;\">really did, click here to choose a new one: </p>">
<#assign control = "<tbody>
                    <tr>
                      <td style=\"border:none;border-radius:none;color:#fff;cursor:auto;padding:10px 25px;\" align=\"center\" valign=\"middle\" bgcolor=\"#26a69a\"><a href=\"${workspaceURL}/#!/password/reset?token=${token}\" style=\"text-decoration:none;background:#26a69a;color:#fff;font-family:Roboto, Tahoma, sans-serif;font-size:13px;font-weight:normal;line-height:120%;text-transform:none;margin:0px;\" target=\"_blank\">Choose a new password</a></td>
                    </tr>
                 </tbody>">
<#assign footer = "<p style=\"line-height: 1;\">If you didn't mean to reset your password, then you can just ignore this email;</p>
                   <p style=\"line-height: 1;\">your password will not change.</p>">

<#include 'common.ftl'>