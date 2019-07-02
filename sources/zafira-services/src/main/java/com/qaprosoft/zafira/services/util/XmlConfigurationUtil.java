package com.qaprosoft.zafira.services.util;

import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.models.db.config.Configuration;
import com.qaprosoft.zafira.services.exceptions.MalformedConfigXMLException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class XmlConfigurationUtil {

    /**
     * Parses configuration
     * @param configXML
     * and
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
            throw new MalformedConfigXMLException(e.getMessage());
        }
        return configuration;
    }

    public static Map<String, String> parseConfigToMap(Configuration configuration){
        return configuration.getArg()
                            .stream()
                            .collect(Collectors.toMap(Argument::getKey, Argument::getValue));
    }

    public static String getConfigValueByName(String name, String configurationXML) {
        Configuration configuration = readArguments(configurationXML);
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
