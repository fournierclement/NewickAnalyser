package decoupe;
import java.util.regex.*;
import java.io.*;
import graphe.*;

public class Decoupeur{
    int lineNbr;
    Graphe graph;
    Noeud noeud;

    public Decoupeur(){}

    /**
    *Decoupe le texte du fichier en graphe, chaque groupe de caractères reconnu comme un mot est une feuille du graphe.
    *@param FileInputStream
    *@return Graphe
    *@throw Exception
    **/
    public Graphe decouperStream(FileInputStream fileStream) throws IOException{
        lineNbr = 1;
        graph = new Graphe();
        noeud = new Noeud();
        graph.setFirst(noeud);
        //lire le fichier jusqu'au bout, ! fonctions recursives !
        char carac = (char) fileStream.read();
        if(Pattern.matches("[a-zA-Z0-9#_]", String.valueOf(carac))){
            getTag(fileStream, carac);
        }
        else if ( carac == '['){
            getComment(fileStream, carac);
        }
        else if (carac == '('){
            getDescription(fileStream, carac);
        }
        else {
            getSpecial(fileStream, carac);
        }
        return graph;
    }

    /**
    *Consome les prochains caracteres qui peuvent appartenir à un identificateur.
    *Quand un caractere ne correspond pas, il fini le mot et appel la fonction designée pour le caractere.
    *@param FileInputStream, le stream du fichier en lecture.
    *@param char le prochain caractere a prendre en compte.
    *@return void, la fonction suivante.
    **/
    private void getTag(FileInputStream fileStream, char carac) throws IOException{
        noeud.setLine(lineNbr);
        String nom = "";
        while (Pattern.matches("[a-zA-Z0-9#_.]", String.valueOf(carac))){
            nom += String.valueOf(carac);
            carac = (char) fileStream.read();
        }
        //Gestion noeud.
        noeud.setName(nom);
        Noeud next = new Noeud();
        noeud.addNext(next);
        noeud = next;
        //Gestion carac.
        if ( carac == '['){
            getComment(fileStream, carac);
        } else if (carac == '('){
            getDescription(fileStream, carac);
        }else {//condition terminale
            getSpecial(fileStream, carac);
        }
    }

    /**
    *Consome les prochains caracteres qui peuvent appartenir à un commentaire.
    *Quand le commentaire est fini, la fonction passe la main pour le prochain carac.
    *@param FileInputStream, le stream du fichier en lecture.
    *@param char le prochain caractere a prendre en compte.
    *@return void, la fonction suivante.
    **/
    private void getComment(FileInputStream fileStream, char carac) throws IOException{
        noeud.setLine(lineNbr);
        String nom = "";
        while (carac != ']'){
            //sortir si on arrive en bout de fichier (le commentaire n'est pas vraimment valide ni donc validé).
            if (carac == (char) -1){
                getSpecial(fileStream, carac);
                return;
            }
            if (carac == '\n'){ lineNbr +=1; }
            nom += String.valueOf(carac);
            carac = (char)fileStream.read();
        }
        nom += String.valueOf(carac);
        carac = (char)fileStream.read();
        //Gestion noeud.
        if (nom.equals("[&R]")||nom.equals("[&r]")){
            noeud.setName(nom);
            Noeud next = new Noeud();
            noeud.addNext(next);
            noeud = next;
        }
        //Gestion carac.
        if (Pattern.matches("[a-zA-Z0-9#_.]", String.valueOf(carac))){
            getTag(fileStream, carac);
        } else if (carac == '('){
            getDescription(fileStream, carac);
        }else {//condition terminale
            getSpecial(fileStream, carac);
        }
    }

    /**
    *Consome le caractere spécial, puis donne la main pour le prochain caractere.
    *@param FileInputStream, le stream du fichier en lecture.
    *@param char le prochain caractere a prendre en compte.
    *@return void, la fonction suivante.
    **/
    private void getSpecial(FileInputStream fileStream, char carac) throws IOException{
        //Ce caractere :
        if (carac == (char) -1){ return ; }//fin du fichier.
        else if (carac == '\n'){lineNbr += 1 ;}//nouvelle ligne.
        else if (carac != ' ' && carac !='\t'){//Si ce n'est pas un espace ou un retour chariot
            noeud.setLine(lineNbr);
            noeud.setName(String.valueOf(carac));
            Noeud next = new Noeud();
            noeud.addNext(next);
            noeud = next;
        }
        //Caractere suivant
        carac = (char)fileStream.read();
        if(Pattern.matches("[a-zA-Z0-9#_.]", String.valueOf(carac))){
            getTag(fileStream, carac);
        } else if ( carac == '['){
            getComment(fileStream, carac);
        } else if (carac == '('){
            getDescription(fileStream, carac);
        } else {
            getSpecial(fileStream, carac);
        }
    }

        /**
    *Consome les prochains caracteres qui peuvent appartenir à une description d'arbre.
    *Quand la description est fini(;), la fonction passe la main pour le prochain carac.
    *@param FileInputStream, le stream du fichier en lecture.
    *@param char le prochain caractere a prendre en compte.
    *@return void, la fonction suivante.
    **/
    private void getDescription(FileInputStream fileStream, char carac) throws IOException{
        noeud.setLine(lineNbr);
        String nom = "";
        while (carac != ';'){
            //sortir si on arrive en bout de fichier (la description n'est pas vraiment valide ni donc validé).
            if (carac == (char) -1){
                getSpecial(fileStream, carac);
                return;
            }
            if (carac == '\n'){ lineNbr +=1; }
            nom += String.valueOf(carac);
            carac = (char)fileStream.read();
        }
        //Gestion noeud.
        noeud.setName(nom);
        Noeud next = new Noeud();
        noeud.addNext(next);
        noeud = next;

        //Gestion carac (;)
        getSpecial(fileStream, carac);
    }
}