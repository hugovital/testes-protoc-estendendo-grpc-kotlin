import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import io.grpc.kotlin.generator.GeneratorRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import java.io.File;

public class MyProtocPlugin {

    public static void main(String[] args) throws IOException, Descriptors.DescriptorValidationException {

        try {

            String s = "CAWABANGA IT IS!!!!";

            File f = new File("c:/roots/sysout.txt");
            if (f.exists())
                f.delete();

            FileWriter writer = new FileWriter("c:/roots/sysout.txt");
            writer.write( new Date().toString() );
            writer.write(" - ");
            writer.write(s);
            writer.close();

        } catch (Exception ex){
            ex.printStackTrace();
        }

        GeneratorRunnerItau generatorItau =  new GeneratorRunnerItau();
        generatorItau.main(args);

        //main_02(args);

    }

    public static void main_02(String[] args) throws IOException, Descriptors.DescriptorValidationException {

/*        String s = "CAWABANGA IT IS!!!!";
        FileWriter writer = new FileWriter("c:/roots/sysout.txt");
        writer.write(s);
        writer.close();*/

/*        System.out.println("CAWABANGA IT IS!!!!");
        if (args == null || args.length == 0) {
            System.out.println("Nenhum arquivo informado");
            return;
        }*/
        GeneratorRunner.main( args );
/*        System.out.println("Done.");*/

    }

    public static void main_01(String[] args) throws IOException, Descriptors.DescriptorValidationException {
        // Plugin receives a serialized CodeGeneratorRequest via stdin
        CodeGeneratorRequest request = CodeGeneratorRequest.parseFrom(System.in);

        // CodeGeneratorRequest contain FileDescriptorProtos for all the proto files we need to process
        // as well as their dependencies.  We want to convert the FileDescriptorProtos into FileDescriptor instances,
        // since they are easier to work with. We will build a map that maps file names to the corresponding file
        // descriptor.
        Map<String, Descriptors.FileDescriptor> filesByName = new HashMap<>();

        for (DescriptorProtos.FileDescriptorProto fp: request.getProtoFileList()) {
            // The dependencies of fp are provided as strings, we look them up in the map as we are generating it.
            Descriptors.FileDescriptor dependencies[] =
                    fp.getDependencyList().stream().map(filesByName::get).toArray(Descriptors.FileDescriptor[]::new);

            Descriptors.FileDescriptor fd  = Descriptors.FileDescriptor.buildFrom(fp, dependencies);

            filesByName.put(
                    fp.getName(),
                    fd
            );
        }

        // Building the response
        CodeGeneratorResponse.Builder response = CodeGeneratorResponse.newBuilder();

        for (String fileName : request.getFileToGenerateList()) {
            Descriptors.FileDescriptor fd = filesByName.get(fileName);
            response.addFileBuilder()
                    .setName(fd.getFullName().replaceAll("\\.proto$", ".txt"))
                    .setContent(generateFileContent(fd));
        }

        // Serialize the response to stdout
        response.build().writeTo(System.out);
    }

    private static String generateFileContent(Descriptors.FileDescriptor fd) {
        StringBuilder sb = new StringBuilder();
        for (Descriptors.Descriptor messageType : fd.getMessageTypes()) {
            generateMessage(sb, messageType, 0);
        }
        return sb.toString();
    }

    private static String renderType(Descriptors.FieldDescriptor fd) {
        if (fd.isRepeated()) {
            return "List<" + renderSingleType(fd) + ">";
        } else {
            return renderSingleType(fd);
        }
    }

    private static String renderSingleType(Descriptors.FieldDescriptor fd) {
        if (fd.getType() != Descriptors.FieldDescriptor.Type.MESSAGE) {
            return fd.getType().toString();
        } else {
            return fd.getMessageType().getName();
        }
    }

    private static void generateMessage(StringBuilder sb, Descriptors.Descriptor messageType, int indent) {
        sb.append(String.join("", Collections.nCopies(indent, " ")));
        sb.append("|- ");
        sb.append(messageType.getName());
        sb.append("(");

        sb.append(
            String.join(
                ", ", 
                messageType
                    .getFields()
                    .stream()
                    .map(field -> field.getName() + ": " + renderType(field))
                    .collect(Collectors.joining(", "))
            )
        );
        sb.append(")");
        sb.append(System.getProperty("line.separator"));

        // recurse for nested messages.
        sb.append(String.join("", Collections.nCopies(indent, " ")));
        for (Descriptors.Descriptor nestedType : messageType.getNestedTypes()) {
            generateMessage(sb, nestedType, indent + 3);
        }
    }
}
