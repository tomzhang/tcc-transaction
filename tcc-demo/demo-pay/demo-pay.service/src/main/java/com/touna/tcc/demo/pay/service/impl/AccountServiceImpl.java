package com.touna.tcc.demo.pay.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import com.touna.tcc.demo.pay.dao.AccountDao;
import com.touna.tcc.demo.pay.dao.Operation;
import com.touna.tcc.demo.pay.dao.model.Account;
import com.touna.tcc.demo.pay.dao.model.PreAccount;
import com.touna.tcc.demo.pay.service.intf.AccountService;

/**
 * Created by chenchaojian on 17/5/27.
 */

public class AccountServiceImpl implements AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Resource
    AccountDao accountDao;

    @Transactional(value="payTransactionManager")
    @Override
    public void pay(String xid, String accountId, Double amount)
    {
     try
     {

        Account account = accountDao.selectAccountByIdForUpdate(accountId);
        Double preBalance =  account.getPreBalance();
        if(preBalance - amount < 0){
            throw new IllegalArgumentException("accountId "+accountId+" money not enough" );
        }

        PreAccount preAccount = new PreAccount();
        preAccount.setAccountId(accountId);
        preAccount.setDelta(amount);
        preAccount.setOperation(Operation.PAY.getValue());
        preAccount.setXid(xid);


        //data check
        if (preAccount.getOperation() != Operation.PAY.getValue()) {
            throw new IllegalArgumentException("prePay operation should be 0");
        }

        accountDao.insert(preAccount);
        account.setDelta(amount);
        accountDao.preBalanceMinusByDelta(account);
     } catch (DuplicateKeyException ex) {//幂等性校验 该预处理已经提交过.忽略这种异常
            logger.warn("pre account had done before " + ex.getMessage(), ex);
     }
    }

    @Transactional(value="payTransactionManager")
    @Override
    public void payCommit(String xid, String accountId) {

        //避免并发操作
        PreAccount preAccount = accountDao.selectPreAccountByXidForUpdate(xid);
        if (preAccount == null) {
            //幂等性操作。
            return;
        }

        Account account = new Account();
        account.setAccountId(preAccount.getAccountId());
        account.setDelta(preAccount.getDelta());
        accountDao.balanceMinusByDelta(account);
        accountDao.deletePreAccountByXid(xid);

    }


    @Transactional(value="payTransactionManager")
    @Override
    public void payRollback(String xid, String accountId) {
        PreAccount preAccount = accountDao.selectPreAccountByXid(xid);
        int rowsAffected = accountDao.deletePreAccountByXid(xid);
        if(rowsAffected == 1){//避免并发删除操作，做幂等性保护
            Account account = new Account();
            account.setAccountId(accountId);
            account.setDelta(preAccount.getDelta());
            accountDao.preBalanceAddByDelta(account);
        }
    }

    @Override
    public void deposit(String xid,String uuid, String accountIdt) {

    }
}
