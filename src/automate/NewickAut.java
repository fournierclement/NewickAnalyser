package automate;
import java.util.*;
import java.util.regex.*;
import java.io.*;
import graphe.*;

public class NewickAut extends Auto{

    /**
    *Construit un graphe correspondant à l'automate du format newick. Les noeuds sont les transitions.
    *@return NewickAut.
    **/
    public NewickAut(){
        State[] states = new State[47];
        for(int i = 0 ; i < states.length ; i++){states[i] = new State();}
        this.setInit(states[0]);
        states[0].addTrans(states[1],Pattern.compile("#nexus",66));
        states[1].addTrans(states[2],Pattern.compile("begin",66));
        states[2].addTrans(states[3],Pattern.compile("taxa",66));
        states[3].addTrans(states[4],Pattern.compile(";"));
        states[4].addTrans(states[5],Pattern.compile("dimensions",66));
        states[5].addTrans(states[6],Pattern.compile("ntax",66));
        states[6].addTrans(states[7],Pattern.compile("="));
        states[7].addTrans(states[8],Pattern.compile("[0-9]+"));
        states[8].addTrans(states[9],Pattern.compile(";"));
        states[9].addTrans(states[10],Pattern.compile("taxlabels",66));
        states[10].addTrans(states[11],Pattern.compile("[a-zA-Z0-9_.]+"));
        states[11].addTrans(states[11],Pattern.compile("[a-zA-Z0-9_.]+"));
        states[11].addTrans(states[12],Pattern.compile(";"));
        states[12].addTrans(states[13],Pattern.compile("end",66));
        states[13].addTrans(states[14],Pattern.compile(";"));

        states[14].addTrans(states[15],Pattern.compile("begin",66));
        states[15].addTrans(states[16],Pattern.compile("trees",66));
        states[16].addTrans(states[17],Pattern.compile(";"));
        //Selon le fichier teste "lgr.nex" ceci est facultatif...
            //patched
            State state175 = new State();
            states[17].addTrans(state175,Pattern.compile("properties",66));
            state175.addTrans(states[18],Pattern.compile("partialtrees",66));
            //\patched
        states[18].addTrans(states[19],Pattern.compile("="));
        states[19].addTrans(states[20],Pattern.compile("yes",66));
        states[20].addTrans(states[21],Pattern.compile(";"));
        //\fin factultatif, contournement :
        states[17].addTrans(states[22],Pattern.compile("tree",66));
        //
        states[21].addTrans(states[22],Pattern.compile("tree",66));
        states[22].addTrans(states[23],Pattern.compile("[a-zA-Z0-9_]+"));
        //selon le fichier teste "ExPartial.nex" = et [&R] seraient interchangeable :
        states[23].addTrans(states[24],Pattern.compile("="));
        states[24].addTrans(states[25],Pattern.compile("\\[&R\\]",66));
        State state24bis = new State();
        states[23].addTrans(state24bis,Pattern.compile("\\[&R\\]",66));
        state24bis.addTrans(states[25],Pattern.compile("="));
        //
        states[25].addTrans(states[26],Pattern.compile("[a-zA-Z0-9_.:\\(\\)\\,]+"));
        states[26].addTrans(states[27],Pattern.compile(";"));
        states[27].addTrans(states[22],Pattern.compile("tree"));
        states[27].addTrans(states[28],Pattern.compile("end",66));
        states[28].addTrans(states[29],Pattern.compile(";"));
        states[29].endState();

        states[29].addTrans(states[30],Pattern.compile("begin",66));
        states[30].addTrans(states[31],Pattern.compile("st_assumptions",66));
        states[31].addTrans(states[32],Pattern.compile(";"));
        states[32].addTrans(states[33],Pattern.compile("treestransform",66));
        states[33].addTrans(states[35],Pattern.compile("="));
        states[35].addTrans(states[37],Pattern.compile("generalab",66));
        states[37].addTrans(states[40],Pattern.compile("GA",66));
        states[40].addTrans(states[42],Pattern.compile("="));
        states[42].addTrans(states[43],Pattern.compile("true",66));
        states[32].addTrans(states[34],Pattern.compile("GA",66));
        states[34].addTrans(states[36],Pattern.compile("="));
        states[36].addTrans(states[38],Pattern.compile("true",66));
        states[38].addTrans(states[39],Pattern.compile("treestransform",66));
        states[39].addTrans(states[41],Pattern.compile("="));
        states[41].addTrans(states[43],Pattern.compile("generalab",66));
        states[43].addTrans(states[44],Pattern.compile(";"));
        states[44].addTrans(states[45],Pattern.compile("end",66));
        states[45].addTrans(states[46],Pattern.compile(";"));
        states[46].endState();
    }
}