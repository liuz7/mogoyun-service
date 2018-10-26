package com.mogo.controller.rest;


import com.mogo.service.KubernetesService;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

/**
 * @author wangchuanjian
 */

@RestController
@RequestMapping("/k8s")
@Log4j2
@Api(description = "Set of endpoints for node,pod,namespace,deployment,service and ingress resources from kubernetes.")
public class KubernetesController {

    @Autowired
    private KubernetesService kubernetesService;

    @GetMapping("/nameSpaceList")
    @ApiOperation("Get the all namespaces in k8s")
    public List<Namespace> getAllNamespaces() {
        return kubernetesService.getNamespaces();
    }

    @PostMapping("/createNameSpace")
    @ApiOperation("namespaces create in k8s ")
    public Namespace createNamespace(String namespaces) {
        return kubernetesService.createNamespace(namespaces);
    }

    @DeleteMapping("/nameSpaceDel")
    @ApiOperation(" namespaces delete by namespace")
    public Boolean nameSpaceDel(String namespace) {
        return kubernetesService.nameSpaceDel(namespace);
    }

    @GetMapping("/namespaces/{namespace}/getNamespace")
    @ApiOperation(" get namespaces by namespace")
    public Namespace getNamespace(@PathVariable("namespace") String namespace) {
        return kubernetesService.getOneNamespace(namespace);
    }

    @GetMapping("/{namespace}/pods/podList")
    @ApiOperation("by namespace Get the podList in k8s")
    public List<Pod> getPodsByNamespace(@PathVariable("namespace") String namespace) {
        return kubernetesService.getPods(namespace);
    }
    @GetMapping("/{namespace}/pods/{podName}/pod")
    @ApiOperation("by namespace and podName Get the pod ")
    public Pod getPodByNamespaceAndPodName(@PathVariable("namespace") String namespace,@PathVariable("podName") String podName) {
        return kubernetesService.getPodByNamespaceAndPodName(namespace,podName);
    }
    @DeleteMapping("/{namespace}/pods/{podName}/podDel")
    @ApiOperation("k8s Del pod by podName")
    public Boolean getPods(@PathVariable("namespace") String namespace,@PathVariable("podName") String podName) {
        return kubernetesService.podDel(namespace,podName);
    }

    @GetMapping("/{namespace}/pods/{podName}/getPodLog")
    @ApiOperation("GET podLog by podName in k8s ")
    public String getPodLog(@PathVariable("namespace") String namespace,@PathVariable("podName") String podName) {
        return kubernetesService.getPodLog(namespace,podName);
    }

    @GetMapping("/{namespace}/pods/{podName}/watchPodLog")
    @ApiOperation("GET run watchPodLog by podName in k8s ")
    public InputStream watchPodLog(@PathVariable("namespace") String namespace,@PathVariable("podName") String podName) {
        return kubernetesService.watchPodLog(namespace,podName);
    }

    @PostMapping("/{namespace}/service/{serviceName}/createService")
    @ApiOperation("k8s createService service by name")
    public Service createNewService(@PathVariable("namespace") String namespace,@PathVariable("serviceName") String serviceName) {
        return kubernetesService.createNewService(namespace,serviceName,null);
    }
    @GetMapping("/{namespace}/service/serviceList")
    @ApiOperation("k8s GET serviceList")
    public List<Service> createNewService(@PathVariable("namespace") String namespace) {
        return kubernetesService.serviceList(namespace);
    }
    @DeleteMapping("/{namespace}/service/{serviceName}/serviceDelByName")
    @ApiOperation("k8s Del service by name")
    public Boolean serviceDelByName(@PathVariable("namespace") String namespace,@PathVariable("serviceName") String serviceName) {
        return kubernetesService.serviceDelByName(namespace,serviceName);
    }
    @GetMapping("/{namespace}/service/{serviceName}/getServiceInfo")
    @ApiOperation("k8s GET service by name")
    public Service getServiceInfo(@PathVariable("namespace") String namespace,@PathVariable("serviceName") String serviceName) {
        return kubernetesService.getServiceInfo(namespace,serviceName);
    }


    @PostMapping("/{namespace}/deployment/{deploymentName}/createNewDeployment")
    @ApiOperation("k8s create New Deployment")
    public Deployment createNewDeployment(@PathVariable("namespace") String namespace,@PathVariable("deploymentName") String deploymentName) {
        return kubernetesService.createNewDeployment(namespace,deploymentName,null);
    }

    @GetMapping("/{namespace}/deployment/deploymentList")
    @ApiOperation("k8s GET deploymentList")
    public List<Deployment> deploymentList(@PathVariable("namespace") String namespace) {
        return kubernetesService.deploymentList(namespace);
    }
    @DeleteMapping("/{namespace}/deployment/{deploymentName}/deploymentDelByName")
    @ApiOperation("k8s Del Deployment by name")
    public Boolean deploymentDelByName(@PathVariable("namespace") String namespace,@PathVariable("deploymentName") String deploymentName) {
        return kubernetesService.deploymentDelByName(namespace,deploymentName);
    }
    @GetMapping("/{namespace}/deployment/{deploymentName}/getServiceInfo")
    @ApiOperation("k8s GET service by name")
    public Deployment getDeploymentInfo(@PathVariable("namespace") String namespace,@PathVariable("deploymentName") String deploymentName) {
        return kubernetesService.getDeploymentInfo(namespace,deploymentName);
    }


    @PostMapping("/{namespace}/ingress/{ingressName}/createNewIngress")
    @ApiOperation("create New ingress in k8s")
    public Ingress createNewIngress(@PathVariable("namespace") String namespace,@PathVariable("ingressName") String ingressName) {
        return kubernetesService.createNewIngress(namespace,ingressName,null);
    }

    @GetMapping("/{namespace}/ingress/ingressList")
    @ApiOperation("GET ingressList in k8s")
    public List<Ingress> ingressList(@PathVariable("namespace") String namespace) {
        return kubernetesService.ingressList(namespace);
    }
    @DeleteMapping("/{namespace}/ingress/{ingressName}/ingressDelByName")
    @ApiOperation("Del ingress by ingressName")
    public Boolean ingressDelByName(@PathVariable("namespace") String namespace,@PathVariable("ingressName") String ingressName) {
        return kubernetesService.ingressDelByName(namespace,ingressName);
    }
    @GetMapping("/{namespace}/ingress/{ingressName}/getIngressInfo")
    @ApiOperation("GET ingress by ingressName ")
    public Ingress getIngressInfo(@PathVariable("namespace") String namespace,@PathVariable("ingressName") String ingressName) {
        return kubernetesService.getIngressInfo(namespace,ingressName);
    }

    @GetMapping("/node/nodeList")
    @ApiOperation("GET nodeList in k8s")
    public List<Node> nodeList() {
        return kubernetesService.nodeList();
    }

    @GetMapping("/node/{nodeName}/getNodeInfo")
    @ApiOperation("GET Node  in k8s")
    public Node getNodeInfo(@PathVariable("nodeName") String nodeName) {
        return kubernetesService.getNodeInfo(nodeName);
    }

}
