import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArquivoServico {

    private String path;
    private String packageName;
    private String className;

    private List<String> linhas;

    private Map<String, String> anotations = new HashMap<String, String>();

    public ArquivoServico(String path) {

        this.path = path;

        this.linhas = new ArrayList();

        anotations.put("GET", "Get");
        anotations.put("POST", "Post");

    }

    public void add(String linha){
        this.linhas.add(linha);
    }

    public void addPackage(String packageName){

        if (!packageName.trim().equals("")) {
            this.packageName = packageName;
        } else {
            this.packageName = "";
        }

    }

    public void addController(){
        this.linhas.add("@Controller()");
    }

    public void addClass(String className){
        String s = String.format("class %s {", className);
        this.linhas.add(s);
        this.className = className;
    }

    public void addFunction(String funcName, String funcInput, String funcType){
        String inputParameterName = funcInput.substring(0,1).toLowerCase() + funcInput.substring(1);
        String s = String.format("\tfun %s(%s: %s): %s {", funcName, inputParameterName, funcInput, funcType);
        this.linhas.add(s);
    }

    public void addNotImplemented(){
        this.linhas.add("\t\tthrow NotImplementedError()");
    }

    public void addMediaJson(){
        this.linhas.add("\t@Produces(MediaType.TEXT_PLAIN)");
    }

    public void closeFunction(){
        this.linhas.add("\t}");
    }

    public void addComment(String comentario){
        this.linhas.add("//" + comentario);
    }

    public void addLine(){
        this.linhas.add("");
    }

    public void addMethodAnotation(String anotation, String pattern){
        String s = "\t@" + anotations.get(anotation);
        if ( pattern != null && !pattern.trim().equals("") ){
            s += "(" + pattern + ")";
        }
        this.linhas.add(s);
    }

    public void closeClass(){
        this.linhas.add("}");
    }

    public void gerar(){

        List<String> toGenerate = new ArrayList<String>();

        if (!this.packageName.trim().equals("")) {
            toGenerate.add("package " + this.packageName);
            toGenerate.add("");
        }

        toGenerate.add("import io.micronaut.http.annotation.*");
        toGenerate.add("import io.micronaut.http.MediaType");

        toGenerate.add("");

        toGenerate.addAll(this.linhas);

        StringBuilder sb = new StringBuilder();
        for(String l : toGenerate){
            sb.append( l );
            sb.append("\r\n");
        }

        try {

            String finalDir = this.path + this.packageName.replace('.', '\\');
            File fdirs = new File( finalDir );
            fdirs.mkdirs();

            String finalFile = finalDir + "\\" + this.className + ".kt";

            FileWriter writer = new FileWriter(finalFile);
            writer.write(sb.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
