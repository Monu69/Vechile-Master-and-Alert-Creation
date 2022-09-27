package com.elogist.vehicle_master_and_alert_creation.models.alerts;


import com.elogist.vehicle_master_and_alert_creation.models.Enums.StateBenchmarkEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.io.IOException;
import java.sql.Array;
import java.util.List;
import java.util.Map;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fo_issue_types",schema = "foadmin")
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
public class    AlertType {


    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "pri_type")
    private Integer priType;

    @Column(name = "sec_type1")
    private Integer secType1;

    @Column(name = "sec_type2")
    private Integer secType2;

    @Column(name = "unit_type")
    private String unitType;

    @Column(name = "unit2_type")
    private String unit2Type;

    @Column(name = "show_type")
    private String showType;

    @Column(name = "show_type2")
    private String showType2;

    @Column(name = "params")
    private String params;

    @Column(name = "description")
    private String description;

    @Type(type = "jsonb")
    @Column(name = "benchmarks",columnDefinition = "jsonb")
    private List<BenchMarkDetails> benchmark;

}