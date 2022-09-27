package com.elogist.vehicle_master_and_alert_creation.repository.mssql;

import com.elogist.vehicle_master_and_alert_creation.config.PersistenceMSSQLConfiguration;
import com.elogist.vehicle_master_and_alert_creation.models.dto.ATAlertMappingDTO;
import com.elogist.vehicle_master_and_alert_creation.models.dto.ATRawDataString;
import com.elogist.vehicle_master_and_alert_creation.models.dto.ATRawData;
import com.elogist.vehicle_master_and_alert_creation.utils.DateAndTime;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ATGpsDataRepository {
    @Autowired
    PersistenceMSSQLConfiguration persistenceMSSQLConfiguration;

    public List<ATAlertMappingDTO> fetchDataFromAT(List<ATRawData> ATRawDataList, LocalDateTime startTime, LocalDateTime endTime) throws SQLException {

        List<ATRawDataString> rawDataList1 = new ArrayList<>();
        for(ATRawData ATRawData : ATRawDataList){
            ATRawDataString atRawDataString = new ATRawDataString(ATRawData);
            rawDataList1.add(atRawDataString);
        }
        String res = new Gson().toJson(rawDataList1);
        String stime1 = DateAndTime.localDateTimeToString(startTime), eTime1 = DateAndTime.localDateTimeToString(endTime);
        String query = "exec sp_elg_VehicleAlerts @vehjson = '" + res + "', @fromdt = '" + stime1 + "', @todt = '" + eTime1 + "'";

        /* mssql datasource connector */
        DataSource dataSource = persistenceMSSQLConfiguration.mssqlDataSource();
        PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(query);
        /* query execution, storing response in ResultSet */
        ResultSet resultSet = preparedStatement.executeQuery();
        List<ATAlertMappingDTO> ATAlertMappingDTOS = new ArrayList<>();
        /* iterate over ResultSet */
        while (resultSet.next()) {
            ATAlertMappingDTO ATAlertMappingDTO = new ATAlertMappingDTO();
            ATAlertMappingDTO.setLat(resultSet.getBigDecimal("lat"));
            ATAlertMappingDTO.setLng(resultSet.getBigDecimal("long"));
            ATAlertMappingDTO.setAtVid(resultSet.getInt("atvid"));
            ATAlertMappingDTO.setServiceId(resultSet.getInt("service_id"));
            ATAlertMappingDTO.setRegNo(resultSet.getString("vehicle_name"));
            ATAlertMappingDTO.setMessage(resultSet.getString("alert_message") + "##" + resultSet.getString("service_name") + "##" + resultSet.getTimestamp("alert_addtime"));
            ATAlertMappingDTO.setAlertAddTime(resultSet.getTimestamp("alert_addtime"));
            ATAlertMappingDTO.setAlertTime(resultSet.getTimestamp("alert_time"));
            ATAlertMappingDTO.setServiceName(resultSet.getString("service_name"));
            ATAlertMappingDTOS.add(ATAlertMappingDTO);
        }
        return ATAlertMappingDTOS;
    }
}
