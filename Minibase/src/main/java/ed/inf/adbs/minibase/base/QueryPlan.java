package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class QueryPlan {

    private Query query;

    private Operator plan;

    public QueryPlan(Query query) {
        this.query = query;
        plan = buildQueryPlan();
    }

    private Operator buildQueryPlan() {

        Head head = query.getHead();
        List<Atom> body = query.getBody();

        storeVariablePositions();

        // Check the first atom (scan) and see if it needs to be rewritten.
        if (((RelationalAtom) (body.get(0))).getTerms().stream().anyMatch(Constant.class::isInstance)) {
            List<Atom> newBody = rewriteAtom((RelationalAtom) body.get(0));
            newBody.addAll(body.subList(1, body.size()));
            query = new Query(head, newBody);
            body = newBody;
        }

        // First atom will be a relational atom (scan).
        RelationalAtom scanAtom = (RelationalAtom) body.get(0);
        plan = new ScanOperator(scanAtom);

        // Then check for conditions.
        List<ComparisonAtom> conditions = new ArrayList<>();

        for (Atom atom : body) {
            if (atom instanceof ComparisonAtom) {
                conditions.add((ComparisonAtom) atom);
            }
        }

        if (conditions.size() > 0) {
            plan = new SelectOperator(conditions, plan);
        }

        // Check head for projection.
        RelationalAtom atom = (RelationalAtom) body.get(0);
        List<Term> scanTerms = atom.getTerms();

        if (!head.getVariables().equals(scanTerms)) {
            plan = new ProjectOperator(plan, head);
        }

        return plan;
    }

    private void storeVariablePositions() {
        DatabaseCatalog catalog = DatabaseCatalog.getCatalogInstance();

        for (Atom atom : query.getBody()) {
            if (atom instanceof RelationalAtom) {
                for (int i = 0; i < ((RelationalAtom) atom).getTerms().size(); i++) {
                    Term term = ((RelationalAtom) atom).getTerms().get(i);
                    if (term instanceof Variable) {
                        catalog.setVarPos((Variable) term, i);
                    }
                }
            }
        }
    }

    private List<Atom> rewriteAtom(RelationalAtom relationalAtom) {

        HashMap<Integer, Constant> posOfConstants = new HashMap<>();

        int posCounter = 0;

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
            posOfNewVars.put(pos, rndVar);
        }

        // Construct the new atoms.
        posCounter = 0;

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
