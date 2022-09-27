package com.elogist.vehicle_master_and_alert_creation.models;

import com.elogist.vehicle_master_and_alert_creation.models.Enums.BenchmarkEnumUnit;
import lombok.Data;

@Data
public class UnitChecker extends RuntimeException {
    private String message;
    private BenchmarkEnumUnit benchmarkEnumDataType;
    private BenchmarkEnumUnit benchmarkEnumUnit;

    public UnitChecker(BenchmarkEnumUnit benchmarkEnumUnit) {
        this.benchmarkEnumUnit = benchmarkEnumUnit;
    }


    public static UnitChecker notString(Object Unit) {
        BenchmarkEnumUnit benchmarkEnumUnit = BenchmarkEnumUnit.KILOMETER;
        UnitChecker userUnitException = new UnitChecker(benchmarkEnumUnit);
        userUnitException.message = Unit + " -> Not a valid unit";
        return userUnitException;
    }
}
