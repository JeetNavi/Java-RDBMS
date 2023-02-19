package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RelationalAtom extends Atom {
    private String name;

    private List<Term> terms;

    public RelationalAtom(String name, List<Term> terms) {
        this.name = name;
        this.terms = terms;
    }

    public String getName() {
        return name;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public boolean equals(RelationalAtom atom){
        return this.toString().equals(atom.toString());
    }

    public RelationalAtom mapTerms (HashMap<Term, Term> mapping) {

        List<Term> mappedTerms = new ArrayList<>();
        boolean found = false;

        for (Term term : terms) {
            if (!(term instanceof Variable)){
                mappedTerms.add(term);
            }
            else {
                for (Term key : mapping.keySet()){
                    if (key instanceof Variable) {
                        if (((Variable) key).equals((Variable) term)){
                            mappedTerms.add(mapping.get(key));
                            found = true;
                            break;
                        }
                    }
                }
                if (!found){
                    mappedTerms.add(term);
                }
                else {
                    found = false;
                }
            }

        }

        return new RelationalAtom(this.name, mappedTerms);
    }

    @Override
    public String toString() {
        return name + "(" + Utils.join(terms, ", ") + ")";
    }
}
