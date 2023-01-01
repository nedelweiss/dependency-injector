package dependency_injector;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageScanner {
    private final Pattern PATTERN = Pattern.compile((".+?(?=.java)"));
    private final String PACKAGE_SEPARATOR = ".";
    private final Logger logger = Logger.getLogger(PackageScanner.class.getName());

    public List<Class<?>> classScanner(File directory, String packageName) {
        File[] files = directory.listFiles();

        List<Class<?>> classes = new ArrayList<>();
        if (!Files.exists(Paths.get(directory.getPath())) || files == null) {
            return classes;
        }

        for (File file : files) {
            String parentPackage = packageName + PACKAGE_SEPARATOR;

            if (file.isDirectory()) {
                classes.addAll(classScanner(file, parentPackage + file.getName()));
            } else {
                try {
                    Optional<String> fileName = fileNameProcessing(file.getName());
                    if (fileName.isEmpty()) continue;

                    classes.add(Class.forName(parentPackage + fileName.get()));
                } catch (ClassNotFoundException e) {
                    logger.info(e.getException().getClass().toString());
                }
            }
        }
        return classes;
    }

    private Optional<String> fileNameProcessing(String fileName) {
        Matcher matcher = PATTERN.matcher(fileName);
        if (matcher.find()) {
            return Optional.of(matcher.group());
        }
        return Optional.empty();
    }
}
