
import java.util.ArrayList;

import javax.swing.JOptionPane;


public class ChomskyAnalizer {

    public static String input;
    public static int index;
    public static TreeNode initialNode;
    public static void main(String[] args) {
        Reader reader = new Reader("arithmeticCFG.txt");
        Chomsky choms = new Chomsky(reader.prods);
        String toVerify = JOptionPane.showInputDialog("Escribe la cadena a verificar."); 
        if (CYK(choms, toVerify)) {
            JOptionPane.showMessageDialog(null, "La cadena fue aceptada!\nEl arbol de derivacion se muestra en consola.", "Aceptada", JOptionPane.INFORMATION_MESSAGE);
            System.out.println(initialNode.toString());
        }else{
            JOptionPane.showMessageDialog(null, "La cadena fue rechazada.", "Rechazada", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static boolean CYK(Chomsky glc, String input) {
        ChomskyAnalizer.input = input;
        index = 0;
        String[][] matrix = new String[input.length()][input.length()];
        // Initialize matrix
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = "";
            }
        }

        ArrayList<String> paths = new ArrayList<>();
        // Find diagonal
        for (int i = 0; i < matrix.length; i++) {
            String symbols = findProduction(glc.prods, input.charAt(i) + "");
            matrix[i][i] += symbols;
            String[] initials;
            if (symbols.length() > 0) {
                symbols.substring(0, symbols.length() - 1);
                initials = symbols.split(",");
                for (String s : initials) {
                    paths.add(s);
                }
            }
        }

        // printMatrix(matrix);
        // Fill matrix
        for (int i = 1; i < input.length(); i++) {
            for (int j = i; j < input.length(); j++) {
                String prod = "";
                for (int k = j - i; k < j; k++) {
                    String[] combined = combineSymbols(matrix[j - i][k], matrix[k + 1][j]);
                    for (String s : combined) {
                        prod += findProduction(glc.prods, s);
                        // System.out.println(prod + " find " + i +" " +j);
                    }
                }
                matrix[j - i][j] = prod;
                //printMatrix(matrix);
            }
        }
        //printMatrix(matrix);
        if (matrix[0][matrix.length - 1].contains("S")) {
            findPath(glc.prods, matrix);
        }
        return matrix[0][matrix.length - 1].contains("S");
    }

    public static String[] combineSymbols(String a, String b) {
        // System.out.println("To combine:" + a + b);
        ArrayList<String> result = new ArrayList<>();
        if (a.length() > 0)
            a = a.substring(0, a.length() - 1);

        if (b.length() > 0)
            b = b.substring(0, b.length() - 1);

        String[] am = a.split(",");
        String[] bm = b.split(",");
        for (int i = 0; i < am.length; i++) {
            for (int j = 0; j < bm.length; j++) {
                // System.out.println("joined: "+am[i] + bm[j]);
                result.add(am[i] + bm[j]);
            }
        }

        if (result.size() == 0) {
            String[] toCopy;
            toCopy = (am.length != 0) ? am : bm;
            for (int i = 0; i < toCopy.length - 1; i++) {
                result.add(toCopy[i]);
            }
        }

        String[] res = new String[result.size()];
        for (int i = 0; i < res.length; i++)
            res[i] = result.get(i);

        return res;
    }

    private static String findProduction(ArrayList<Production> prods, String toFind) {
        String result = "";
        for (Production p : prods) {
            if (p.prods.contains(toFind)) {
                result += p.key + ",";
                // System.out.println(rank+""+p.key + "->"+toFind+";");
                //graph += p.key + "->" + toFind + ";";
            }
        }
        return result;
    }

    private static void findPath(ArrayList<Production> prods, String[][] matrix) {
        initialNode = new TreeNode("S");
        findSymbol(prods, matrix, "S", 0, matrix.length-1, initialNode);
    }

    private static String findSymbol(ArrayList<Production> prods, String[][] matrix, String toFind, int is, int js, TreeNode node){

        Production currentProd = null;
        for(Production p: prods){
            if(toFind.equals(p.key+"")){
                currentProd = p;
                break;
            }
        }

        if(is==js){
            ArrayList<TreeNode> toAdd = new ArrayList<>();
            toAdd.add(new TreeNode(input.charAt(index)+""));
            node.children = toAdd;
            //node.left = new Node(input.charAt(index)+"");
            String toReturn = currentProd.key+"->"+input.charAt(index)+";";
            index++;
            return toReturn;
        }
        String tmp1 = "";
        String tmp2 = "";
        for(int i = is; i<matrix.length-1;i++){
            if(!matrix[is][i].equals("") && !matrix[i+1][js].equals("")){
                tmp1 = matrix[is][i].substring(0, matrix[is][i].length()-1);
                tmp2 = matrix[i+1][js].substring(0, matrix[i+1][js].length()-1);
                String[] splitted1 = tmp1.split(",");
                String[] splitted2 = tmp2.split(",");

                //System.out.println(matrix[is][i] + " " + matrix[i+1][js]);
                //Check first possible order
                for(String s1 : splitted1){
                    for(String s2 : splitted2){
                        for(String sProd : currentProd.prods){
                            if(sProd.equals(s1+s2)){
                                TreeNode n1 = new TreeNode(s1);
                                TreeNode n2 = new TreeNode(s2);
                                ArrayList<TreeNode> toAdd = new ArrayList<>();
                                toAdd.add(n1);
                                toAdd.add(n2);
                                node.children = toAdd;
                                //node.left = n1;
                                //node.right = n2;
                                String add1 = findSymbol(prods, matrix, s1, is, i,n1);
                                String add2 = findSymbol(prods, matrix, s2, i+1, js, n2);
                                return currentProd.key+"->{"+s1+","+s2+"};"+add1+add2;
                            }else if(sProd.equals(s2+s1)){
                                TreeNode n1 = new TreeNode(s1);
                                TreeNode n2 = new TreeNode(s2);
                                ArrayList<TreeNode> toAdd = new ArrayList<>();
                                toAdd.add(n2);
                                toAdd.add(n1);
                                node.children = toAdd;
                                //node.left = n2;
                                //node.right = n1;
                                String add2 = findSymbol(prods, matrix, s2, i+1, js, n2);
                                String add1 = findSymbol(prods, matrix, s1, is, i, n1);
                                return currentProd.key+"->{"+s2+","+s1+"};"+add2+add1;
                            }
                        }
                    }
                }
            }
        }
        return "";
        
    }

    public static <T> void printMatrix(T[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " | ");
            }
            System.out.println();
        }
        System.out.println();
    }

}
