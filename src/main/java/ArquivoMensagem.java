import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArquivoMensagem {

    private String path;
    private String packageName;
    private String className;

    private List<String> linhas;

    private Map<String, String> tipos = new HashMap<String, String>();
    private Map<String, String> defaulValues = new HashMap<String, String>();

    private boolean hasRepeatedFields = false;

    public ArquivoMensagem(String path) {

        this.path = path;

        this.linhas = new ArrayList();

        this.tipos.put("STRING", "String");
        this.tipos.put("BOOL", "Boolean");
        this.tipos.put("INT32", "Int");

        this.defaulValues.put("STRING", "\"\"");
        this.defaulValues.put("BOOL", "false");
        this.defaulValues.put("INT32", "0");

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

    public void addClass(String className){
        String s = String.format("class %s {", className);
        this.linhas.add(s);
        this.className = className;
    }

    public void addComment(String comentario){
        this.linhas.add("//" + comentario);
        this.linhas.add("");
    }

    public void closeClass(){
        this.linhas.add("}");
    }

    public void addField(String fieldName, String fieldType){

        String tipo = tipos.get(fieldType);
        String defaultValue = defaulValues.get(fieldType);

        if (tipo == null)
            tipo = fieldType + "?";

        String s = String.format("\tvar %s: %s = %s", fieldName, tipo, defaultValue);
        this.linhas.add(s);

    }

    public void addRepeatedField(String fieldName, String fieldType){

        this.hasRepeatedFields = true;

        String tipo = tipos.get(fieldType);
        String defaultValue = defaulValues.get(fieldType);

        if (tipo == null)
            tipo = fieldType + "?";

        String s = String.format("\tvar %s: List<%s> = ArrayList<%s>()", fieldName, tipo, tipo);

        this.linhas.add(s);

    }

    public void gerar(){

        List<String> toGenerate = new ArrayList<String>();

        if (!this.packageName.trim().equals("")) {
            toGenerate.add("package " + this.packageName);
            toGenerate.add("");
        }

        if ( this.hasRepeatedFields ){
            toGenerate.add("import java.util.*");
            toGenerate.add("");
        }

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
