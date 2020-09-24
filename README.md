# testes-protoc-estendendo-grpc-kotlin
testes-protoc-estendendo-grpc-kotlin

1) Exemplo totalmente baseado no projeto simples e muito útil: https://github.com/thesamet/protoc-plugin-in-java
2) Como coloquei no projeto acima todas as dependências de https://github.com/grpc/grpc-kotlin, precisei alterar o modelo para gerar um ubber-jar:
	docs do ubber-jar: https://docs.gradle.org/current/userguide/working_with_files.html#sec:creating_uber_jar_example
	gradle task:
		task customFatJar(type: Jar) {
			manifest {
				attributes 'Main-Class': 'kotlin.main.TesteGereratorRunner'
			}
			//baseName = 'all-in-one-jar'
			from { configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } }
			with jar
		}
3) Clonar o repositório do grpc-kotlin: https://github.com/grpc/grpc-kotlin
4) Compilá-lo no linux (em windows dá um erro);
5) Pegar o 'jar' gerado em grpc-kotlin-master/compiler/libs/grpc-kotlin-compiler-0.2.0.jar;
6) Como eu gerei o jar local, no projeto exemplo em (1), inclui como referência ao disco:
	implementation files('C:\\Users\\Hugo\\Documents\\PROJS\\grpc-kotlin-master\\compiler\\build\\libs\\grpc-kotlin-compiler-0.2.0.jar')
7) Fiz uma nova classe Main(MyProtocPlugin), que simplesmente chama a classe main do grpc-kotlin-compiler. 
	Coloquei algumas marcas (FileWriter) para saber que está sendo chamada;
	Se usar Systeout, dá problema com o mecanismo de geração do protoc;
8) Compilei tudo, fiz um installDist. O importante é gerar os .bat (é algo que o compilador protoc usa);
9) Encontrei o protoc na minha máquina (executável para windows); Separei numa pasta, montei os caminhos no PATH no bat myplugin;
10) Executei o comando:
	protoc-3.12.2-windows-x86_64.exe --plugin=protoc-gen-example=scripts\myplugin.bat protos\a.proto protos\b.proto protos\hello_world.proto -I protos  --example_out=protos
11) Gerou os arquivos do kotlin esperados que o grpc-kotlin normalmente geraria; Gerou minhas marcações (FileWriter); 
		Assim, concluímos que deu certo fazer um 'wrapper' para o grpc-kotlin;
	