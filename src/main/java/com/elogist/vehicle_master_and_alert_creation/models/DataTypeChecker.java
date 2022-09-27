package com.elogist.vehicle_master_and_alert_creation.models;


import com.elogist.vehicle_master_and_alert_creation.models.Enums.BenchmarkEnumDataType;
import com.elogist.vehicle_master_and_alert_creation.models.Enums.BenchmarkEnumUnit;
import lombok.Data;

@Data
public class DataTypeChecker extends RuntimeException {
    private String message;
    private BenchmarkEnumDataType benchmarkEnumDataType;
    private BenchmarkEnumUnit benchmarkEnumUnit;

    public DataTypeChecker(BenchmarkEnumDataType benchmarkEnumDataType) {
        this.benchmarkEnumDataType = benchmarkEnumDataType;
    }


    public static DataTypeChecker notString(Object dataType) {
        BenchmarkEnumDataType benchmarkEnumDataType = BenchmarkEnumDataType.INTEGER;
        DataTypeChecker userException = new DataTypeChecker(benchmarkEnumDataType);
        userException.message = dataType + " -> Not a valid Datatype";
        return userException;
    }
}

