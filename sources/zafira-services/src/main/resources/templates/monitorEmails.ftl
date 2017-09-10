<div>
   <p>${monitor.name}</p>
   <#if monitor.url??>
   <tr>
      <td>URL:${monitor.url}</td>
   </tr>
   </br>
   </#if>
   <#if actualStatus??>
   <tr>
      <td>Actual status:${actualStatus}</td>
   </tr>
   </#if>
</div>
