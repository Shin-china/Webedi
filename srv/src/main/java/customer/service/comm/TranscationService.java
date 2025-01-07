package customer.service.comm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import customer.comm.tool.TranscationTool;

public class TranscationService {

    @Autowired
    protected PlatformTransactionManager transactionManager;

    // 创建事务开始
    public TransactionDefinition transactionInit() {
        return TranscationTool.transactionInit();
    }

    // 创建事务开始
    public TransactionStatus begin(TransactionDefinition definition) {
        return TranscationTool.begin(transactionManager, definition);
    }

    // 事务提交
    public void commit(TransactionStatus status) {
        TranscationTool.commit(transactionManager, status);
    }

    // 事务回滚
    public void rollback(TransactionStatus status) {
        TranscationTool.rollback(transactionManager, status);
    }

}
