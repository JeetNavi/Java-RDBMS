package ed.inf.adbs.minibase.base;

import java.util.*;

public class QueryPlan {

    private Query query;

    private Operator plan;

    public QueryPlan(Query query) {
        this.query = query;
        plan = buildQueryPlan();
    }

    private Operator buildQueryPlan() {

        DatabaseCatalog catalog = DatabaseCatalog.getCatalogInstance();

        Rewriter rewriter = new Rewriter(query);
        Query rewrittenQuery = rewriter.rewriteQuery();

        rewrittenQuery.setBodyInfo();


        Head head = query.getHead();

        storeVariablePositions();

        HashMap<Variable, String> varr = catalog.getVarRelations();
        HashMap<Variable, Integer> varp = catalog.getVarPositions();
        HashMap<String, HashMap<Variable, Integer>> varlp = catalog.getLocalVarPositions();

        List<Atom> body = rewriteBody();

        List<RelationalAtom> relationalAtoms = new ArrayList<>();
        List<ComparisonAtom> comparisonAtoms = new ArrayList<>();

        // Split the body into two; relational and comparison atoms.
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                relationalAtoms.add((RelationalAtom) atom);
            }
            else {
                comparisonAtoms.add((ComparisonAtom) atom);
            }
        }

        // Split the comparisonAtoms into two; join and selection conditions.
        // Join conditions can be hashmap; SelectionCondition -> String[2]. (SelectionCondition can be list of comparisonAtoms).
        // Selection conditions can be hashmap; SelectionCondition -> String. (SelectionCondition can be list of comparisonAtoms).
        // Constant conditions can be list. This can be evaluated first to optimise.


        // Now we have a rewritten query. All relational atoms only consist of unique variables.
        // U(i, u, z), i=1, u=1, R(a,o,c), o=4, a=l, S(x,h,l), h=smith

        HashMap<String, List<ComparisonAtom>> selectionsOnRelation = new HashMap<>();

        HashMap<String, List<ComparisonAtom>> joinConditionRhs = new HashMap<>();
        HashMap<String, HashMap<String, List<ComparisonAtom>>> joinConditionsOnRelations = new HashMap<>();

        for (ComparisonAtom comparisonAtom : comparisonAtoms) {
            // If lhs or rhs is constant, it is a selection.
            Term lhs = comparisonAtom.getTerm1();
            Term rhs = comparisonAtom.getTerm2();
            ComparisonOperator op = comparisonAtom.getOp();
            if (lhs instanceof Variable && rhs instanceof Variable) {
                if ((catalog.getVarRelation((Variable) lhs)).equals(catalog.getVarRelation((Variable) rhs))) {
                    // Variables belong to same relation; so it is a selection condition.
                    selectionsOnRelation.computeIfAbsent(catalog.getVarRelation((Variable) lhs), k -> new ArrayList<>()).add(comparisonAtom);
                }
                else {
                    // Variables belong to different relations; so it is a join condition.
                    // TODO
                    joinConditionRhs.computeIfAbsent(catalog.getVarRelation((Variable) rhs), k -> new ArrayList<>()).add(comparisonAtom);
                    joinConditionsOnRelations.put(catalog.getVarRelation((Variable) lhs), joinConditionRhs);
                }
            }
            else if (lhs instanceof Variable && rhs instanceof Constant) {
                // Selection condition.
                selectionsOnRelation.computeIfAbsent(catalog.getVarRelation((Variable) lhs), k -> new ArrayList<>()).add(comparisonAtom);
            }
            else if (lhs instanceof Constant && rhs instanceof Variable) {
                // Selection condition.
                selectionsOnRelation.computeIfAbsent(catalog.getVarRelation((Variable) rhs), k -> new ArrayList<>()).add(comparisonAtom);
            }
            else {
                // lhs and rhs are constants. If this condition holds, ignore it. If this condition is false, return null.
                SelectionCondition constantCondition = new SelectionCondition(Collections.singletonList(comparisonAtom));
                if (!constantCondition.evaluateOnTuple(new Tuple(null))) {
                    return null;
                }
            }
        }

        Operator lhsOp;
        Operator rhsOp;

        RelationalAtom firstRelationalAtom = relationalAtoms.get(0);
        lhsOp = new ScanOperator(firstRelationalAtom);
        if (selectionsOnRelation.containsKey(firstRelationalAtom.getName())) {
            // There is a selection condition on the first relational atom.
            lhsOp = new SelectOperator(new SelectionCondition(selectionsOnRelation.get(firstRelationalAtom.getName())), lhsOp);
        }

        for (int i = 1; i < relationalAtoms.size(); i++) {
            RelationalAtom prevRelationalAtom = relationalAtoms.get(i-1);
            RelationalAtom relationalAtom = relationalAtoms.get(i);
            rhsOp = new ScanOperator(relationalAtom);
            if (selectionsOnRelation.containsKey(relationalAtom.getName())) {
                rhsOp = new SelectOperator(new SelectionCondition(selectionsOnRelation.get(relationalAtom.getName())), rhsOp);
            }

            List<ComparisonAtom> joinConditions = new ArrayList<>();

            if (joinConditionsOnRelations.containsKey(prevRelationalAtom.getName()) && joinConditionRhs.containsKey(relationalAtom.getName())) {
                joinConditions.addAll(joinConditionsOnRelations.get(prevRelationalAtom.getName()).get(relationalAtom.getName()));
            }
            if (joinConditionsOnRelations.containsKey(relationalAtom.getName()) && joinConditionRhs.containsKey(prevRelationalAtom.getName())) {
                joinConditions.addAll(joinConditionsOnRelations.get(relationalAtom.getName()).get(prevRelationalAtom.getName()));
            }

            if (joinConditions.size() > 0) {
                lhsOp = new JoinOperator(lhsOp, rhsOp, new SelectionCondition(joinConditions));
            }
            else {
                lhsOp = new JoinOperator(lhsOp, rhsOp, null);
            }
        }


        return lhsOp;
    }

    private List<Atom> rewriteBody() {

        List<Atom> body = query.getBody();
        List<Atom> rewrittenBody = new ArrayList<>();

        int posOfFirstVar = 0;

        // First, rewrite relational atoms that contain constants.
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                if (((RelationalAtom) (atom)).getTerms().stream().anyMatch(Constant.class::isInstance)) {
                    List<Atom> rewrittenAtom = rewriteAtomConst((RelationalAtom) atom, posOfFirstVar);
                    rewrittenBody.addAll(rewrittenAtom);
                }
                else {
                    rewrittenBody.add(atom);
                }
                posOfFirstVar += ((RelationalAtom) atom).getTerms().size();
            }
            else {
                // We have reached the second half of the body with comparison atoms.
                rewrittenBody.add(atom);
            }
        }

        body = rewrittenBody;
        rewrittenBody = new ArrayList<>();

        // Then we rewrite atoms that contain variable that have appeared in earlier atoms.
        // R(x,y), S(y,z) -> R(x,y), S(<new>,z), y=<new>.
        List<Variable> currentVars = new ArrayList<>();
        posOfFirstVar = 0;
        HashSet<Variable> appearedVars = new HashSet<>();
        boolean atomAdded = false;
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                for (Term term : ((RelationalAtom) atom).getTerms()) {
                    if (term instanceof Variable) {
                        if (appearedVars.contains(term)){
                            rewrittenBody.addAll(rewriteAtomVar((RelationalAtom) atom, appearedVars, posOfFirstVar));
                            atomAdded = true;
                        }
                        else {
                            currentVars.add((Variable) term);
                            //appearedVars.add((Variable) term);
                        }
                    }
                }
                posOfFirstVar += ((RelationalAtom) atom).getTerms().size();
                appearedVars.addAll(currentVars);
                currentVars.clear();
            }
            if (!atomAdded) {
                rewrittenBody.add(atom);
            }
            else {
                atomAdded = false;
            }
        }

        return rewrittenBody;

    }

    private void storeVariablePositions() {

        //Maybe moving this to databasecatalog?
        // BTW we also store var relations in here too.

        DatabaseCatalog catalog = DatabaseCatalog.getCatalogInstance();

        int globalPosCounter = 0;
        int localPosCounter = 0;

        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                for (int i = 0; i < ((RelationalAtom) atom).getTerms().size(); i++) {
                    Term term = ((RelationalAtom) atom).getTerms().get(i);
                    if (term instanceof Variable) {
                        catalog.setVarPos((Variable) term, globalPosCounter);
                        catalog.setLocalVarPos(((RelationalAtom) atom).getName(), (Variable) term, localPosCounter);
                        //catalog.setVarRelation((Variable) term, ((RelationalAtom) atom).getName());
                        catalog.setVarRelationIfNotExists((Variable) term, ((RelationalAtom) atom).getName());
                    }
                    globalPosCounter += 1;
                    localPosCounter += 1;
                }
            }
            localPosCounter = 0;
        }
    }

    private List<Atom> rewriteAtomVar(RelationalAtom relationalAtom, HashSet<Variable> appearedVars, int posOfFirstVar) {
        // R(x,y), S(y,z) -> R(x,y), S(<new>,z), y=<new>.
        // S(y,z) will get passed in.
        // So we detect which variable is already present (y).
        // Then we generate a new variable and assign it to old variable in a new compAtom

        List<Atom> result = new ArrayList<>();
        Random rnd = new Random();
        int posCounter = posOfFirstVar;
        int localPosCounter = 0;

        for (Term term : relationalAtom.getTerms()) {
            if (term instanceof Variable && appearedVars.contains(term)) {
                Variable rndVar = new Variable(String.valueOf((char) ('a' + rnd.nextInt(26))));
                while (DatabaseCatalog.getCatalogInstance().getVarPositions().containsKey((rndVar))){
                    rndVar.changeName(rndVar.getName() + (String.valueOf((char) ('a' + rnd.nextInt(26)))));
                }
                // We have generated a new variable in rndVar
                DatabaseCatalog.getCatalogInstance().setVarPos(rndVar, posCounter);
                DatabaseCatalog.getCatalogInstance().setLocalVarPos((relationalAtom.getName()), (Variable) term, localPosCounter);
                DatabaseCatalog.getCatalogInstance().setVarRelation(rndVar, relationalAtom.getName());
                relationalAtom.replaceVar((Variable) term, rndVar);
                result.add(new ComparisonAtom(term, rndVar, ComparisonOperator.EQ));
            }
            posCounter += 1;
            localPosCounter += 1;
        }

        result.add(relationalAtom);

        return result;
    }


    private List<Atom> rewriteAtomConst(RelationalAtom relationalAtom, int posOfFirstVar) {

        HashMap<Integer, Constant> posOfConstants = new HashMap<>();

        int posCounter = 0;
        int localPosCounter = 0;

        for (Term term : relationalAtom.getTerms()) {
            if (term instanceof Constant){
                posOfConstants.put(posCounter, (Constant) term);
            }
            posCounter += 1;
        }


        Random rnd = new Random();

        HashMap<Integer, Variable> posOfNewVars = new HashMap<>();

        for (Integer pos : posOfConstants.keySet()) {
            Variable rndVar = new Variable(String.valueOf((char) ('a' + rnd.nextInt(26))));

            while (DatabaseCatalog.getCatalogInstance().getVarPositions().containsKey((rndVar))){
                rndVar.changeName(rndVar.getName() + (String.valueOf((char) ('a' + rnd.nextInt(26)))));
            }
            // We have generated a new variable in rndVar
            DatabaseCatalog.getCatalogInstance().setVarPos(rndVar, pos);
            DatabaseCatalog.getCatalogInstance().setVarRelation(rndVar, relationalAtom.getName());
            posOfNewVars.put(pos, rndVar);
        }

        // Construct the new atoms.
        posCounter = posOfFirstVar;

        List<ComparisonAtom> conditions = new ArrayList<>();

        List<Term> newVars = new ArrayList<>();

        for (Term term : relationalAtom.getTerms()) {
            if (term instanceof Constant) {
                newVars.add(posOfNewVars.get(posCounter));
                conditions.add(new ComparisonAtom(posOfNewVars.get(posCounter), posOfConstants.get(posCounter), ComparisonOperator.EQ));
            }
            else {
                newVars.add(term);
            }
            posCounter += 1;
        }

        Atom scanAtom = new RelationalAtom(relationalAtom.getName(), newVars);

        List<Atom> result = new ArrayList<>();
        result.add(scanAtom);
        result.addAll(conditions);

        return result;
    }

    public Operator getPlan() {
        return plan;
    }
}
