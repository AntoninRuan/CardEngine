package fr.antoninruan.mao.utils.update4j;

import org.update4j.Configuration;
import org.update4j.FileMetadata;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigurationCreator {

    public static void main(String[] args) {

        Configuration configuration = Configuration.builder()
                .baseUri("https://github.com/AntoninRuan/CardEngine/releases/latest/download")
                .basePath("${user.dir}")

                .file(FileMetadata
                        .readFrom(new File("").getAbsolutePath() + "/out/artifacts/Mao_jar/Mao.jar")
                        .path("Mao.jar")
                        .classpath())
                .build();

        try (Writer out = Files.newBufferedWriter(Paths.get("out/artifacts/Mao_jar/config.xml"))) {
            configuration.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Writer out = Files.newBufferedWriter(Paths.get("config.xml"))) {
            configuration.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
