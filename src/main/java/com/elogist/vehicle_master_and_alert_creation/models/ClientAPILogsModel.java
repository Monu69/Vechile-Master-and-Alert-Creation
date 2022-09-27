package com.elogist.vehicle_master_and_alert_creation.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity(name = "client_api_logs")
public class ClientAPILogsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "foid")
    private Integer foId;

    @Column(name = "api_type")
    private String apiType;

    @Column(name = "addtime")
    private LocalDateTime addTime;

    @Column(name = "res_code")
    private Integer resCode;

    @Column(name = "res_msg")
    private String resMsg;

    @Column(name = "pcode")
    private Integer pCode;

    @Column(name = "error_msg")
    private String errorMsg;

    public ClientAPILogsModel(Integer foId, String apiType, LocalDateTime addTime, Integer resCode, String resMsg){
        this.foId = foId;
        this.apiType = apiType;
        this.addTime = addTime;
        this.resCode = resCode;
        this.resMsg = resMsg;
    }

    public ClientAPILogsModel(Integer foId, String apiType, LocalDateTime addTime, Integer resCode, String resMsg, String errorMsg){
        this.foId = foId;
        this.apiType = apiType;
        this.addTime = addTime;
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.errorMsg = errorMsg;
    }

}
