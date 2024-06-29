package org.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendRejectionMail implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(SendRejectionMail.class);

    @Override
    public void execute(DelegateExecution execution) {
        log.info("CurrentActivityId: {}",execution.getCurrentActivityId());
        log.info("ProcessDefinitionId: {}",execution.getProcessDefinitionId());
        log.info("ProcessInstanceId: {}",execution.getProcessInstanceId());
        log.info("Variables: {}",execution.getVariables().toString());
    }
}
