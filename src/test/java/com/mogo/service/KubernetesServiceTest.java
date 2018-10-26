package com.mogo.service;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import lombok.extern.log4j.Log4j2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
public class KubernetesServiceTest {
    @Before
    public void setUp() throws Exception {
        //执行前创建一个  namespace
        Namespace cc=kubernetesService.createNamespace("wang-text");
    }
    @After
    public void setAfter()throws Exception {
        //执行 完成后 删除  namespace
        Boolean cc=kubernetesService.nameSpaceDel("wang-text");
        assertThat(cc.equals(true));
    }
    @Autowired
    private KubernetesService kubernetesService;


    @Test
    public void testGetNamespaces() throws Exception {
        for (Namespace namespace : kubernetesService.getNamespaces()) {
            log.info("Namespace:{}", namespace.getMetadata().getName());
        }
    }

    @Test
    public void getOneNamespace() throws Exception {
        Namespace cc=kubernetesService.getOneNamespace("wang-text");
        assertThat(cc).isNotNull();
    }

    @Test
    public void deployment() throws Exception {
        HashMap mapInfo = new HashMap();
        Deployment ss=kubernetesService.createNewDeployment("wang-text","tomcat",mapInfo);
        log.info("创建Deployment信息为:"+ss);
        assertThat(ss).isNotNull();

        List<Deployment> cc=kubernetesService.deploymentList("wang-text");
        log.info("获取Deployment集和信息为:"+cc);
        assertThat(cc).isNotNull();

        Deployment deployment=kubernetesService.getDeploymentInfo("wang-text","tomcat");
        log.info("获取Deployment信息为:"+deployment);
        assertThat(deployment).isNotNull();

        List<Pod> podList=kubernetesService.getPods("wang-text");
        assertThat(podList).isNotNull();
        for(Pod pod:podList) {
            kubernetesService.getPodByNamespaceAndPodName("wang-text", pod.getMetadata().getName());
            assertThat(pod).isNotNull();

            String podLog=kubernetesService.getPodLog("wang-text",pod.getMetadata().getName());
            assertThat(podLog).isNotNull();
            kubernetesService.watchPodLog("wang-text",pod.getMetadata().getName());

            Boolean isSuccess=kubernetesService.podDel("wang-text","tomcat");
            log.info("podDel "+isSuccess);
            assertThat(isSuccess.equals(true));
        }
        Boolean isSuccess=kubernetesService.deploymentDelByName("wang-text","tomcat");
        log.info("deploymentDel "+isSuccess);
        assertThat(isSuccess.equals(true));

    }

    @Test
    public void createNewService() throws Exception {
        HashMap mapInfo = new HashMap();
        mapInfo.put("name","test-port");
        mapInfo.put("protocol","TCP");
        mapInfo.put("port",80);
        mapInfo.put("type","LoadBalancer");
        mapInfo.put("ip","192.168.1.22");
        Service ss=kubernetesService.createNewService("wang-text","text-service",mapInfo);
        log.info("创建service信息为:"+ss);
        assertThat(ss).isNotNull();

        List<Service> cc=kubernetesService.serviceList("wang-text");
        log.info("获取service集和信息为:"+cc);
        assertThat(cc).isNotNull();
        for(Service service:cc){
            Service s=kubernetesService.getServiceInfo("wang-text",service.getMetadata().getName());
            log.info("获取service信息为:"+s);
            Boolean aBoolean=kubernetesService.serviceDelByName("wang-text","text-service");
            assertThat(aBoolean.equals(true));
        }
    }

    @Test
    public void createNewIngress() throws Exception {
        HashMap mapInfo = new HashMap();
        mapInfo.put("Ingress","test-ingress");
        mapInfo.put("name","name");
        mapInfo.put("port","8889");
        Ingress ss=kubernetesService.createNewIngress("wang-text","text-ingress",mapInfo);
        log.info("创建Ingress信息为:"+ss);

        List<Ingress>  cc=kubernetesService.ingressList("wang-text");
        log.info("获取Ingress集合信息为:"+cc);
        assertThat(cc).isNotNull();

        for(Ingress ingress:cc){
            Ingress in=kubernetesService.getIngressInfo("wang-text","text-ingress");
            log.info("获取Ingress信息为:"+in);
            assertThat(in).isNotNull();

            Boolean aBoolean=kubernetesService.ingressDelByName("wang-text","text-ingress");
            assertThat(aBoolean.equals(true));
        }
    }

    @Test
    public void nodeList() throws Exception {
        List<Node> cc=kubernetesService.nodeList();
        assertThat(cc).isNotNull();
    }
    @Test
    public void getNodeInfo() throws Exception {
        Node cc=kubernetesService.getNodeInfo("wang-text");
        log.info("查询单个Node信息为:"+cc);
    }


}
