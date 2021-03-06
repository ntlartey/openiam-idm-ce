package org.openiam.idm.srvc.report.service;

import org.openiam.idm.srvc.report.domain.ReportCriteriaParamEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

/**
 * Smoke Test for DAO service for ReportCriteriaParamEntity
 *
 * @author vitaly.yakunin
 */
@ContextConfiguration(locations = {"classpath:applicationContext-test.xml","classpath:dozer-application-context-test.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ReportCriteriaParamDAOTouchTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private ReportCriteriaParamDao criteriaParamDao;

    @Test
    public void touchSave() {
        criteriaParamDao.save(new ReportCriteriaParamEntity());
    }

    @Test
    public void touchFindByReportInfoId() {
        criteriaParamDao.findByReportInfoId("1");
    }
}
