package OAEI;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("Duplicates")
public class OAEIMappingParser {
    public OAEIMapping Parse(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        xmlContent = xmlContent.replaceAll("[\t\n]", "");
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlContent));
        Document document = builder.parse(is);
        document.normalizeDocument();
        OAEIMapping mapping = ParseDocument(document);
        return mapping;
    }

    public OAEIMapping Parse(File xmlFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        String fileContent = FileUtils.readFileToString(xmlFile, StandardCharsets.UTF_8);
        fileContent = fileContent.replaceAll("[\t\n\r]", "");
        InputSource is = new InputSource(new StringReader(fileContent));
        Document document = builder.parse(is);
        document.normalizeDocument();
        OAEIMapping mapping = ParseDocument(document);
        return mapping;
    }

    @SuppressWarnings("Duplicates")
    private OAEIMapping ParseDocument(Document doc) throws XPathExpressionException {
        String firstOntologyName = doc.getElementsByTagName("Ontology")
                                        .item(0)
                                        .getAttributes()
                                        .getNamedItem("rdf:about")
                                        .getNodeValue();

        String secondOntologyName = doc.getElementsByTagName("Ontology")
                .item(1)
                .getAttributes()
                .getNamedItem("rdf:about")
                .getNodeValue();


        NodeList mappings = doc.getElementsByTagName("map");
        HashMap<String, ArrayList<EntityEquivalent>> hashMapping = new HashMap<String, ArrayList<EntityEquivalent>>();
        for(int i = 0; i < mappings.getLength(); i++){
            Node node = mappings.item(i).getFirstChild();
            String firstEntity = node.getChildNodes().item(0).getAttributes().getNamedItem("rdf:resource").getNodeValue();
            String secondEntity = node.getChildNodes().item(1).getAttributes().getNamedItem("rdf:resource").getNodeValue();
            double measure = Float.parseFloat(node.getChildNodes().item(2).getTextContent());

            if(hashMapping.containsKey(firstEntity)){
                hashMapping.get(firstEntity).add(new EntityEquivalent(secondEntity, measure));
            }
            else{
                ArrayList<EntityEquivalent> equivalentList = new ArrayList<EntityEquivalent>();
                equivalentList.add(new EntityEquivalent(secondEntity, measure));
                hashMapping.put(firstEntity, equivalentList);
            }

            if(hashMapping.containsKey(secondEntity)){
                hashMapping.get(secondEntity).add(new EntityEquivalent(firstEntity, measure));
            }
            else{
                ArrayList<EntityEquivalent> equivalentList = new ArrayList<EntityEquivalent>();
                equivalentList.add(new EntityEquivalent(firstEntity, measure));
                hashMapping.put(secondEntity, equivalentList);
            }
        }

        return new OAEIMapping(firstOntologyName.toString(), secondOntologyName, hashMapping);
    }
}
