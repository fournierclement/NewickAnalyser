package automate;
import java.util.*;
import java.util.regex.*;
import java.io.*;
import graphe.*;

public class Auto{
    State initState;
    State currState;

    public Auto(){}

    /**
    *Parcour un graphe pour le faire reconnaitre par le graphe.
    *@param Graphe pour etre reconnu par l'automate.
    *@return String, texte decrivant chaque erreurs.
    *@throw Exception.
    **/
    public String analyser(Graphe graph){
        String analyse = "";
        this.init();
        Noeud currGraph = graph.getFirst();
        analyse = this.goOn(analyse, currGraph);
        return analyse;
    }

    /**
    *Verifie si le noeud du graphe correspond à une transition de l'automate.
    *Passe à l'état suivant dans l'automate.
    *@param Noeud, le symbole du graphe.
    *@return boolean, True s'ils correspondent.
    **/
    private boolean nextState(Noeud currGraph){
        State next = this.state().searchTrans(currGraph.getName());
        if(next.error()){
            return false;
        }else{
            this.setState(next);
            return true;
        }
    }
    /**
    *Deroule l'automate, s'il y a une transition qui correspond, passe aux noeuds suivants.
    *Sinon on recherche un point d'accroche.
    *@param String, les erreurs de syntaxe.
    *@param Noeud, le symbole du graphe à reconnaitre.
    *@return String, les erreurs de syntaxe.
    **/
    private String goOn(String erreurs, Noeud currGraph){
        //On verifie qu'on n'est pas a la fin
        if (!currGraph.isLeaf() || !this.state().end()){
            //On cherche la transition suivante.
            if (this.nextState(currGraph)){
                //System.out.println(currGraph.getName()+" is fine");
                //Et on deroule.
                return this.goOn(erreurs, currGraph.getNext());
            }else {
                //Sinon on se raccroche quelque part.
                return searchMatch(erreurs, currGraph);
            }
        }else{//Condition terminale
            return end(erreurs, currGraph);
        }
    }

    /**
    *Cherche un nouveau point de cohérence entre l'automate et le texte.
    *On saute des symbole du texte jusqu'a retomber sur une transition possible.
    *De plus on precise l'erreur.
    *@param String, le block d'erreur de syntaxe.
    *@param Noeud, le symbole du graphe à reconnaitre.
    *@return String, le block d'erreur de syntaxe avec les nouvelles erreurs.
    **/
    private String searchMatch(String erreurs, Noeud currGraph){
        Noeud next = currGraph;
        //On parcours le fichier jusqu'a trouver quelque chose qui marche.
        while (!nextState(next) && !next.isLeaf()){
            //System.out.println(next.getName()+" is Wrong");
            next = next.getNext(); 
        }
        //Si on arrive au bout avant, il manque un symbole dans le texte.
        if(next.isLeaf()){
            erreurs += "Error line " + currGraph.getLine() + " : a token is missing.\n";
            return erreurs;
        }else{
            erreurs += "Error line " + currGraph.getLine() + " : a token is missing, found in " + next.getLine() + ".\n";
            return this.goOn(erreurs, next.getNext());
        }
    }

    /**
    *Analyse la fin du graphe ou de l'automate.
    *@param String, le block d'erreur de syntaxe.
    *@param Noeud, le symbole du graphe.
    *@return String, le block d'erreur de syntaxe.
    **/
    private String end(String erreurs, Noeud currGraph){
        if(!this.state().end()){
            erreurs += "Error : end of the file without terminal symbol.\n";
        }else if (!currGraph.isLeaf()){
            erreurs += "Error : excesive expression in file.";
        }
        return erreurs;
        //Je ne sais pas trop quoi faire si le fichier de base à des trucs en trop ecris, est-ce une erreur ?
    }


    /**
    *Initialise l'automate.
    **/
    private void init(){
        this.setState(this.getInit());
    }

    /**
    *Renvoie l'etat courant.
    *@return state l'etat courant.
    **/
    private State state(){
        return this.currState;
    }

    /**
    *change l'etat courant.
    *@param State, le novuel etat courant.
    **/
    private void setState(State newState){
        this.currState = newState;
    }
    
    /**
    *Defini l'etat initial de l'automate.
    *@param State, l'etat initial.
    **/
    public void setInit(State init){
        this.initState = init;
    }

    /**
    *Retourne l'état initial de l'automate.
    *@return State, l'etat initial.
    **/
    private State getInit(){
        return this.initState;
    }
}

class State{
    ArrayList<Transition> trans = new ArrayList<Transition>();
    boolean error = false;
    boolean end = false;

    State(){};

    /**
    *Decrit un etat comme final.
    **/
    void endState(){
        this.end = true;
    }

    /**
    *@return boolean, True si l'état est final.
    **/
    boolean end(){
        return this.end;
    }

    /**
    *Ajoute une transition de cet etat à un autre.
    *@param State, l'etat vers lequel pointe la transition.
    *@param Pattern, une expression regulier pour passer de cet etat à l'autre.
    **/
    void addTrans(State state, Pattern pattern){
        this.trans.add(new Transition(state, pattern));
    }

    /**
    *recherche et retourne l'etat avec une transition qui correspond au mot.
    *@param String, un mot pour passer une transition.
    *@return State, l'etat suivant ou un etat tel que State.error() == True.
    **/
    State searchTrans(String word){
        State next = new State();
        next.toggleError(true);

        for(Transition transi : this.trans){
            Matcher m = transi.getPatt().matcher(word);
            if(m.matches()){
                next = transi.getState();
            }
        }

        return next;
    }

    /**
    *@return boolean, true si l'etat est un etat erreur.
    **/
    boolean error(){
        return this.error;
    }

    /**
    *@param boolean, true pour que l'etat soit un etat erreur.
    **/
    void toggleError(boolean error){
        this.error = error;
    }
}

class Transition{
    State next;
    Pattern trans;

    /**
    *@param State, l'etat suivant.
    *@param Pattern, l'expression regulière compilee pour passer à l'etat suivant.
    **/
    Transition(State next, Pattern trans){
        this.next = next;
        this.trans = trans;
    }

    /**
    *@return State, l'etat en bout de transition.
    **/
    State getState(){
        return this.next;
    }

    /**
    *@return Pattern, l'expression regulière compilee pour passer à l'etat suivant.
    **/
    Pattern getPatt(){
        return this.trans;
    }
}