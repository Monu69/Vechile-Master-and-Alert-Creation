package com.elogist.vehicle_master_and_alert_creation.models.dto;

import com.elogist.vehicle_master_and_alert_creation.models.IssueConstraints;
import com.elogist.vehicle_master_and_alert_creation.models.TicketProperties;
import com.elogist.vehicle_master_and_alert_creation.models.alerts.Alerts;
import com.elogist.vehicle_master_and_alert_creation.services.ATDataFetchService;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Data
@DynamicUpdate
@NoArgsConstructor
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class

)

public class Issues {

    private static final Logger LOGGER = LoggerFactory.getLogger(Issues.class);

    private Integer id;

    @Type(type = "list-array")
    private List<Integer> benchmark;

    @Type(type = "list-array")
    private List<Integer> consignees;

    @Type(type = "list-array")
    private List<Integer> destinations;

    @Type(type = "list-array")
    private List<Integer> transporters;

    @Type(type = "list-array")
    private List<Integer> vehicles;

    @Type(type = "list-array")
    private List<Integer> groups;

    @Type(type = "list-array")
    private List<Integer> sources;

    private String benchmarks;

    private LocalTime nightStart = LocalTime.of(23,0);
    private LocalTime nightEnd = LocalTime.of(5,0);

    public Issues(Integer id,List<Integer> benchmark, List<Integer> consignees, List<Integer> destinations, List<Integer> transporters, List<Integer> vehicles, List<Integer> groups, List<Integer> sources, String benchmarks){
        this.id = id;
        this.benchmark = benchmark;
        this.consignees = consignees;
        this.destinations = destinations;
        this.transporters = transporters;
        this.vehicles = vehicles;
        this.groups = groups;
        this.sources = sources;
        this.benchmarks = benchmarks;
    }

    public Issues( Integer id, List<Integer> benchmark, String benchmarks){
        this.id = id;
        this.benchmark = benchmark;
        this.benchmarks = benchmarks;
        this.consignees = new ArrayList<>();
        this.destinations = new ArrayList<>();
        this.transporters = new ArrayList<>();
        this.vehicles = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.sources = new ArrayList<>();
    }

    public Issues(TicketProperties ticketProperties, IssueConstraints issueConstraints){
        List<Integer> benchmarks = new ArrayList<>();
        if(ticketProperties.getBenchmark() != null) {
            benchmarks.add(ticketProperties.getBenchmark());
        }
        if(ticketProperties.getBenchmark2() != null) {
            benchmarks.add(ticketProperties.getBenchmark2());
        }

        if(issueConstraints != null) {
            this.id = ticketProperties.getId();
            this.benchmark = benchmarks;
            this.benchmarks = ticketProperties.getBenchmarks();
            this.consignees = issueConstraints.getConsignees();
            this.destinations = issueConstraints.getDestinations();
            this.transporters = issueConstraints.getTransporters();
            this.vehicles = issueConstraints.getVehicles();
            this.groups = issueConstraints.getGroups();
            this.sources = issueConstraints.getSources();
        }
        else{
            this.id = ticketProperties.getId();
            this.benchmark = benchmarks;
            this.benchmarks = ticketProperties.getBenchmarks();
            this.consignees = new ArrayList<>();
            this.destinations = new ArrayList<>();
            this.transporters = new ArrayList<>();
            this.vehicles = new ArrayList<>();
            this.groups = new ArrayList<>();
            this.sources = new ArrayList<>();
        }
    }

    public Alerts.Benchmarks getFinalBenchmark(Alerts alerts)  {


        try {
            Alerts.Benchmarks benchmarks1 = alerts.getBenchmarks(benchmarks);
            Field[] fields = benchmarks1.getClass().getDeclaredFields();
            HashMap<String, Object> fieldValues = new HashMap<String, Object>();
            for (Field field : fields) {
                field.setAccessible(true);
                fieldValues.put(field.getName(), field.get(benchmarks1));
            }
            for (String key : fieldValues.keySet()) {
                if ((!key.equals("this$0")) && fieldValues.get(key) == null) {
                    return alerts.getDefaultBenchmark();
                }
            }
            return benchmarks1;
        }
        catch (Exception e){
            LOGGER.warn("Error while fetching Benchmark for property: "+this.id+" .So picking default... Error : "+e.getMessage());
            return alerts.getDefaultBenchmark();
        }
    }
}

