import java.util.*;
import java.util.regex.*;
import java.io.*;
import graphe.*;
import automate.*;
import decoupe.*;

public class NewickAnalyser {

    public static void main(String[] args){
        //Ouvrir le fichier dans un FileInputStream
        try {
            FileInputStream fileStream = new FileInputStream(args[0]);
            
            //Decouper le fichier en un graphe.
            Decoupeur decoup = new Decoupeur();
            Graphe fileGraph = decoup.decouperStream(fileStream);

            //System.out.println(fileGraph);

            //Le faire passer dans l'automate de newick.
            NewickAut newick = new NewickAut();
            String erreurSyntaxe = newick.analyser(fileGraph);
            //Si erreur, inutile de continuer.
            if(!erreurSyntaxe.isEmpty()){
                System.out.println(erreurSyntaxe);
                System.exit(1);
            }
            //Vérifier la correspondance des noms de variable.
            ArrayList<Noeud> taxLabels = pickTaxLabels(fileGraph);
            ArrayList<Noeud> taxTrees = pickTaxTrees(fileGraph);
            String differences = comparerLabels(taxLabels,taxTrees);
            if(taxLabels.size() != Integer.parseInt(fileGraph.chercheNoeud("ntax").getNext().getNext().getName())){
                differences += "Error : invalid ntax count";
            }
            if(!differences.isEmpty()){
                System.out.println(differences);
            }
            
            //Verifier la structure des arbres
            String erreurStructure = checkTrees(fileGraph);
            if(!erreurStructure.isEmpty()){
                System.out.println(erreurStructure);
            }
        } catch (FileNotFoundException e){
            System.out.println("There's not such file.");
            System.exit(1);
        }catch (IOException e){
            System.out.println("Reading issues");
            System.exit(1);
        }
    }

    /**
    *Va chercher les TaxLabels dans la premiere partie du graphe.
    *@param Graphe
    *@return ArrayList<Noeud>, contient des objets noeuds independants.
    **/
    static ArrayList<Noeud> pickTaxLabels(Graphe fileGraph){
        Noeud curr = fileGraph.chercheNoeud("taxlabels").getNext();
        ArrayList<Noeud> taxLabels = new ArrayList<Noeud>();
        while(!curr.getName().equals(";")){
            taxLabels.add(curr);
            curr = curr.getNext();
        }
        return taxLabels;
    }

    /**
    *Va chercher les TaxLabels dans la partie tress du graphe.
    *@param Graphe
    *@return ArrayList<Noeud>, contient des objets noeuds independants.
    **/
    static ArrayList<Noeud> pickTaxTrees(Graphe fileGraph){
        ArrayList<Noeud> taxTrees = new ArrayList<Noeud>();
        //Aller au premier "tree"
        Noeud curr = fileGraph.chercheNoeud("tree").getNext();
        //tant qu'on arrive pas à la fin.
        while( !Pattern.compile("end",66).matcher(curr.getName()).matches()){
            //on se deplace jusqu'a la description, avant ;.
            if(curr.getNext().getName().equals(";")){
                //On decoupe la chaine de carac en retirant les (),;
                String[] desc = curr.getName().split("[\\(\\)\\,\\:]");
                for(String nom : desc){
                    if (Pattern.matches("[a-zA-Z][a-zA-Z0-9_.]*",nom)){
                        Noeud tax = new Noeud();
                        tax.setName(nom); tax.setLine(curr.getLine());
                        taxTrees.add(tax);
                    }
                }
            }
            curr = curr.getNext();
        }
        return taxTrees;
    }

    /**
    *Comparer les deux NoArrayList<Noeud> et renvoie un text expliquant les differences.
    *@param ArrayList<Noeud>
    *@param ArrayList<Noeud>
    *@return String expliquant les differences.
    **/
    static String comparerLabels(ArrayList<Noeud> labels, ArrayList<Noeud> trees){
        String erreurs = "";
        //Comparons en 2 n² les taxons:
        for(Noeud label : labels){
            boolean found = false;
            for(Noeud tree : trees){
                found = found || (label.getName().equals(tree.getName()));
            }
            if(!found){
                erreurs += "Taxon '"+label.getName()+"' line "+label.getLine()+" in Taxa not found in Trees.\n";
            }
        }
        for(Noeud tree : trees){
            boolean found = false;
            for(Noeud label : labels){
                found = found || (tree.getName().equals(label.getName()));
            }
            if(!found){
                erreurs += "Taxon '"+tree.getName()+"' line "+tree.getLine()+" in Trees not found in Taxa.\n";
            }
        }
        return erreurs;
    }

    /**
    *Verifie que la structure des arbres est correcte.
    *@param Graphe, le graphe du fichier.
    *@return String, le texte expliquatif des erreurs.
    **/
    static String checkTrees(Graphe fileGraph){
        String erreurs = "";
        Noeud curr = fileGraph.chercheNoeud("tree").getNext();
        //tant qu'on arrive pas à la fin.
        while( !Pattern.compile("end",66).matcher(curr.getName()).matches()){
            //on se deplace jusqu'a la description après [&r]
            if( curr.getNext().getName().equals(";")){
                //enfin on traite la chaine.
                erreurs += testStructure(curr.getName(), curr.getLine());
            }
            curr = curr.getNext();
        }
        return erreurs;
    }

    /**
    *Test la structure.
    *@param String, la description de l'arbre.
    *@param int, numero de ligne
    *@return String, les erreurs de description.
    **/
    static String testStructure(String desc, int line){
        String erreurs = "";
        Stack<Boolean> arbre = new Stack<Boolean>();
        char[] chars = desc.toCharArray();
        //racine
        arbre.push(false);
        //boolean qui decide quand en rentre à l'interieur de l'arbre et quand on sort.
        boolean in = false;
        for(char c : chars){
            if(c == '('){//On ouvre une generation de feuille,
                //L'int represente la feuille la plus a droite de cette generation. false = vide
                //On ne peut ouvrir une generation de feuille que si la feuille ou on est est vide.
                if(arbre.size()==1){in = true;}
                if (!arbre.pop()){
                    arbre.push(true);
                    arbre.push(false);
                }else{
                    erreurs += "Line "+line+" : a leaf must be empty to get a son.\n";
                    arbre.push(true);
                    arbre.push(false);
                }
            }else if(c==')'){//on ferme une generation de feuille,
                //si la feuille a droite est vide (false) il y a une erreur.
                if(!arbre.pop()){
                    erreurs += "line "+line+" : empty leaf.\n";
                }
                if(arbre.size()==1){in = false;}
            }else if(c==','){//on change de feuille,
                //si la precedente est vide, il y a une erreur.
                if(!in){erreurs += "line "+line+" : gone out of the tree.\n";}
                if(!arbre.pop()){
                    erreurs += "line "+line+" : empty leaf.\n";
                    arbre.push(false);
                }else{
                    arbre.push(false);
                }
            }else{//Si notre automate a bien fonctionné, le reste sont des noms de variable.
                    arbre.pop();
                    arbre.push(true);
            }
            if(arbre.isEmpty()){
                erreurs += "line "+line+" : excessive right brace.\n";
                arbre.push(false);
            }
        }
        //la parenthese racine en moins.
        arbre.pop();
        //On compte les parentheses gauches en trop.
        while(!arbre.isEmpty()){
            erreurs += "line "+ line + " : excessive left brace.\n";
        }
        return erreurs;
    }
}

