import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OAEIMappingParserTests {
    private static String mappingXML = "<?xml version='1.0' encoding='utf-8'?>\n" +
            "\t\t\t\t<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment' \n" +
            "\t\t\t\t\t xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' \n" +
            "\t\t\t\t\t xmlns:xsd='http://www.w3.org/2001/XMLSchema#'>\n" +
            "\t\t\t\t<Alignment>\n" +
            "\t\t\t\t<xml>yes</xml>\n" +
            "\t\t\t\t<level>0</level>\n" +
            "\t\t\t\t<type>??</type>\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t<onto1>\n" +
            "\t\t\t\t<Ontology rdf:about=\"http://cmt\">\n" +
            "\t\t\t\t  <location>http://nb.vse.cz/~svabo/oaei2010/cmt.owl</location>\n" +
            "\t\t\t\t</Ontology>\n" +
            "\t\t\t\t</onto1>\n" +
            "\t\t\t\t<onto2>\n" +
            "\t\t\t\t<Ontology rdf:about=\"http://conference\">\n" +
            "\t\t\t\t  <location>http://nb.vse.cz/~svabo/oaei2010/Conference.owl</location>\n" +
            "\t\t\t\t</Ontology>\n" +
            "\t\t\t\t</onto2>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='1'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#Conference'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#Conference_volume'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='2'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#Preference'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#Review_preference'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='3'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#Author'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#Regular_author'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='4'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#Person'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#Person'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='5'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#email'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#has_an_email'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='6'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#Co-author'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#Contribution_co-author'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='7'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#PaperAbstract'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#Abstract'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='8'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#Document'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#Conference_document'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='9'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#Review'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#Review'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='10'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#Conference'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#Conference'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='11'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#ProgramCommittee'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#Program_committee'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='12'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#Chairman'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#Chair'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='13'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#SubjectArea'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#Topic'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='16'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#assignedByReviewer'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#invited_by'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t<map>\n" +
            "\t\t\t\t\t\t\t\t<Cell cid='17'>\n" +
            "\t\t\t\t\t\t\t\t\t<entity1 rdf:resource='http://cmt#assignExternalReviewer'/>\n" +
            "\t\t\t\t\t\t\t\t\t<entity2 rdf:resource='http://conference#invites_co-reviewers'/>\n" +
            "\t\t\t\t\t\t\t\t\t<measure rdf:datatype='xsd:float'>1.0</measure>\n" +
            "\t\t\t\t\t\t\t\t\t<relation>=</relation>\n" +
            "\t\t\t\t\t\t\t\t</Cell>\n" +
            "\t\t\t\t\t\t\t</map>\n" +
            "\t\t\t\t\t\t\t</Alignment>\n" +
            "\t</rdf:RDF>";

    @Test
    public void GivenXMLMappingFromStringShouldReturnProperMappingObject(){
        OAEIMappingParser parser = new OAEIMappingParser();
        try{
            OAEIMapping mapping = parser.Parse(mappingXML);
            assertEquals(14, mapping.getMapping().size());
            assertEquals(mapping.getMapping().get("http://cmt#ProgramCommittee").get(0).getCertainty(), 1.0, 0.001);
            assertTrue(mapping.getMapping().get("http://cmt#Conference").size() == 2);
        }catch (Exception ex){
            System.out.println(ex);
        }
    }

    @Test
    public void GivenXMLMappingFromFileShouldReturnProperMappingObject(){
        OAEIMappingParser parser = new OAEIMappingParser();
        File file = new File("src/test/resources/cmt-conference.rdf");

        try{
            OAEIMapping mapping = parser.Parse(file);
            assertEquals(14, mapping.getMapping().size());
            assertEquals(mapping.getMapping().get("http://cmt#ProgramCommittee").get(0).getCertainty(), 1.0, 0.001);
            assertEquals(mapping.getMapping().get("http://cmt#ProgramCommittee").get(0).getDistance(), 0.0, 0.001);
            assertTrue(mapping.getMapping().get("http://cmt#Conference").size() == 2);
        }catch (Exception ex){
            System.out.println(ex);
        }
    }

}
