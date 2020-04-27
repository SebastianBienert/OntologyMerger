package OAEI;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

import java.lang.reflect.Array;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OAEIOntologyMerger {
    public OWLOntology Merge(OWLOntology firstOntology, OWLOntology secondOntology, OAEIMappingCollection mappings) throws Exception {
        OWLOntologyManager ontologyManager = firstOntology.getOWLOntologyManager();
        IRI IOR = IRI.create(firstOntology.getOntologyID().getOntologyIRI().get().getIRIString() +
                "_" + secondOntology.getOntologyID().getOntologyIRI().get().getFragment());
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology mergedOntology = man.createOntology(IOR);


        ArrayList<OWLOntology> ontologies = new ArrayList<>();
        ontologies.add(firstOntology);
        ontologies.add(secondOntology);

        final OWLEntityRenamer renamer = new OWLEntityRenamer(ontologyManager, ontologies);

       // secondOntology.axioms().forEach(System.out::println);
        List<String> test = secondOntology.getSignature()
                .stream()
                .filter(x -> !x.isBuiltIn())
                .map(x -> x.getIRI().toString())
                .collect(Collectors.toList());


        Map<OWLEntity, IRI> entity2IRIMapSecond = secondOntology.getSignature()
                .stream()
                .filter(x -> !x.isBuiltIn() && mappings.get(x.getIRI().toString()).size() > 0)
                .collect(Collectors.toMap(
                        x -> x,
                        x -> IRI.create(getMappedEntityName(
                                IOR.getShortForm(),
                                x.getIRI().toString(),
                                mappings.get(x.getIRI().toString()).get(0).getEntityName()
                        ))));

        Map<OWLEntity, IRI> entity2IRIMapFirst = firstOntology.getSignature()
                .stream()
                .filter(x -> !x.isBuiltIn() && mappings.get(x.getIRI().toString()).size() > 0)
                .collect(Collectors.toMap(
                        x -> x,
                        x -> IRI.create(getMappedEntityName(
                                IOR.getShortForm(),
                                x.getIRI().toString(),
                                mappings.get(x.getIRI().toString()).get(0).getEntityName()
                        ))));

        if(entity2IRIMapSecond.isEmpty()){
            throw new Exception("Empty mapping");
        }
        secondOntology.applyChanges(renamer.changeIRI(entity2IRIMapSecond));
        firstOntology.applyChanges(renamer.changeIRI(entity2IRIMapFirst));

        //secondOntology.axioms().forEach(System.out::println);

        mergedOntology.addAxioms(firstOntology.getAxioms());
        mergedOntology.addAxioms(secondOntology.getAxioms());


//        for (OWLAxiom ax : firstOntologyAxioms) {
//            List<OWLEntity> owlEntities = ax.getSignature().stream()
//                    .filter(entity -> mapping.getMapping().containsKey(entity.getIRI().toString()))
//                    .collect(Collectors.toList());
//
//            if (owlEntities.size() > 0) {
//                //
//            }
//            else{
//                mergedOntology.addAxiom(ax);
//            }
//        }

//        List<OWLClass> firstOntologyMappedEntities = firstOntology.getSignature()
//                .stream()
//                .filter(x -> x.isOWLClass() && !x.isBuiltIn() && !mapping.getMapping().containsKey(x.getIRI().toString()))
//                .map(x -> x.asOWLClass())
//                .collect(Collectors.toList());
//
//        for (OWLClass clazz : firstOntologyMappedEntities) {
//            List<OWLEquivalentClassesAxiom> owlEquivalentClassesAxioms = firstOntology.equivalentClassesAxioms(clazz)
//                                .collect(Collectors.toList());
//
//
//
//        }



//        Stream<OWLEntity> secondOntologyMappedEntities = secondOntology.getSignature()
//                .stream()
//                .filter(x -> !x.isBuiltIn() && !mapping.getMapping().containsKey(x.getIRI()));
//
//        firstOntology.axioms()
//
//       // mergedOntology.addAxioms(firstOntologynotMappedAxioms);

        return mergedOntology;
    }

    private String getMappedEntityName(String mergedOntologyName, String firstEntityIRI, String secondEntityIRI){
        String firstEntityName = firstEntityIRI.substring(7).replace('#', '@');
        String secondEntityName = secondEntityIRI.replace('#', '@');
//        String firstEntityName = key.substring(7).replace('#', '@');
//        String secondEntityName = this.mapping.get(key).get(0).getEntityName().replace('#', '@');

        ArrayList<String> alphabeticalSort = new ArrayList<>(Arrays.asList(firstEntityName, secondEntityName));
        alphabeticalSort.sort(Collator.getInstance());

        String result = "http://" +mergedOntologyName + "#" + alphabeticalSort.get(0) + "||" + alphabeticalSort.get(1);
        return result;
    }


    public double CalculateKnwoledgeIncrease(OWLOntology firstOntology, OWLOntology secondOntology, OAEIMappingCollection mappings){
        long firstOnotologyConceptsCount = GetOntologyConceptsCount(firstOntology);
        long secondOnotologyConceptsCount = GetOntologyConceptsCount(secondOntology);

        long sizeOfMapping = mappings.allMapings.keySet().stream()
                .filter(x -> x.contains(firstOntology.getOntologyID().getOntologyIRI().get().getShortForm()))
                .count();

        double maxDistancesSum = mappings.allMapings.entrySet().stream()
                .filter(x -> x.getKey().contains(firstOntology.getOntologyID().getOntologyIRI().get().getShortForm()))
                .map(x -> x.getValue().stream()
                    .map(EntityEquivalent::getDistance)
                    .max(Double::compare).orElse(0.0))
                .mapToDouble(n -> n)
                .sum();

        long numberOfEquivalents = mappings.allMapings.entrySet().stream()
                .filter(x -> x.getKey().contains(firstOntology.getOntologyID().getOntologyIRI().get().getShortForm()))
                .map(x -> x.getValue())
                .map(x -> x.stream().count())
                .mapToLong(x -> x)
                .sum();


        double konwledgeIncrease = 1 - ((sizeOfMapping - maxDistancesSum) /
                (firstOnotologyConceptsCount + secondOnotologyConceptsCount - numberOfEquivalents));

        return konwledgeIncrease;
    }


    private long GetOntologyConceptsCount(OWLOntology ontology){
        long count = ontology.classesInSignature().filter(x -> x.isOWLClass() && !x.isBuiltIn()).count();
        return count;
    }

}
