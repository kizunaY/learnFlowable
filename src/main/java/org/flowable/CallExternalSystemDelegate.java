package org.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class CallExternalSystemDelegate implements JavaDelegate{

	public void execute(DelegateExecution execution) {
		// TODO Auto-generated method stub
		System.out.println(System.getenv());
		System.out.println("Calling the external system for employee "
	            + execution.getVariable("employee"));
	}
	
}
