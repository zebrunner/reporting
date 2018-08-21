package com.qaprosoft.zafira.services.services.jmx;

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.jmx.models.AbstractType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JMXTenancyStorage
{

    private static Map<Setting.Tool, Map<String, ? extends AbstractType>> tenancyEntity = new ConcurrentHashMap<>();

    @SuppressWarnings("all")
    public synchronized static <T extends AbstractType> void putType(Setting.Tool tool, T t)
    {
        if(tenancyEntity.get(tool) == null)
        {
            Map<String, T> typeMap = new ConcurrentHashMap<>();
            typeMap.put(TenancyContext.getTenantName(), t);
            tenancyEntity.put(tool, typeMap);
        } else
        {
            ((Map<String, T>) tenancyEntity.get(tool)).put(TenancyContext.getTenantName(), t);
        }
    }

    @SuppressWarnings("all")
    public static <T> T getType(Setting.Tool tool)
    {
        Map<String, ? extends AbstractType> clientMap = tenancyEntity.get(tool);
        return clientMap == null ? null : (T) tenancyEntity.get(tool).get(TenancyContext.getTenantName());
    }
}
