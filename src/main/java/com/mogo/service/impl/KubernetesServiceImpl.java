package com.mogo.service.impl;

import com.google.common.base.Strings;
import com.mogo.service.KubernetesService;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.kubernetes.api.model.extensions.IngressBackend;
import io.fabric8.kubernetes.api.model.extensions.IngressSpec;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import lombok.extern.log4j.Log4j2;
import sun.rmi.runtime.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author wangchuanjian
 */
@org.springframework.stereotype.Service
@Log4j2
public class KubernetesServiceImpl implements KubernetesService {

    private KubernetesClient client;


    public KubernetesServiceImpl() {
        try {
            this.client = new DefaultKubernetesClient();
            log.info("Kubernetes client is initialized");
        } catch (Exception e) {
            log.info("Kubernetes client is failed to be initialized");
        }
    }

    @Override
    public Namespace createNamespace(String namespace) {
        Namespace myns = this.client.namespaces().createNew()
                .withNewMetadata()
                .withName(namespace)
                .endMetadata()
                .done();
        return myns;
    }
    @Override
    public List<Namespace> getNamespaces() {
        return this.client.namespaces().list().getItems();
    }

    @Override
    public Pod getPodByNamespaceAndPodName(String namespace,String podName){
        namespace = Strings.isNullOrEmpty(namespace) ? "default" : namespace;
        return this.client.pods().inNamespace(namespace).withName(podName).get();
    }

    @Override
    public String getPodLog(String namespace,String podName){
        namespace = Strings.isNullOrEmpty(namespace) ? "default" : namespace;
        String logStr=this.client.pods().inNamespace(namespace).withName(podName).getLog();
        log.info("查看pod日志信息============"+logStr);
        return logStr;
    }


    @Override
    public InputStream watchPodLog(String namespace, String podName){
        namespace = Strings.isNullOrEmpty(namespace) ? "default" : namespace;
        LogWatch logWatch=this.client.pods().inNamespace(namespace).withName(podName).watchLog();
        return logWatch.getOutput();
    }

    @Override
    public List<Pod> getPods(String namespace) {
        namespace = Strings.isNullOrEmpty(namespace) ? "default" : namespace;
        return this.client.pods().inNamespace(namespace).list().getItems();
    }

    @Override
    public Boolean podDel(String namespace,String podName) {
        namespace = Strings.isNullOrEmpty(namespace) ? "default" : namespace;
        return this.client.pods().inNamespace(namespace).withName(podName).delete();
    }

    @Override
    public Boolean nameSpaceDel(String name) {
        return this.client.namespaces().withName(name).delete();
    }

    @Override
    public Namespace getOneNamespace(String namespace) {
        return client.namespaces().withName(namespace).get();
    }

    @Override
    public Service createNewService(String namespace, String serviceName, HashMap mapInfo) {
        Service service = new ServiceBuilder()
                .withNewMetadata()
                .withName(serviceName)
                .endMetadata()
                .withNewSpec()
                .addNewPort()
                .withName(mapInfo.get("name").toString())
                .withProtocol(mapInfo.get("protocol").toString())
                .withPort(Integer.getInteger(mapInfo.get("port").toString()))
                .withTargetPort(new IntOrString(8888))
                .endPort()
                .withType(mapInfo.get("type").toString())
                .endSpec()
                .withNewStatus()
                .withNewLoadBalancer()
                .addNewIngress()
                .withIp(mapInfo.get("ip").toString())
                .endIngress()
                .endLoadBalancer()
                .endStatus()
                .build();
        service = client.services().inNamespace(namespace).create(service);
        log.info("Created service", service);
        return service;
    }

    @Override
    public List<Service> serviceList(String namespace) {
        return this.client.services().inNamespace(namespace).list().getItems();
    }

    @Override
    public Service getServiceInfo(String namespace, String serviceName) {
        return this.client.services().inNamespace(namespace).withName(serviceName).get();
    }

    @Override
    public Boolean serviceDelByName(String namespace, String serviceName) {
        return this.client.services().inNamespace(namespace).withName(serviceName).delete();
    }

    @Override
    public Deployment createNewDeployment(String namespace,String deploymentName,HashMap mapInfo) {
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName(deploymentName)
                .endMetadata()
                .withNewSpec()
                .withReplicas(1)
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app", deploymentName)
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(deploymentName)
                .withImage(deploymentName)
                .addNewPort()
                .withContainerPort(88)
                .endPort()
                .endContainer()
                .endSpec()
                .endTemplate()
                .withNewSelector()
                .addToMatchLabels("app", deploymentName)
                .endSelector()
                .endSpec()
                .build();
        deployment = this.client.apps().deployments().inNamespace(namespace).create(deployment);
        log.info("Created deployment", deployment);
        return deployment;
    }

    @Override
    public List<Deployment> deploymentList(String namespace) {
        return this.client.apps().deployments().inNamespace(namespace).list().getItems();
    }

    @Override
    public Deployment getDeploymentInfo(String namespace, String serviceName) {
        return this.client.apps().deployments().inNamespace(namespace).withName(serviceName).get();
    }

    @Override
    public Boolean deploymentDelByName(String namespace, String serviceName) {
        return this.client.apps().deployments().inNamespace(namespace).withName(serviceName).delete();
    }

    @Override
    public Ingress createNewIngress(String namespace, String ingressName,HashMap mapInfo) {
        Ingress ingress = new Ingress();
        ingress.setKind(mapInfo.get("Ingress").toString());
        IngressSpec is=new IngressSpec();
        IngressBackend backend =new IngressBackend();
        backend.setServiceName(mapInfo.get("name").toString());
        backend.setServicePort(new IntOrString(mapInfo.get("port").toString()));
        is.setBackend(backend);
        ingress.setSpec(is);
        ObjectMeta om=new ObjectMeta();
        om.setName(ingressName);
        om.setGenerateName(ingressName);
        om.setNamespace(namespace);

        ingress.setMetadata(om);
        ingress=this.client.extensions().ingresses().inNamespace(namespace).create(ingress);
        log.info("Created ingress", ingress);
        return ingress;
    }

    @Override
    public List<Ingress> ingressList(String namespace) {
        List<Ingress> ingressList=this.client.extensions().ingresses().inNamespace(namespace).list().getItems();
        return ingressList;
    }

    @Override
    public Ingress getIngressInfo(String namespace, String ingreeName) {
        Ingress ingress=this.client.extensions().ingresses().inNamespace(namespace).withName(ingreeName).get();
        return ingress;
    }

    @Override
    public Boolean ingressDelByName(String namespace, String ingreeName) {
        return this.client.extensions().ingresses().inNamespace(namespace).withName(ingreeName).delete();
    }

    /**
     * 获取 node 机器节点信息
     */
    @Override
    public List<Node> nodeList(){
        List<Node> nodeList=this.client.nodes().list().getItems();
        return nodeList;
    }
    @Override
    public Node getNodeInfo(String nodeName){
        Node node=this.client.nodes().withName(nodeName).get();
        return node;
    }

}
