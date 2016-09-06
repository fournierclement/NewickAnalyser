package graphe;
import java.util.*;
import java.util.regex.*;

public class Graphe{

    Noeud first;

    public Graphe(){}

    /**
    *@param Noeud, choisie comme premier noeud.
    **/
    public void setFirst(Noeud noeud){
        this.first = noeud;
    }

    /**
    *@return Noeud, celui par lequel le graphe commence.
    **/
    public Noeud getFirst(){
        return this.first;
    }

    /**
    *@param String, cherche le premier Noeud de ce nom.
    *@return Noeud, qui a le nom recherche.
    **/
    public Noeud chercheNoeud(String nom){
        Pattern patt = Pattern.compile(nom,66);
        Noeud curr = this.getFirst();
        while(!curr.isLeaf()){
            Matcher m = patt.matcher(curr.getName());
            if(m.matches()){
                return curr;
            }
            curr = curr.getNext();
        }
        return null;
    }

    public String toString(){
        String toString ="";
        Noeud curr = this.getFirst();
        while(!curr.isLeaf()){
            toString += curr.getLine() + " : " + curr.getName()+"\n";
            curr = curr.getNext();
        }
        return toString;
    }
}