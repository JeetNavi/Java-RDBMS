package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.Utils;

import java.util.ArrayList;
import java.util.Collections;
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

    public void mapVariables(HashMap<Term, Term> mapping){
        List<Term> newTerms = new ArrayList<>();

        for (Term term : this.terms){
            newTerms.add(mapping.getOrDefault(term, term));
        }

        this.terms = newTerms;
    }

    public boolean equals (RelationalAtom atom){
        return atom.toString().equals(this.toString());
    }

    @Override
    public String toString() {
        return name + "(" + Utils.join(terms, ", ") + ")";
    }
}
