package org.wso2.carbon.mvndepgraph;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.installation.InstallationException;

import javax.xml.namespace.QName;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Hello world!
 */
public class Client {

    private static Logger log = Logger.getLogger(Client.class);

    private static RepositorySystem system;
    private static RepositorySystemSession session;

    private static CommandLineParser parser = new BasicParser();
    private static Options options = new Options();

    public static void main(String[] args) throws Exception {
        initOptions();

        CommandLine line = parser.parse(options, args);

        if (line.hasOption("help") || !line.hasOption("repo") || !(line.hasOption("file") || line.hasOption("id"))) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ant", options);
            return;
        }

        String repo = line.getOptionValue("repo");
        String path = line.getOptionValue("file");
        String artifactId = line.getOptionValue("id");
        String artifactVersion = line.getOptionValue("version");

        Map<String, Map<String, String>> features = new HashMap<String, Map<String, String>>();
        findFeatures(features, new File(repo));

        Map<String, String> results = new HashMap<String, String>();
        if (path != null) {
            Map<String, String> dependencies = findDependencies(new File(path));
            for (String id : dependencies.keySet()) {
                String version = dependencies.get(id);
                findDependencies(id, version, features, results);
                results.put(id, version);
            }
        } else {
            findDependencies(artifactId, artifactVersion, features, results);
        }

        for (String id : results.keySet()) {
            System.out.println(id + ":" + results.get(id));
        }
    }

    private static void findDependencies(String id,
                                         String version,
                                         Map<String, Map<String, String>> features,
                                         Map<String, String> results)
            throws IOException, InstallationException {
        Map<String, String> versions = features.get(id);
        if (versions == null) {
            System.out.println("cannot find " + id + ":" + version);
            return;
        }
        String artifact = versions.get(version);
        if (artifact == null) {
            System.out.println("cannot find " + id + ":" + version);
            return;
        }
        Map<String, String> dependencies = findDependencies(new File(artifact));
        for (String dependencyId : dependencies.keySet()) {
            String dependencyVersion = dependencies.get(dependencyId);
            findDependencies(dependencyId, dependencyVersion, features, results);
            results.put(dependencyId, dependencyVersion);
        }
    }

    private static Map<String, String> findDependencies(File file)
            throws IOException, InstallationException {
        Map<String, String> dependencies = new HashMap<String, String>();
        ZipFile zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.matches("features/.*/feature.xml")) {
                InputStream stream = zipFile.getInputStream(entry);
                OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(stream);
                OMElement artifact = builder.getDocumentElement();
                OMElement requires = artifact.getFirstChildWithName(new QName(null, "require"));
                if (requires == null) {
                    continue;
                }
                Iterator imports = requires.getChildrenWithName(new QName(null, "import"));
                while (imports.hasNext()) {
                    OMElement imprt = (OMElement) imports.next();
                    OMAttribute feature = imprt.getAttribute(new QName(null, "feature"));
                    if (feature == null) {
                        continue;
                    }
                    OMAttribute version = imprt.getAttribute(new QName(null, "version"));
                    dependencies.put(feature.getAttributeValue(), version.getAttributeValue());
                }
            }
        }
        return dependencies;
    }

    private static void initOptions() {
        options.addOption("help", false, "prints this message");
        options.addOption("repo", true, "m2 repository location e.g. /home/ruchira/.m2/repository");
        options.addOption("file", true, "file to analyse for dependencies e.g. /home/ruchira/foo.zip");
        options.addOption("id", true, "file to analyse for dependencies e.g. /home/ruchira/foo.zip");
        options.addOption("version", true, "file to analyse for dependencies e.g. /home/ruchira/foo.zip");
    }

    private static void findFeatures(Map<String, Map<String, String>> features, File file)
            throws IOException, InstallationException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    findFeatures(features, f);
                }
            }
            return;
        }
        if (!file.getName().endsWith(".zip")) {
            return;
        }
        findFeature(features, file);
    }

    private static void findFeature(Map<String, Map<String, String>> features, File file)
            throws IOException, InstallationException {
        ZipFile zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.matches("features/.*/feature.xml")) {
                InputStream stream = zipFile.getInputStream(entry);
                OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(stream);
                OMElement artifact = builder.getDocumentElement();
                String id = artifact.getAttributeValue(new QName(null, "id"));
                String version = artifact.getAttributeValue(new QName(null, "version"));
                Map<String, String> versions = features.get(id);
                if (versions == null) {
                    versions = new HashMap<String, String>();
                    features.put(id, versions);
                }
                versions.put(version, file.getAbsolutePath());
                return;
            }
        }
    }
}
