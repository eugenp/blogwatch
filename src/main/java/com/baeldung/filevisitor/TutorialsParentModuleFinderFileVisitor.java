package com.baeldung.filevisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TutorialsParentModuleFinderFileVisitor extends SimpleFileVisitor<Path> {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private final String artificateId;

    public TutorialsParentModuleFinderFileVisitor(String artifactId) {
        this.artificateId = artifactId;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        Function<Path, String> getParentArtifactId = filePath -> {
            try {
                return Optional.of(Jsoup.parse(filePath.toFile(), "UTF-8"))
                        .map(doc -> doc.getElementsByTag("parent"))
                        .map(elements -> elements.first())
                        .map(parentTag -> parentTag.getElementsByTag("artifactId"))
                        .map(parentTags -> parentTags.first())
                        .map(artififactIdTag -> artififactIdTag.text())
                        .orElse("not-found");
            } catch (IOException e1) {
                System.out.println("Exception.............");
                return "not-found";
            }
        };
        File file = path.toFile();
        if (file.isFile() && file.getName()
                .equalsIgnoreCase("pom.xml")) {
            logger.info("inspection {}", path);
            String parentArtificatId = getParentArtifactId.apply(path);
            System.out.println("Prenet tag:" + parentArtificatId);

        }
        return FileVisitResult.CONTINUE;
    }

}
