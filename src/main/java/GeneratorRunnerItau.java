import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.UnknownFieldSet;
import com.squareup.kotlinpoet.FileSpec;
import io.grpc.MethodDescriptor;
import io.grpc.kotlin.generator.protoc.AbstractGeneratorRunner;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class GeneratorRunnerItau extends AbstractGeneratorRunner {

    public void main(String[] args){
        this.doMain(args);
    }

    private static StringBuilder sb;
    private static void ger(String msg){
        sb.append(msg).append("\r\n");
    }

    @NotNull
    @Override
    public List<FileSpec> generateCodeForFile(@NotNull Descriptors.FileDescriptor fileDescriptor) {

        String path = "C:\\Users\\Hugo\\Documents\\PROJS\\creating-your-first-micronaut-app-kotlin-master\\complete\\src\\main\\kotlin\\";

        for (Descriptors.Descriptor descriptor : fileDescriptor.getMessageTypes()) {

            ArquivoMensagem arquivoMensagem = new ArquivoMensagem(path);

            arquivoMensagem.addPackage(fileDescriptor.getPackage());
            arquivoMensagem.addClass(descriptor.getName());

            for (Descriptors.FieldDescriptor field : descriptor.getFields()) {

                String tipo = "";
                if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE){
                    tipo = field.getMessageType().getName();
                } else {
                    tipo = field.getType().toString();
                }

                if (field.isRepeated()){
                    arquivoMensagem.addRepeatedField(field.getName(), tipo);
                } else {
                    arquivoMensagem.addField(field.getName(), tipo);
                }

            }

            arquivoMensagem.closeClass();
            arquivoMensagem.gerar();

        }

        for(Descriptors.ServiceDescriptor descriptor :  fileDescriptor.getServices()){

            ArquivoServico arquivoServico = new ArquivoServico(path);
            arquivoServico.addPackage(fileDescriptor.getPackage());
            arquivoServico.addController();
            arquivoServico.addClass(descriptor.getName() + "Controller");

            for(Descriptors.MethodDescriptor methodDescriptor : descriptor.getMethods()){

                //TODO: Descobrir uma forma melhor de fazer o parser/discovery abaixo
                HttpOptionParser optionParser = new HttpOptionParser( methodDescriptor.getOptions().toString() );

                arquivoServico.addLine();

                if (optionParser.hasOptions()){
                    arquivoServico.addMethodAnotation(optionParser.getHttpVerb(), optionParser.getUrlPattern());
                } else {
                    arquivoServico.addMethodAnotation("POST", "");
                }

                arquivoServico.addMediaJson();

                arquivoServico.addFunction(
                        methodDescriptor.getName(),
                        methodDescriptor.getInputType().getName(),
                        methodDescriptor.getOutputType().getName() );

                arquivoServico.addNotImplemented();
                arquivoServico.closeFunction();

                arquivoServico.add("/*");
                arquivoServico.add("'" + methodDescriptor.getOptions().toString() + "'");
                arquivoServico.add("*/");

/*                arquivoServico.addComment( methodDescriptor.getFullName() );
                arquivoServico.addComment( methodDescriptor.getName() );
                arquivoServico.addComment( methodDescriptor.getInputType().getName() );
                arquivoServico.addComment( methodDescriptor.getOutputType().getName() );*/

/*              arquivoServico.addComment("HttpVerb:" + optionParser.getHttpVerb());
                arquivoServico.addComment("HttpUrlPattern:" + optionParser.getUrlPattern());*/

            }

            arquivoServico.closeClass();
            arquivoServico.gerar();

        }

        return new ArrayList<FileSpec>();
    }

    @NotNull
    public List<FileSpec> generateCodeForFile01(@NotNull Descriptors.FileDescriptor fileDescriptor) {

        for(Descriptors.ServiceDescriptor service : fileDescriptor.getServices()) {

            String name = service.getName();

            try (FileWriter writer = new FileWriter("c:\\roots\\files\\" + name + "Gerado.txt")) {

                sb = new StringBuilder();

                ger("Name: " + fileDescriptor.getName());
                ger("File: " + fileDescriptor.getFile());
                ger("FullName: " + fileDescriptor.getFullName());
                ger("Package: " + fileDescriptor.getPackage());

                ger("Service(fullName): " + service.getFullName());
                ger("Service(name): " + service.getName());
                ger("");

                for (Descriptors.Descriptor descriptor : fileDescriptor.getMessageTypes()) {

                    ger("Descriptor: " + descriptor.getName());
                    descriptor.getName();
                    for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
                        ger("Field-Name: " + field.getName());
                        ger("Field-Type: " + field.getType());
                    }

                }

                writer.write(sb.toString());

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        return new ArrayList<FileSpec>();
    }

}
