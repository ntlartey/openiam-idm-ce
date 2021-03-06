package org.openiam.idm.srvc.report.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.report.dto.ReportInfoDto;

/**
 * This entity used in reporting system to define Report information
 *
 * @author vitaly.yakunin
 */
@Entity
@Table(name = "REPORT_INFO")
@DozerDTOCorrespondence(ReportInfoDto.class)
public class ReportInfoEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "REPORT_INFO_ID")
    private String id;

    @Column(name = "REPORT_NAME")
    private String reportName;

    @Column(name = "DATASOURCE_FILE_PATH")
    private String datasourceFilePath;

    @Column(name = "REPORT_FILE_PATH")
    private String reportFilePath;

    public ReportInfoEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getDatasourceFilePath() {
        return datasourceFilePath;
    }

    public void setDatasourceFilePath(String datasourceFilePath) {
        this.datasourceFilePath = datasourceFilePath;
    }

    public String getReportFilePath() {
        return reportFilePath;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }

    @Override
    public String toString() {
        return "ReportInfo{" +
                "id='" + id + '\'' +
                ", reportName='" + reportName + '\'' +
                ", datasourceFilePath='" + datasourceFilePath + '\'' +
                ", reportFilePath='" + reportFilePath + '\'' +
                '}';
    }
}