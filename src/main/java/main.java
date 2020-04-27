import OAEI.MappingProvider;
import OAEI.OAEIMapping;
import OAEI.OAEIMappingParser;
import OAEI.OntologyProvider;
import benchmarks.MergingBenchmark;
import benchmarks.MergingSample;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semanticweb.owlapi.apibinding.OWLManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class main {

    public static final String KOALA = "<?xml version=\"1.0\"?>\n"
            + "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns=\"http://protege.stanford.edu/plugins/owl/owl-library/koala.owl#\" xml:base=\"http://protege.stanford.edu/plugins/owl/owl-library/koala.owl\">\n"
            + "  <owl:Ontology rdf:about=\"\"/>\n"
            + "  <owl:Class rdf:ID=\"Female\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:FunctionalProperty rdf:about=\"#hasGender\"/></owl:onProperty><owl:hasValue><Gender rdf:ID=\"female\"/></owl:hasValue></owl:Restriction></owl:equivalentClass></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Marsupials\"><owl:disjointWith><owl:Class rdf:about=\"#Person\"/></owl:disjointWith><rdfs:subClassOf><owl:Class rdf:about=\"#Animal\"/></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Student\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#Person\"/><owl:Restriction><owl:onProperty><owl:FunctionalProperty rdf:about=\"#isHardWorking\"/></owl:onProperty><owl:hasValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasValue></owl:Restriction><owl:Restriction><owl:someValuesFrom><owl:Class rdf:about=\"#University\"/></owl:someValuesFrom><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasHabitat\"/></owl:onProperty></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"KoalaWithPhD\"><owl:versionInfo>1.2</owl:versionInfo><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:hasValue><Degree rdf:ID=\"PhD\"/></owl:hasValue><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasDegree\"/></owl:onProperty></owl:Restriction><owl:Class rdf:about=\"#Koala\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"University\"><rdfs:subClassOf><owl:Class rdf:ID=\"Habitat\"/></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Koala\"><rdfs:subClassOf><owl:Restriction><owl:hasValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</owl:hasValue><owl:onProperty><owl:FunctionalProperty rdf:about=\"#isHardWorking\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:someValuesFrom><owl:Class rdf:about=\"#DryEucalyptForest\"/></owl:someValuesFrom><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasHabitat\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf rdf:resource=\"#Marsupials\"/></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Animal\"><rdfs:seeAlso>Male</rdfs:seeAlso><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasHabitat\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:cardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">1</owl:cardinality><owl:onProperty><owl:FunctionalProperty rdf:about=\"#hasGender\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf><owl:versionInfo>1.1</owl:versionInfo></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Forest\"><rdfs:subClassOf rdf:resource=\"#Habitat\"/></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Rainforest\"><rdfs:subClassOf rdf:resource=\"#Forest\"/></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"GraduateStudent\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasDegree\"/></owl:onProperty><owl:someValuesFrom><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><Degree rdf:ID=\"BA\"/><Degree rdf:ID=\"BS\"/></owl:oneOf></owl:Class></owl:someValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf rdf:resource=\"#Student\"/></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Parent\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#Animal\"/><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasChildren\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass><rdfs:subClassOf rdf:resource=\"#Animal\"/></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"DryEucalyptForest\"><rdfs:subClassOf rdf:resource=\"#Forest\"/></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Quokka\"><rdfs:subClassOf><owl:Restriction><owl:hasValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasValue><owl:onProperty><owl:FunctionalProperty rdf:about=\"#isHardWorking\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf rdf:resource=\"#Marsupials\"/></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"TasmanianDevil\"><rdfs:subClassOf rdf:resource=\"#Marsupials\"/></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"MaleStudentWith3Daughters\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#Student\"/><owl:Restriction><owl:onProperty><owl:FunctionalProperty rdf:about=\"#hasGender\"/></owl:onProperty><owl:hasValue><Gender rdf:ID=\"male\"/></owl:hasValue></owl:Restriction><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasChildren\"/></owl:onProperty><owl:cardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">3</owl:cardinality></owl:Restriction><owl:Restriction><owl:allValuesFrom rdf:resource=\"#Female\"/><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasChildren\"/></owl:onProperty></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Degree\"/>\n  <owl:Class rdf:ID=\"Gender\"/>\n"
            + "  <owl:Class rdf:ID=\"Male\"><owl:equivalentClass><owl:Restriction><owl:hasValue rdf:resource=\"#male\"/><owl:onProperty><owl:FunctionalProperty rdf:about=\"#hasGender\"/></owl:onProperty></owl:Restriction></owl:equivalentClass></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Person\"><rdfs:subClassOf rdf:resource=\"#Animal\"/><owl:disjointWith rdf:resource=\"#Marsupials\"/></owl:Class>\n"
            + "  <owl:ObjectProperty rdf:ID=\"hasHabitat\"><rdfs:range rdf:resource=\"#Habitat\"/><rdfs:domain rdf:resource=\"#Animal\"/></owl:ObjectProperty>\n"
            + "  <owl:ObjectProperty rdf:ID=\"hasDegree\"><rdfs:domain rdf:resource=\"#Person\"/><rdfs:range rdf:resource=\"#Degree\"/></owl:ObjectProperty>\n"
            + "  <owl:ObjectProperty rdf:ID=\"hasChildren\"><rdfs:range rdf:resource=\"#Animal\"/><rdfs:domain rdf:resource=\"#Animal\"/></owl:ObjectProperty>\n"
            + "  <owl:FunctionalProperty rdf:ID=\"hasGender\"><rdfs:range rdf:resource=\"#Gender\"/><rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#ObjectProperty\"/><rdfs:domain rdf:resource=\"#Animal\"/></owl:FunctionalProperty>\n"
            + "  <owl:FunctionalProperty rdf:ID=\"isHardWorking\"><rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#boolean\"/><rdfs:domain rdf:resource=\"#Person\"/><rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#DatatypeProperty\"/></owl:FunctionalProperty>\n"
            + "  <Degree rdf:ID=\"MA\"/>\n</rdf:RDF>";

    public static OWLOntology load(OWLOntologyManager manager) throws OWLOntologyCreationException {
        // in this test, the ontology is loaded from a string
        return manager.loadOntologyFromOntologyDocument(new StringDocumentSource(KOALA));
    }

    private void mergeOntologies(){
        IRI mergedOntologyIRI = IRI.create("http://www.semanticweb.com/", "cmt_confOf");
        OWLOntology mergedOntology = null;
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        Set<OWLImportsDeclaration> imports = new HashSet<OWLImportsDeclaration>();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        List<OWLOntology> ontologies = new ArrayList<OWLOntology>();
        try{

            OWLOntology cmtOnto = man.loadOntologyFromOntologyDocument(new File("D:\\PWR\\MGR\\Datasets\\Conference2019\\cmt.owl"));
            OWLOntology confOf = man.loadOntologyFromOntologyDocument(new File("D:\\PWR\\MGR\\Datasets\\Conference2019\\confOf.owl"));
            ontologies.add(cmtOnto);
            ontologies.add(confOf);
            for(OWLOntology ontology : ontologies){
                axioms.addAll(ontology.getAxioms());
                imports.addAll(ontology.getImportsDeclarations());
                man.removeOntology(ontology);
            }
            mergedOntology = man.createOntology(mergedOntologyIRI);
            man.addAxioms(mergedOntology, axioms);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        //Adding the import declarations
        for(OWLImportsDeclaration decl : imports){
            man.applyChange(new AddImport(mergedOntology, decl));
        }
        //rename individuals names to use the merged ontology's IRI
//        renameIRIs(mergedOntologyIRI);
        OWLEntityRenamer renamer = new OWLEntityRenamer(man, man.getOntologies());

        for(OWLOntology ontology : ontologies){
            for ( OWLEntity individual : ontology.getIndividualsInSignature()){
                //replace the individual's old IRI with the new one E.g: http://ontologyOld#name becomes newIRI#name
                IRI individualName = IRI.create(individual.getIRI().toString().replaceFirst("[^*]+(?=#|;)", mergedOntologyIRI.toString()));
                man.applyChanges(renamer.changeIRI(individual.getIRI(), individualName));
            }
        }
    }

    private void renameIRIs (IRI newIRI){
//        OWLEntityRenamer renamer = new OWLEntityRenamer(man, man.getOntologies());
//
//        for(Ontology ontology : ontologies){
//            for ( OWLEntity individual : ontology.getOntology().getIndividualsInSignature()){
//                //replace the individual's old IRI with the new one E.g: http://ontologyOld#name becomes newIRI#name
//                IRI individualName = IRI.create(individual.getIRI().toString().replaceFirst("[^*]+(?=#|;)", newIRI.toString()));
//                man.applyChanges(renamer.changeIRI(individual.getIRI(), individualName));
//            }
//        }
    }

    public static void main(String[] args) throws Exception {
//        MergingSample sample = new MergingSample("cmt", "conference", "cmt-conference");
//
//        Options opt = new OptionsBuilder()
//                .include(MergingBenchmark.class.getSimpleName())
//                .param("mergingSample", sample)
//                .build();
//
//
//        new Runner(opt).run();

        org.openjdk.jmh.Main.main(args);

//        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
//        OAEIMappingParser parser = new OAEIMappingParser();
//
//        try{
//            OAEIMapping mapping = parser.Parse(new File("src/test/resources/cmt-conference.rdf"));
//            OWLOntology cmtOnto = man.loadOntologyFromOntologyDocument(new File("src/test/resources/cmt.owl"));
//            OWLOntology confOf = man.loadOntologyFromOntologyDocument(new File("src/test/resources/Conference.owl"));
//            ExperimentMeasures measuresEngine = new ExperimentMeasures();
//            TimeMeasures mergingTime = measuresEngine.GetOntologyMergingTime(cmtOnto, confOf, mapping);
//            System.out.println(mergingTime);
//            System.out.println(mergingTime.get_measures());
//        }
//        catch(Exception e){
//            System.out.println(e);
//        }

        //blindMerge();
    }

    private static void blindMerge(){
        try{
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology cmtOnto = man.loadOntologyFromOntologyDocument(new File("D:\\PWR\\MGR\\Datasets\\Conference2019\\cmt.owl"));
            man.loadOntologyFromOntologyDocument(new File("D:\\PWR\\MGR\\Datasets\\Conference2019\\confOf.owl"));

            Object[] objects = cmtOnto.axioms().toArray();

            Set<OWLEntity> signatures = cmtOnto.getSignature();
            //load(man);
//            OWLOntology o = man.createOntology(IRI.create("urn:test", ""));
//            man.addAxiom(o, man.getOWLDataFactory().getOWLDeclarationAxiom(man.getOWLDataFactory()
//                    .getOWLClass("urn:test#",
//                    "class")));
            // Create our ontology merger
            OWLOntologyMerger merger = new OWLOntologyMerger(man);
            // We merge all of the loaded ontologies. Since an OWLOntologyManager is
            // an OWLOntologySetProvider we just pass this in. We also need to
            // specify the IRI of the new ontology that will be created.
            IRI mergedOntologyIRI = IRI.create("http://www.semanticweb.com/", "cmt_confOf");
            OWLOntology merged = merger.createMergedOntology(man, mergedOntologyIRI);
            //Print out the axioms in the merged ontology.
//             for (OWLAxiom ax : merged.getAxioms()) {
//                System.out.println(ax);
//             }
            File file = new File("D:\\PWR\\MGR\\result.owl");
            man.saveOntology(merged, IRI.create(file.toURI()));
        }catch (Exception e){
            System.out.println(e);
        }
    }
}