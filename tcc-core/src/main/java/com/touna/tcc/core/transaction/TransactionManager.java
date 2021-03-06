package com.touna.tcc.core.transaction;

import com.touna.tcc.core.TccCommitException;
import com.touna.tcc.core.TccRollbackException;
import com.touna.tcc.core.interceptor.TransactionInfo;
import com.touna.tcc.core.log.service.TxChildLogService;
import com.touna.tcc.core.log.service.TxLogService;

/**
 * Created by chenchaojian on 17/5/10.
 */
public interface TransactionManager {

    /**
     * 提交事务
     */
    void commit(TransactionInfo txInfo)throws TccCommitException;

    /**
     * 回滚事务
     */
    void rollback(TransactionInfo txInfo)throws TccRollbackException;


    TransactionStatus getTransaction(String xid);

//    TxLogService getTxLogService();
//
//    TxChildLogService getTxChildLogService();


}
