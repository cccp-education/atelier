<#include "header.ftl">

<#include "menu.ftl">

<#if (content.title)??>
    <div class="p-4 mb-8">
        <h1><#escape x as x?xml>${content.title}</#escape></h1>
    </div>
<#else></#if>

<#--	<p><em>${content.date?string("dd MMMM yyyy")}</em></p>-->

<p>${content.body}</p>

<hr/>

<#include "footer.ftl">
