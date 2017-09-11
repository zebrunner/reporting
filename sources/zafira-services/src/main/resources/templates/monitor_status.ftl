<div>
    <p>Monitor name: <b style="font-variant: small-caps">${monitor.name}</b></p>
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
