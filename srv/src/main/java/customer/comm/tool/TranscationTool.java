package customer.comm.tool;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TranscationTool {
    // 创建事务开始
    public static TransactionDefinition transactionInit() {
        return new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);//
    }

    // 创建事务开始
    public static TransactionStatus begin(PlatformTransactionManager transactionManager,
            TransactionDefinition definition) {
        return transactionManager.getTransaction(definition);
    }

    // 事务提交
    public static void commit(PlatformTransactionManager transactionManager, TransactionStatus status) {
        if (transactionManager != null && status != null && !status.isCompleted()) {
            transactionManager.commit(status);
        }
    }

    // 事务回滚
    public static void rollback(PlatformTransactionManager transactionManager, TransactionStatus status) {
        if (transactionManager != null && status != null && !status.isCompleted()) {
            transactionManager.rollback(status);
        }

    }
}
