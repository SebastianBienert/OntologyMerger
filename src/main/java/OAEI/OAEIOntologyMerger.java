package OAEI;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OAEIOntologyMerger {
    public OWLOntology Merge(OWLOntology firstOntology, OWLOntology secondOntology, OAEIMapping mapping) throws Exception {
        OWLOntologyManager ontologyManager = firstOntology.getOWLOntologyManager();
        OWLDataFactory df = ontologyManager.getOWLDataFactory();
        IRI IOR = IRI.create(firstOntology.getOntologyID().getOntologyIRI().get().getIRIString() + "+" + secondOntology.getOntologyID().getOntologyIRI().get().getFragment());
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology mergedOntology = man.createOntology(IOR);

        Set<OWLAxiom> firstOntologyAxioms = firstOntology.getAxioms();

        final OWLEntityRenamer renamer = new OWLEntityRenamer(ontologyManager, Collections.singleton(secondOntology));

       // secondOntology.axioms().forEach(System.out::println);
        List<String> test = secondOntology.getSignature()
                .stream()
                .filter(x -> !x.isBuiltIn())
                .map(x -> x.getIRI().toString())
                .collect(Collectors.toList());


        Map<OWLEntity, IRI> entity2IRIMap = secondOntology.getSignature()
                .stream()
                .filter(x -> !x.isBuiltIn() && mapping.getMapping().containsKey(x.getIRI().toString()))
                .collect(Collectors.toMap(x -> x, x -> IRI.create(mapping.getMapping().get(x.getIRI().toString()).get(0).getEntityName())));

        if(entity2IRIMap.isEmpty()){
            throw new Exception("Empty mapping");
        }
        secondOntology.applyChanges(renamer.changeIRI(entity2IRIMap));

        //secondOntology.axioms().forEach(System.out::println);

        mergedOntology.addAxioms(firstOntologyAxioms);
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


    public double CalculateKnwoledgeIncrease(OWLOntology firstOntology, OWLOntology secondOntology, OAEIMapping mapping){
        long firstOnotologyConceptsCount = GetOntologyConceptsCount(firstOntology);
        long secondOnotologyConceptsCount = GetOntologyConceptsCount(secondOntology);

        long sizeOfMapping = mapping.getMapping().keySet().stream()
                .filter(x -> x.contains(firstOntology.getOntologyID().getOntologyIRI().get().getShortForm()))
                .count();

        double maxDistancesSum = mapping.getMapping().entrySet().stream()
                .filter(x -> x.getKey().contains(firstOntology.getOntologyID().getOntologyIRI().get().getShortForm()))
                .map(x -> x.getValue().stream()
                    .map(EntityEquivalent::getDistance)
                    .max(Double::compare).orElse(0.0))
                .mapToDouble(n -> n)
                .sum();

        long numberOfEquivalents = mapping.getMapping().values().stream()
                .map(x -> x.stream().count())
                .mapToLong(x -> x)
                .sum() / 2;


        double konwledgeIncrease = 1 - ((sizeOfMapping - maxDistancesSum) /
                (firstOnotologyConceptsCount + secondOnotologyConceptsCount - numberOfEquivalents));

        return konwledgeIncrease;
    }


    private long GetOntologyConceptsCount(OWLOntology ontology){
        long count = ontology.classesInSignature().filter(x -> x.isOWLClass() && !x.isBuiltIn()).count();
        return count;
    }

}
