package com.qaprosoft.zafira.services.util;

import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.models.db.config.Configuration;
import com.qaprosoft.zafira.services.exceptions.ProcessingException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static com.qaprosoft.zafira.services.exceptions.ProcessingException.ProcessingErrorDetail.UNPROCESSABLE_XML_ENTITY;

public class XmlConfigurationUtil {

    /**
     * Parses configuration xml
     * into Configuration object
     * @param configXML
     * @return Configuration object
     */
    public static Configuration readArguments(String configXML) {
        Configuration configuration = new Configuration();
        try {
            if (!StringUtils.isEmpty(configXML)) {
                ByteArrayInputStream xmlBA = new ByteArrayInputStream(configXML.getBytes());
                configuration = (Configuration) JAXBContext.newInstance(Configuration.class).createUnmarshaller().unmarshal(xmlBA);
                IOUtils.closeQuietly(xmlBA);
            }
        } catch (JAXBException e) {
            throw new ProcessingException(UNPROCESSABLE_XML_ENTITY, "Error parsing XML document", e);
        }
        return configuration;
    }

    /**
     * Parses Configuration object
     * into Map<String, String> map
     * @param configuration
     * @return map
     */
    public static Map<String, String> parseConfigToMap(Configuration configuration){
        return configuration.getArg()
                            .stream()
                            .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);
    }

    /**
     * Gets value of argument in Configuration
     * by name
     * @param name
     * @param configXML
     * @return value
     */
    public static String getConfigValueByName(String name, String configXML) {
        Configuration configuration = readArguments(configXML);
        return configuration.getArg()
                            .stream()
                            .filter(arg -> arg.getKey().equalsIgnoreCase(name))
                            .findFirst()
                            .orElse(new Argument()).getValue();
    }

    public static boolean isConfigValueIsEmpty(String value) {
        return org.apache.commons.lang3.StringUtils.isBlank(value) || value.equalsIgnoreCase("NULL") || value.equals("*");
    }
}
