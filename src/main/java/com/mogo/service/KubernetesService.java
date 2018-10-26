package com.mogo.service;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.extensions.Ingress;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * @author wangchuanjain
 */
public interface KubernetesService {

    /**
     * 得到pod
     * @param namespace
     * @param podName
     * @return
     */
    Pod getPodByNamespaceAndPodName(String namespace,String podName);

    /**
     * 得到命名空间集和
     * @return
     */
    List<Namespace> getNamespaces();

    /**
     * 得到命名空间下的 pods
     * @param namespace
     * @return
     */
    List<Pod> getPods(String namespace);

    /**
     * 删除 pod
     * @param namespace
     * @param podName
     * @return
     */
    Boolean podDel(String namespace,String podName);

    /**
     * 创建nameSpace
     * @param namespace
     * @return
     */
    Namespace createNamespace(String namespace);

    /**
     * 删除k8s命名空间NameSpace
     * @param namespace
     * @return
     */
    Boolean nameSpaceDel(String namespace);

    /**
     * 根据名字获取 某个 命名空间
     * @param namespace
     * @return
     */
    Namespace getOneNamespace(String namespace);

    /**
     * 某个命名
     * @param namespace
     * @return
     */
    List<Service> serviceList(String namespace);

    /**
     * 删除 service
     * @param namespace
     * @param serviceName
     * @return
     */
    Boolean serviceDelByName(String namespace,String serviceName);

    /**
     * 获取单个 service
     * @param namespace
     * @param serviceName
     * @return
     */
    Service getServiceInfo(String namespace, String serviceName);

    /**
     * 得到Deployment 集和
     * @param namespace
     * @return
     */
    List<Deployment> deploymentList(String namespace);

    /**
     * 得到 Deployment
     * @param namespace
     * @param deploymentName
     * @return
     */
    Deployment getDeploymentInfo(String namespace, String deploymentName);

    /**
     * 删除Deployment
     * @param namespace
     * @param deploymentName
     * @return
     */
    Boolean deploymentDelByName(String namespace, String deploymentName);

    /**
     * 得到 Ingress集和
     * @param namespace
     * @return
     */
    List<Ingress> ingressList(String namespace);

    /**
     * 得到单个 Ingress
     * @param namespace
     * @param ingreeName
     * @return
     */
    Ingress getIngressInfo(String namespace, String ingreeName);

    /**
     * 删除 Ingress
     * @param namespace
     * @param ingreeName
     * @return
     */
    Boolean ingressDelByName(String namespace, String ingreeName);

    /**
     * 服务器集和
     * @return
     */
    List<Node> nodeList();

    /**
     * 单个服务信息
     * @param nodeName
     * @return
     */
    Node getNodeInfo(String nodeName);

    /**
     * 创建Service
     * @param namespace
     * @param serviceName
     * @return
     */
    Service createNewService(String namespace, String serviceName,HashMap mapInfo);

    /**
     * 创建Deployment
     * @param namespace
     * @param deploymentName
     * @return
     */
    Deployment createNewDeployment(String namespace,String deploymentName,HashMap mapInfo);

    /**
     * 创建 Ingress
     * @param namespace
     * @param ingressName
     * @return
     */
    Ingress createNewIngress(String namespace, String ingressName, HashMap mapInfo);

    /**
     * 得到 pod 运行日志信息
     * @param namespace
     * @param podName
     * @return
     */
    String getPodLog(String namespace,String podName);

    /**
     * 时时查看pod 日志信息
     * @param namespace
     * @param podName
     * @return
     */
    InputStream watchPodLog(String namespace, String podName);
}
