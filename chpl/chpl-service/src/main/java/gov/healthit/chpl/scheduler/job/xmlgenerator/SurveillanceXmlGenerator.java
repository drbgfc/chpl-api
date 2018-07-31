package gov.healthit.chpl.scheduler.job.xmlgenerator;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import gov.healthit.chpl.domain.Surveillance;

public class SurveillanceXmlGenerator extends XmlGenerator {
    public static void add(List<Surveillance> surveillances, String rootNodeName, XMLStreamWriter sw) throws XMLStreamException {
        if (surveillances != null) {
            sw.writeStartElement(rootNodeName);
            for (Surveillance s : surveillances) {
                add(s, "surveillance", sw);
            }
            sw.writeEndElement();
        }
    }
    
    public static void add(Surveillance surveillance, String rootNodeName, XMLStreamWriter sw) throws XMLStreamException {
        if (surveillance != null) {
            sw.writeStartElement(rootNodeName);;
            createSimpleElement(surveillance.getId(), "id", sw);
            createSimpleElement(surveillance.getFriendlyId(), "friendlyId", sw);
            createSimpleElement(surveillance.getStartDate(), "startDate", sw);
            createSimpleElement(surveillance.getEndDate(), "endDate", sw);
            createSimpleElement(surveillance.getStartDate(), "startDate", sw);
            SurveillanceTypeXmlGenerator.addSurveillanceType(surveillance.getType(), "type", sw);
            createSimpleElement(surveillance.getRandomizedSitesUsed(), "randomizedSitesUsed", sw);
            SurveillanceRequirementXmlGenerator.add(surveillance.getRequirements(), "surveilledRequirements", sw);
            sw.writeEndElement();
        }
    }
}
