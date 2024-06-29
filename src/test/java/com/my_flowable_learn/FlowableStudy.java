package com.my_flowable_learn;


import liquibase.pro.packaged.M;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.crypto.MacSpi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowableStudy {


    private static final Logger log = LoggerFactory.getLogger(FlowableStudy.class);

    @Test
    public void testProcessEngine() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("flowable.cfg.xml");
        ProcessEngineConfiguration processEngineConfiguration = applicationContext.getBean("processEngineConfiguration", ProcessEngineConfiguration.class);

        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        System.out.println("Process Engine Name: " + processEngine.getName());
    }
    ProcessEngineConfiguration processEngineConfiguration = null;

    @Before
    public void setUp() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("flowable.cfg.xml");
        processEngineConfiguration = applicationContext.getBean("processEngineConfiguration", ProcessEngineConfiguration.class);
    }
    /**
     * 部署流程Deploy,蓝图
     */
    @Test
    public void deployProcessEngine() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        Deployment deployment = processEngine
                .getRepositoryService()
                .createDeployment()
                .addClasspathResource("holiday-request.bpmn20.xml")
                .name("请假流程")
                .deploy();
        log.info("Deployment ID: {}", deployment.getId());
        log.info("Deployment Name: {}", deployment.getName());
    }
    /**
     * 查询部署信息queryDeploy
     */
    @Test
    public void queryDeployment() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId("1")
                .singleResult();
        log.info("Process Definition DeploymentId: {}", processDefinition.getDeploymentId());
        log.info("Process Definition Name: {}", processDefinition.getName());
        log.info("Process Definition Description: {}", processDefinition.getDescription());
        log.info("Process Definition ID: {}",processDefinition.getId());
    }
    /**
     * 删除流程定义，deleteDeployment
     */
    @Test
    public void deleteDeployment() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.deleteDeployment("7501", true);
    }
    /**
     * 启动流程实例，runProcess
     */
    @Test
    public void runProcessEngine() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> variables = new HashMap<String, Object>();

        variables.put("员工","张三");
        variables.put("天数","3");
        variables.put("理由","出去玩！");

        ProcessInstance holidayRequest = runtimeService.startProcessInstanceByKey("holidayRequest", variables);
        log.info("Holiday Request ID: {}", holidayRequest.getId());
        log.info("Holiday Request activityId: {}", holidayRequest.getActivityId());
        log.info("Holiday Request processDefinitionId: {}", holidayRequest.getProcessDefinitionId());
        log.info("Holiday Request processDefinitionKey: {}", holidayRequest.getProcessDefinitionKey());
    }
    /**
     * 查询任务queryTask
     */
    @Test
    public void queryTask() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        TaskService taskService = processEngine.getTaskService();

        List<Task> taskList = taskService
                .createTaskQuery()
                .processDefinitionKey("holidayRequest")
                .taskAssignee("李四")
                .list();
        for (Task task : taskList) {
            log.info("Task ID: {}", task.getId());
            log.info("Task ProcessDefinitionId: {}", task.getProcessDefinitionId());
            log.info("Task Assignee: {}", task.getAssignee());
            log.info("Task Name: {}", task.getName());
            log.info("Task Description: {}", task.getDescription());
        }
        List<Execution> executions = processEngine.getRuntimeService().createExecutionQuery().list();
        for (Execution execution : executions) {
            log.info("Execution ID: {}", execution.getId());
            log.info("Execution Name: {}", execution.getName());
            log.info("Execution Description: {}", execution.getDescription());
            log.info("Execution ProcessInstanceId: {}", execution.getProcessInstanceId());
            log.info("Execution ActivityId: {}", execution.getActivityId());
            log.info("Execution IsEnded {}",execution.isEnded());
            log.info("Execution IsSuspended {}",execution.isSuspended());
            log.info("Execution ParentId {}",execution.getParentId());
        }


    }
    /**
     * 完成任务，completeTask
     */
    @Test
    public void completeTask() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //查询任务
        Task task = taskService
                .createTaskQuery()
                .processDefinitionKey("holidayRequest")
                .taskAssignee("李四")
                .singleResult();

        Map<String, Object> variables = new HashMap<>();

        variables.put("approved", false);
        taskService.complete(task.getId(), variables);
    }

    /**
     * 查询历史记录  queryHistory
     */
    @Test
    public void queryHistory() {
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        HistoryService historyService = processEngine.getHistoryService();

        List<HistoricActivityInstance> historicActivityInstanceList = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId("2501")
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();
        historicActivityInstanceList.forEach(historic -> log.info("Historic Activity Instance ID: {}, \n ActivityId: {} \n ActivityName: {}\n Assignee: {} \n DurationInMillis: {}", historic.getId(),historic.getActivityId(),historic.getActivityName(),historic.getAssignee(),historic.getDurationInMillis()));
    }

}
