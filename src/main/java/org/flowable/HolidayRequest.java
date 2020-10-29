package org.flowable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

public class HolidayRequest {
	public static void main(String[] args) {
		//创建流程引擎实例
		Abc A=new Abc();
		A.toString();
		ProcessEngineConfiguration cfg=new StandaloneProcessEngineConfiguration().
				setJdbcUrl("jdbc:mysql://localhost:3306/flowable?characterEncoding=utf8&serverTimezone=GMT%2b8&nullCatalogMeansCurrent=true")
				.setJdbcUsername("root")
				.setJdbcPassword("password")
				.setJdbcDriver("com.mysql.jdbc.Driver")
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		ProcessEngine processEngine = cfg.buildProcessEngine();
		System.out.print("创建完成！！！");
		XYZ x=new XYZ();
		//部署流程文件
		RepositoryService repositoryService = processEngine.getRepositoryService();
		Deployment deployment = repositoryService.createDeployment()
		  .addClasspathResource("holiday-request.bpmn20.xml")
		  .deploy();
		/**
		 * RuntimeService runtimeService = processEngine.getRuntimeService();
		 *	Map<String, Object> variables = new HashMap();
		 *	ProcessInstance processInstance =
		 *		  runtimeService.startProcessInstanceByKey("holidayRequest", variables);
		 */
		
		
		 
		//获取用户已经部署好的流程
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				  .deploymentId(deployment.getId())
				  .singleResult();
				System.out.println("Found process definition : " + processDefinition.getName());
				
		//初始化入参
		Scanner scanner= new Scanner(System.in);

		System.out.println("Who are you?");
		String employee = scanner.nextLine();

		System.out.println("How many holidays do you want to request?");
		Integer nrOfHolidays = Integer.valueOf(scanner.nextLine());

		System.out.println("Why do you need them?");
		String description = scanner.nextLine();
		
		//启动一个流程节点
		RuntimeService runtimeService = processEngine.getRuntimeService();

		Map<String, Object> variables = new HashMap();
		variables.put("employee", employee);
		variables.put("nrOfHolidays", nrOfHolidays);
		variables.put("description", description);
		ProcessInstance processInstance =
		  runtimeService.startProcessInstanceByKey("holidayRequest", variables);
		
		//返回managers组的任务
		TaskService taskService = processEngine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
		System.out.println("You have " + tasks.size() + " tasks:");
		for (int i=0; i<tasks.size(); i++) {
		  System.out.println((i+1) + ") " + tasks.get(i).getName());
		}
		
		//使用任务Id获取特定流程实例的变量，并在屏幕上显示实际的申请
		System.out.println("Which task would you like to complete?");
		int taskIndex = Integer.valueOf(scanner.nextLine());
		Task task = tasks.get(taskIndex - 1);
		Map<String, Object> processVariables = taskService.getVariables(task.getId());
		System.out.println(processVariables.get("employee") + " wants " +
		    processVariables.get("nrOfHolidays") + " of holidays. Do you approve this?");
		
		//模拟经理处理任务的情况,Y的情况
		boolean approved = scanner.nextLine().toLowerCase().equals("y");
		variables = new HashMap<String, Object>();
		variables.put("approved", approved);
		taskService.complete(task.getId(), variables);
		
		//获取历史记录
		HistoryService historyService = processEngine.getHistoryService();
		List<HistoricActivityInstance> activities =
		  historyService.createHistoricActivityInstanceQuery()
		   .processInstanceId(processInstance.getId())
		   .finished()
		   .orderByHistoricActivityInstanceEndTime().asc()
		   .list();

		for (HistoricActivityInstance activity : activities) {
		  System.out.println(activity.getActivityId() + " took "
		    + activity.getDurationInMillis() + " milliseconds");
		}
	}
}
