import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import java.util.ArrayList; 

@SuppressWarnings("unused")
public class Reader {

    public ArrayList<Production> prods;

    public Reader(String fileName){
        prods = new ArrayList<>();
        try{
            File file = new File(fileName);
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()){
                String[] line = scan.nextLine().split(">");
                Production prod = new Production(line[0].charAt(0), line[1].split("\\|"));
                prods.add(prod);
            }
            scan.close();
            //Chomsky choms = new Chomsky(prods);
            //choms.notify();

        }catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "El nombre de archivo especificado no es valido", "Archivo no encontrado", JOptionPane.ERROR_MESSAGE);
        }

    }

}
