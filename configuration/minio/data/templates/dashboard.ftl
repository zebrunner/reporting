<div>
    <h3>${subject}</h3>
    <table>
        <tr>
            <td>
                <p>
                    <pre>${text}</pre>
                </p>
            </td>
        </tr>
        <#if attachments??>
            <#list attachments as attachment>
                <tr>
                    <td>
                        <img src='cid:${attachment.name?replace("[^A-Za-z0-9]", "_", "r")}' alt="attachment" style="width: 100%">
                    </td>
                </tr>
            </#list>
        </#if>
    </table>
</div>
