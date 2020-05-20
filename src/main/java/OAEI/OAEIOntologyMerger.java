package OAEI;

import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.javatuples.Pair;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

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

        Map<OWLEntity, IRI> entity2IRIMapSecond = secondOntology.getSignature()
                .stream()
                .filter(x -> !x.isBuiltIn() && mappings.get(x.getIRI().toString()).size() > 0)
                .collect(Collectors.toMap(
                        x -> x,
                        x -> IRI.create(getMappedEntityName(
                                IOR.getShortForm(),
                                x.getIRI().toString(),
                                mappings.get(x.getIRI().toString()).stream().map(m -> m.getEntityName()).collect(Collectors.toList())
                        ))));

        Map<OWLEntity, IRI> entity2IRIMapFirst = firstOntology.getSignature()
                .stream()
                .filter(x -> !x.isBuiltIn() && mappings.get(x.getIRI().toString()).size() > 0)
                .collect(Collectors.toMap(
                        x -> x,
                        x -> IRI.create(getMappedEntityName(
                                IOR.getShortForm(),
                                x.getIRI().toString(),
                                mappings.get(x.getIRI().toString()).stream().map(m -> m.getEntityName()).collect(Collectors.toList())
                        ))));

        if(entity2IRIMapSecond.isEmpty()){
            throw new Exception("Empty mapping");
        }
        secondOntology.applyChanges(renamer.changeIRI(entity2IRIMapSecond));
        firstOntology.applyChanges(renamer.changeIRI(entity2IRIMapFirst));

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
        return mergedOntology;
    }

    private String getMappedEntityName(String mergedOntologyName, String firstEntityIRI, List<String> secondEntityIRIs) {
        boolean isEntityFirstLevel = !firstEntityIRI.substring(firstEntityIRI.lastIndexOf('/') + 1, firstEntityIRI.indexOf('#')).contains("_");
        String firstEntityName = isEntityFirstLevel ? firstEntityIRI.substring(7) : firstEntityIRI.substring(firstEntityIRI.indexOf('#') + 1);
        ArrayList<String> firstEntityParts = new ArrayList(Arrays.asList(firstEntityName.replace("#", "@").split("__OR__")));
        List<String> secondEntityParts = secondEntityIRIs.stream().map(x -> x.replace('#', '@')).collect(Collectors.toList());
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


    public double CalculateKnwoledgeIncrease(OWLOntology firstOntology, OWLOntology secondOntology, OAEIMappingCollection mappings){
        long firstOnotologyConceptsCount = GetOntologyConceptsCount(firstOntology);
        long secondOnotologyConceptsCount = GetOntologyConceptsCount(secondOntology);

        long sizeOfMapping2 = mappings.allMapings.keySet().stream()
                .filter(x -> x.contains(firstOntology.getOntologyID().getOntologyIRI().get().getShortForm()))
                .count();

        long sizeOfMapping = 0l;
        for(OWLClass c : firstOntology.classesInSignature().collect(Collectors.toList())){
            List<String> concepts = OWLEntityToSplitConceptNames(c).stream().map(x -> x.replace("@", "#")).collect(Collectors.toList());
            if(concepts.stream().anyMatch(concept -> mappings.allMapings.keySet().stream().anyMatch(m -> m.substring(7).equals(concept)))){
                sizeOfMapping += 1;
            }
        }


//        long sizeOfMapping = mappings.allMapings.keySet().stream()
//                .filter(x -> firstOntology.classesInSignature().anyMatch(c -> x.contains(c.getIRI().getShortForm())))
//                .count();

        double maxDistancesSum = mappings.allMapings.entrySet().stream()
                .filter(x -> x.getKey().contains(firstOntology.getOntologyID().getOntologyIRI().get().getShortForm()))
                .map(x -> x.getValue().stream()
                    .map(EntityEquivalent::getDistance)
                    .max(Double::compare).orElse(0.0))
                .mapToDouble(n -> n)
                .sum();

        long numberOfEquivalents = 0l;
        for(OWLClass c : firstOntology.classesInSignature().collect(Collectors.toList())){
            List<String> concepts = OWLEntityToSplitConceptNames(c).stream().map(x -> x.replace("@", "#")).collect(Collectors.toList());
            Optional<String> mapping = mappings.allMapings.keySet().stream().filter(m -> concepts.stream().anyMatch(concept -> m.substring(7).equals(concept))).findFirst();
            if(mapping.isPresent()){
                numberOfEquivalents += mappings.get(mapping.get()).size();
            }
        }

//        long numberOfEquivalents = mappings.allMapings.entrySet().stream()
//                .filter(x -> x.getKey().contains(firstOntology.getOntologyID().getOntologyIRI().get().getShortForm()))
//                .map(x -> x.getValue())
//                .map(x -> x.stream().count())
//                .mapToLong(x -> x)
//                .sum();


        double konwledgeIncrease = 1 - ((sizeOfMapping - maxDistancesSum) /
                (firstOnotologyConceptsCount + secondOnotologyConceptsCount - numberOfEquivalents));

        return konwledgeIncrease;
    }

    public double NormalizeKnowledgeIncrease(List<Double> measures){
        Double sum = measures.stream().reduce(0d, (acc, x) -> acc + x);
        Double max = Double.valueOf(measures.size());
        Double min = 0.0;
        Double newMax = 1.0;
        Double newMin = 0.0;

        Double normalizedSum = ((sum - min) / (max - min)) * (newMax - newMin) + newMin;
        return normalizedSum;
    }


    private long GetOntologyConceptsCount(OWLOntology ontology){
        long count = ontology.classesInSignature().filter(x -> x.isOWLClass() && !x.isBuiltIn()).count();
        return count;
    }

}
