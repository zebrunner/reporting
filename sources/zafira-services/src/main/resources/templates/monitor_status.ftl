<div>
    <p>Monitor name: <a href="${zafiraFullURL}/#!/monitors" style="font-variant: small-caps; color: #000;">${monitor.name}</a></p>
<#if monitor.url??>
    <tr>
        <td>Monitor with URL: ${monitor.url}
            has incorrect status.
        </td>
    </tr>
    </br>
</#if>
<#if actualStatus??>
    <tr>
        <td>Actual status: <b style="font-variant: small-caps">${actualStatus}</b></td>
    </tr>
</#if>
</div>
