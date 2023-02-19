package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.Utils;

import java.util.List;

public class Query {
    private Head head;

    private List<Atom> body;

    public Query(Head head, List<Atom> body) {
        this.head = head;
        this.body = body;
    }

    public Head getHead() {
        return head;
    }

    public List<Atom> getBody() {
        return body;
    }

    public void removeFromBody(Atom atom) {
        this.body.remove(atom);
    }

//    public Query clone() {
//        return new Query(this.head, this.body);
//    }

    @Override
    public String toString() {
        return head + " :- " + Utils.join(body, ", ");
    }
}
