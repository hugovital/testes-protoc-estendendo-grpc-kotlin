import java.util.HashMap;
import java.util.Map;

//Classe para fazer parser dos dados do options http.proto
public class HttpOptionParser {

    private Map<Integer, String> conteudo = new HashMap<Integer, String>();

    private boolean options = false;

    public boolean hasOptions(){
        return this.options;
    }

    public HttpOptionParser (String option){

        if (option.trim().equals("")) {
            this.options = false;
            return;
        }

        this.options = true;

        String[] linhas = option.split("\n");
        for(String l : linhas){
            if (l.contains(":")){
                String[] partes = l.split(":");
                conteudo.put(Integer.parseInt(partes[0].trim()), partes[1].trim());
            }
        }

    }

    public String getHttpVerb(){
        /*
        Documentacao de http.proto
        string get = 2;
        string put = 3;
        string post = 4;
        string delete = 5;
        string patch = 6;
        CustomHttpPattern custom = 8;
         */

        if (conteudo.containsKey(2))
            return "GET";
        else if (conteudo.containsKey(4))
            return "POST";

        return "NotImplemented - See HttpOptionParser.getHttpVerb";

    }

    public String getUrlPattern(){

        int[] possiveis = new int[] { 2, 4 };

        for (int p : possiveis){
            if (conteudo.containsKey(p))
                return this.conteudo.get(p);
        }

        return "NotImplemented - See HttpOptionParser.getUrlPattern";

    }


}
