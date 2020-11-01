import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class Chomsky {

    public Set<Character> symbols;
    public ArrayList<Production> prods;
    //private Production initial;

    public Chomsky(ArrayList<Production> prods) {
        this.prods = prods;
        symbols = new HashSet<>();
        for (Production p : this.prods) {
            symbols.add(p.key);
            //if (p.key == 'S') 
                //initial = p;

        }
        //print();
        // checkInitial();
        deleteEpsilon();
        //print();
        deleteUnits();
        //print();
        deleteThree();
        //print();
        deleteSymbolsTerminals();
        //print();

    }

    private void deleteEpsilon() {
        boolean change = false;
        Set<Production> toChange = new HashSet<>();
        for (Production p : prods) {
            for (String s : p.prods) {
                if (s.equals("")) {
                    change = true;
                    toChange.add(p);
                }
            }
        }

        for (Production p : toChange) {
            deleteEpsilon(p);
        }

        if (change) {
            deleteEpsilon();
        }

    }

    private void deleteEpsilon(Production toChange) {
        char name = toChange.key;
        for (Production p : prods) {
            Set<String> currentProds = new HashSet<>(p.prods);
            for (String s : currentProds) {
                if (s.contains(name + "")) {
                    boolean same = true;
                    for (int j = 0; j < s.length(); j++) {
                        if (s.charAt(j) != name) {
                            same = false;
                            break;
                        }
                    }

                    if (!same) {
                        for (int j = 0; j < s.length(); j++) { // Check how many ocurrences of the symbol are
                            if (s.charAt(j) == name) {
                                StringBuilder newProd = new StringBuilder(s);
                                newProd.deleteCharAt(j);
                                p.prods.add(newProd.toString());
                            }
                        }
                    }
                    String newProd = s.replace(name + "", "");
                    p.prods.add(newProd);
                }
            }
        }

        ArrayList<String> copy = new ArrayList<String>();
        for (String s : toChange.prods) {
            copy.add(s);
        }

        // Delete Epsilon transition
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i).equals("")) {
                toChange.prods.remove(copy.get(i));
            }
        }

    }

    private void deleteUnits() {
        for (Production p : prods) {
            Set<String> currentProds = new HashSet<>(p.prods);
            for (String s : currentProds) {
                if (s.length() == 1) { // Check if production is only one symbol
                    if (symbols.contains(s.charAt(0))) { // Check if production is a nonterminal symbol
                        Production toCopy = getProdByKey(s.charAt(0));
                        Set<String> newProds = new HashSet<>(toCopy.prods);
                        newProds.remove(s);
                        p.prods.remove(s);
                        p.prods.addAll(newProds);
                    }
                }
            }
        }
    }

    private void deleteThree() {
        ArrayList<Production> copyProds = new ArrayList<>();
        for (Production p : prods) {
            copyProds.add(p);
        }
        for (Production p : copyProds) {
            Set<String> currentProds = new HashSet<>(p.prods);
            for (String s : currentProds) {
                if (s.length() > 2) {
                    int cont = 0;
                    int totalCont = 0;
                    StringBuilder toJoin = new StringBuilder("");
                    for (int i = 0; i < s.length(); i++) {
                        totalCont++;
                        cont++;
                        toJoin.append(s.charAt(i));
                        if (cont > 1) {
                            //System.out.println(toJoin.toString()+ " union");
                            char newSymbol = createProduction(toJoin.toString());
                            String newProd = s.substring(totalCont, s.length());
                            newProd = newSymbol+newProd;
                            //System.out.println(newProd + " newProd, falta lo otro");
                            p.prods.remove(s);
                            p.prods.add(newProd);
                            cont = 0;
                            toJoin = new StringBuilder("");
                        }
                    }
                }
            }
        }
    }

    private void deleteSymbolsTerminals() {
        boolean repeat = false;

        ArrayList<Production> copyProds = new ArrayList<>();
        for (Production p : prods) {
            copyProds.add(p);
        }
        for (Production p : copyProds) {
            Set<String> currentProds = new HashSet<>(p.prods);
            for (String s : currentProds) {
                if (s.length() > 1) {
                    for (int i = 0; i < s.length(); i++) {
                        if (!symbols.contains(s.charAt(i))) {
                            StringBuilder newProd = new StringBuilder(s);
                            char newSymbol = createProduction(s.charAt(i) + "");
                            newProd.setCharAt(i, newSymbol);
                            p.prods.remove(s);
                            p.prods.add(newProd.toString());
                            repeat = true;
                        }
                    }
                }
            }
        }

        if(repeat)
            deleteSymbolsTerminals();
    }

    private Character createProduction(String prod) {
        for (Production p : prods) {
            if (p.prods.size() == 1) {
                for (String s : p.prods) {
                    if (s.equals(prod)) {
                        return p.key;
                    }
                }
            }
        }
        char symbolName = 'A';
        while (symbols.contains(symbolName)) {
            symbolName++;
        }
        String[] tmp = new String[] { prod };
        Production newProd = new Production(symbolName, tmp);
        prods.add(newProd);
        symbols.add(symbolName);

        return newProd.key;
    }

    private void print() {
        System.out.println();
        for (Production p : prods) {
            System.out.print(p.key + "-");
            for (String s : p.prods) {
                System.out.print(s + "|");
            }
            System.out.println();
        }
    }

    private Production getProdByKey(char key) {
        for (Production p : prods) {
            if (p.key == key) {
                return p;
            }
        }
        return null;
    }

}