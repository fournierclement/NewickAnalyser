package graphe;
import java.util.*;

public class Noeud{
    String name="";
    Noeud next;
    int line;
    
    public Noeud(){}

    /**
    *True si le Noeud est vide.
    *@return Boolean, True si le noeud est vide.
    **/
    public boolean isLeaf(){
        return (this.getName().equals(""));
    }

    /**
    *Donne au noeud un numero de ligne.
    *@param Int, le numero de ligne.
    **/
    public void setLine(int line){
        this.line = line;
    }

    /**
    *Donne le numero de ligne du noeud.
    *@return Int, le numero de ligne.
    **/
    public int getLine(){
        return this.line;
    }

    /**
    *Donne au noeud un nom.
    *@param String, le nom du noeud.
    **/
    public void setName(String name){
        this.name = name;
    }

    /**
    *Donne le nom du noeud.
    *@return String, le nom du noeud.
    **/
    public String getName(){
        return this.name;
    }

    /**
    *Ajoute un noeud au noeud accessible depuis celui ci.
    *@param Noeud, nouveau noeud suivant.
    **/
    public void addNext(Noeud next){
        this.next = next;
    }

    /**
    *retourne le noeud suivant.
    *@return Noeud.
    **/
    public Noeud getNext(){
        return this.next;
    }

}