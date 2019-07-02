package com.qaprosoft.zafira.services.util;

import com.qaprosoft.zafira.models.db.config.Configuration;
import com.qaprosoft.zafira.services.exceptions.MalformedConfigXMLException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;

public class ConfigurationUtil {

    /**
     * Parses configuration
     * @param configXML
     * and
     * @return Configuration object
     */
    public static Configuration readConfigArgs(String configXML) {
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

}
