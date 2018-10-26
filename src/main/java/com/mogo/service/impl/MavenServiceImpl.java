package com.mogo.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mogo.model.MavenModule;
import com.mogo.service.MavenService;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import lombok.extern.log4j.Log4j2;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

@Service
@Log4j2
public class MavenServiceImpl implements MavenService {

    @Value("${git.localPath}")
    private String gitLocalPath;
    private Invoker invoker;
    private static final String POM_FILE = "pom.xml";

    public MavenServiceImpl(@Value("${maven.home}") String mavenHome) {
        this.invoker = new DefaultInvoker();
        this.invoker.setMavenHome(Paths.get(mavenHome).toFile());
    }

    @Override
    public void cleanAndPackage(String projectName) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(Paths.get(gitLocalPath, projectName, POM_FILE).toFile());
        request.setMavenOpts("-Dmaven.test.skip=true");
        request.setGoals(Lists.newArrayList("clean", "package"));
        request.setUserSettingsFile(new File("maven/maven_settings.xml"));
        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0) {
            throw new IllegalStateException("Maven Package failed.");
        }
    }

    @Override
    public void cleanAndInstall(String pomFile) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(Paths.get(gitLocalPath, pomFile).toFile());
        request.setMavenOpts("-Dmaven.test.skip=true");
        request.setGoals(Lists.newArrayList("clean", "install"));
        request.setUserSettingsFile(new File("maven/maven_settings.xml"));
        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0) {
            throw new IllegalStateException("Maven Install failed.");
        }
    }

    @Override
    public TreeNode<MavenModule> parseModuleTree(String projectName, String repoUrl, String buildPack) {
        Path pomFile = Paths.get(projectName, POM_FILE);
        Path fullPomFile = Paths.get(gitLocalPath, pomFile.toString());
        if (!fullPomFile.toFile().exists()) {
            return null;
        }
        MavenProject project = parsePom(fullPomFile.toFile());
        String targetFile = getTargetFile(project);
        String serviceName = parseService(project);
        boolean isService = !Strings.isNullOrEmpty(serviceName);
        MavenModule mavenModule = new MavenModule(
                project.getGroupId(),
                project.getArtifactId(),
                project.getVersion(),
                project.getPackaging(),
                targetFile,
                pomFile.toString(),
                buildPack,
                isService,
                serviceName,
                projectName,
                repoUrl);
        TreeNode<MavenModule> root = new ArrayMultiTreeNode<>(mavenModule);
        List<String> modules = project.getModules();
        for (String subModule : modules) {
            Path subPomFile = Paths.get(projectName, subModule, POM_FILE);
            Path fullSubPomFile = Paths.get(gitLocalPath, subPomFile.toString());
            MavenProject subProject = parsePom(fullSubPomFile.toFile());
            String subTargetFile = getTargetFile(subProject);
            String subServiceName = parseService(subProject);
            boolean isSubService = !Strings.isNullOrEmpty(subServiceName);
            MavenModule subMavenModule = new MavenModule(
                    subProject.getGroupId(),
                    subProject.getArtifactId(),
                    subProject.getVersion(),
                    subProject.getPackaging(),
                    subTargetFile,
                    subPomFile.toString(),
                    buildPack,
                    isSubService,
                    subServiceName,
                    projectName,
                    repoUrl);
            root.add(new ArrayMultiTreeNode<>(subMavenModule));
        }
        return root;
    }

    private String parseService(MavenProject project) {
        String serviceName = null;
        String baseDir = project.getModel().getPomFile().getParentFile().getAbsolutePath();
        List<String> resourceFiles = Lists.newArrayList();
        Path resourceDir = Paths.get(baseDir, "src/main/resources");
        if (resourceDir.toFile().exists()) {
            try {
                Files.walk(resourceDir, 3)
                        .filter(Files::isRegularFile)
                        .filter(e -> e.getFileName().toString().endsWith(".xml"))
                        .filter(e -> e.getFileName().toString().contains("dubbo"))
                        .filter(e -> !e.toAbsolutePath().toString().contains("jobs"))
                        .forEach(e -> resourceFiles.add(e.toAbsolutePath().toString()));
            } catch (IOException ioe) {
                log.info("Exception:{}", ioe);
            }
        }
        if (resourceFiles.size() != 0) {
            for (String resourceFile : resourceFiles) {
                serviceName = parseXml(resourceFile);
                if (!Strings.isNullOrEmpty(serviceName)) {
                    break;
                }
            }
        }
        return serviceName;
    }

    private String parseXml(String xmlFile) {
        String result = null;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(xmlFile);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(new NamespaceResolver(document));
            XPathExpression expr = xpath.compile("//msf:application/@name");
            result = (String) expr.evaluate(document, XPathConstants.STRING);
            //log.info("ProjectService name:{}", result);
        } catch (Exception e) {
            log.info("Exception:{}", e);
        }
        return result;
    }

    class NamespaceResolver implements NamespaceContext {
        //Store the source document to search the namespaces
        private Document sourceDocument;

        public NamespaceResolver(Document document) {
            sourceDocument = document;
        }

        //The lookup for the namespace uris is delegated to the stored document.
        public String getNamespaceURI(String prefix) {
            if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                return sourceDocument.lookupNamespaceURI(null);
            } else {
                return sourceDocument.lookupNamespaceURI(prefix);
            }
        }

        public String getPrefix(String namespaceURI) {
            return sourceDocument.lookupPrefix(namespaceURI);
        }

        @SuppressWarnings("rawtypes")
        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }
    }

    private MavenProject parsePom(File pomFile) {
        Model model = null;
        FileReader reader;
        MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
        try {
            reader = new FileReader(pomFile.toString());
            model = mavenXpp3Reader.read(reader);
            model.setPomFile(pomFile);
        } catch (Exception ex) {
            log.info("Exception:{}", ex);
        }
        MavenProject project = new MavenProject(model);
        return project;
    }

    private String getTargetFile(MavenProject project) {
        String baseDir = project.getModel().getPomFile().getParentFile().getAbsolutePath();
        List<String> targetFiles = Lists.newArrayList();
        Path targetDir = Paths.get(baseDir, "target");
        if (targetDir.toFile().exists()) {
            try {
                Files.walk(targetDir, 1)
                        .filter(Files::isRegularFile)
                        .filter(e -> e.getFileName().toString().endsWith(".jar"))
                        .filter(e -> !e.getFileName().toString().endsWith("-sources.jar"))
                        .forEach(e -> targetFiles.add(e.toAbsolutePath().toString()));
            } catch (IOException ioe) {
                log.info("Exception:{}", ioe);
            }
        }
        if (targetFiles.size() != 0) {
            return targetFiles.get(0);
        } else {
            return null;
        }
    }
}
