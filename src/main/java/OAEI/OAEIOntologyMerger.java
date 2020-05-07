package OAEI;

import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.javatuples.Pair;
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


       // firstOntology.addAxiom(new )

        ArrayList<OWLOntology> ontologies = new ArrayList<>();
        ontologies.add(firstOntology);
        ontologies.add(secondOntology);



        final OWLEntityRenamer renamer = new OWLEntityRenamer(ontologyManager, ontologies);

       // secondOntology.axioms().forEach(System.out::println);
        List<String> test = secondOntology.getSignature()
                .stream()
                .filter(x -> !x.isBuiltIn())
                .map(x -> GetUNescapedURIString(x.getIRI().toString()))
                .collect(Collectors.toList());


        Map<OWLEntity, IRI> entity2IRIMapSecond = secondOntology.getSignature()
                .stream()
                .filter(x -> !x.isBuiltIn() && mappings.get(x.getIRI().toString()).size() > 0)
                .collect(Collectors.toMap(
                        x -> x,
                        x -> IRI.create(getMappedEntityName(
                                IOR.getShortForm(),
                                x.getIRI().toString(),
                                mappings.get(x.getIRI().toString()).stream().map(m -> m.getEntityName()).collect(Collectors.toList()),
                                firstOntology.getSignature())
                        )));

        Map<OWLEntity, IRI> entity2IRIMapFirst = firstOntology.getSignature()
                .stream()
                .filter(x -> !x.isBuiltIn() && mappings.get(x.getIRI().toString()).size() > 0)
                .collect(Collectors.toMap(
                        x -> x,
                        x -> IRI.create(getMappedEntityName(
                                IOR.getShortForm(),
                                x.getIRI().toString(),
                                mappings.get(x.getIRI().toString()).stream().map(m -> m.getEntityName()).collect(Collectors.toList()),
                                firstOntology.getSignature()
                        ))));

        if(entity2IRIMapSecond.isEmpty()){
            throw new Exception("Empty mapping");
        }
        secondOntology.applyChanges(renamer.changeIRI(entity2IRIMapSecond));
        firstOntology.applyChanges(renamer.changeIRI(entity2IRIMapFirst));

        //secondOntology.axioms().forEach(System.out::println);

        mergedOntology.addAxioms(firstOntology.getAxioms());
        mergedOntology.addAxioms(secondOntology.getAxioms());



        //LAST CHANCE PLS
        ArrayList<OWLOntology> finalOntology = new ArrayList<>();
        finalOntology.add(mergedOntology);
        final OWLEntityRenamer finalRenamer = new OWLEntityRenamer(ontologyManager, finalOntology);
        List<Pair<OWLEntity, ArrayList<String>>> collect = mergedOntology.getSignature().stream()
                .map(x -> new Pair<>(x, OWLEntityToSplitConceptNames(x)))
                .collect(Collectors.toList());
        Map<OWLEntity, IRI> entityFInalIRIMapFirst = new HashMap<>();
        for (Pair<OWLEntity, ArrayList<String>> pair : collect){
            Optional<Pair<OWLEntity, ArrayList<String>>> superSet = collect.stream()
                    .filter(x -> OWLEntityToSplitConceptNames(x.getValue0())
                            .containsAll(pair.getValue1()) && !x.getValue0().getIRI().toString().equals(pair.getValue0().getIRI().toString()))
                    .findAny();
            if(superSet.isPresent()){
                entityFInalIRIMapFirst.put(pair.getValue0(), superSet.get().getValue0().getIRI());
            }
        }

        mergedOntology.applyChanges(finalRenamer.changeIRI(entityFInalIRIMapFirst));

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

    private String getMappedEntityName(String mergedOntologyName, String firstEntityIRI, List<String> secondEntityIRIs, Set<OWLEntity> secondOntologyEntities) {
        boolean isEntityFirstLevel = !firstEntityIRI.substring(firstEntityIRI.lastIndexOf('/') + 1, firstEntityIRI.indexOf('#')).contains("_");
        String firstEntityName = isEntityFirstLevel ? firstEntityIRI.substring(7) : firstEntityIRI.substring(firstEntityIRI.indexOf('#') + 1);
        ArrayList<String> firstEntityParts = new ArrayList(Arrays.asList(firstEntityName.replace("#", "@").split("__OR__")));
        List<String> secondEntityParts = secondEntityIRIs.stream().map(x -> x.replace('#', '@')).collect(Collectors.toList());

        List<OWLEntity> owlEntityStream = secondOntologyEntities.stream()
                .filter(x -> OWLEntityToSplitConceptNames(x)
                .containsAll(secondEntityIRIs.stream()
                        .map(e -> e.replace('#', '@'))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());

        if(owlEntityStream.size() > 1){
            System.out.println("STH WRONG");
        }

        if(owlEntityStream.size() == 1){
            secondEntityParts = owlEntityStream.stream().flatMap(x -> OWLEntityToSplitConceptNames(x).stream()).collect(Collectors.toList());
        }

//        String firstEntityName = key.substring(7).replace('#', '@');
//        String secondEntityName = this.mapping.get(key).get(0).getEntityName().replace('#', '@');

        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>(Stream.of(firstEntityParts, secondEntityParts)
                .flatMap(x -> x.stream())
                .collect(Collectors.toList()));

        ArrayList<String> alphabeticalSort = new ArrayList<String>(linkedHashSet);
        alphabeticalSort.sort(Collator.getInstance());

        String result = "http://" + mergedOntologyName + "#";
        for(int i = 0; i < alphabeticalSort.size(); i++){
            if(i == 0){
                result += alphabeticalSort.get(i);
            }
            else{
                result += "__OR__" + alphabeticalSort.get(i);
            }

        }

        return result;
    }

    private ArrayList<String> OWLEntityToSplitConceptNames(OWLEntity entity){
        String entityIRI = entity.getIRI().toString();
        boolean isEntityFirstLevel = !entityIRI.substring(entityIRI.lastIndexOf('/') + 1, entityIRI.indexOf('#')).contains("_");
        String entityName = isEntityFirstLevel ? entityIRI.substring(7) : entityIRI.substring(entityIRI.indexOf('#') + 1);
        ArrayList<String> firstEntityParts = new ArrayList(Arrays.asList(entityName.replace("#", "@").split("__OR__")));
        return firstEntityParts;
    }

    private String GetUNescapedURIString(String uri){
        return uri.replaceAll("%7C", "|");
    }


    public double CalculateKnwoledgeIncrease(OWLOntology firstOntology, OWLOntology secondOntology, OAEIMappingCollection mappings){
        long firstOnotologyConceptsCount = GetOntologyConceptsCount(firstOntology);
        long secondOnotologyConceptsCount = GetOntologyConceptsCount(secondOntology);

        long sizeOfMapping2 = mappings.allMapings.keySet().stream()
                .filter(x -> x.contains(firstOntology.getOntologyID().getOntologyIRI().get().getShortForm()))
                .count();

        long sizeOfMapping = mappings.allMapings.keySet().stream()
                .filter(x -> firstOntology.classesInSignature().anyMatch(c -> x.contains(c.getIRI().getShortForm())))
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
