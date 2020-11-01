import java.util.HashSet;
import java.util.Set;

public class Production {

    public Character key;
    public Set<String> prods;
    
    public Production(char key, String[] prods){
        this.key = key;
        this.prods = new HashSet<>();
        for(String s :prods){
            if(s.equals("ϵ")){
                this.prods.add("");
            }else{
                this.prods.add(s);
            }
            
        }
    }
}