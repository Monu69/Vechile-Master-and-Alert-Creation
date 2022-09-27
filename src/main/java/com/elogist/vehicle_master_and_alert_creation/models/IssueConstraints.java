package com.elogist.vehicle_master_and_alert_creation.models;


import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@DynamicUpdate
@Table(name = "fo_issue_contraints")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class

)
public class IssueConstraints implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Type(type = "list-array")
    @Column(name = "consignees",columnDefinition = "int[]")
    private List<Integer> consignees;


    @Type(type = "list-array")
    @Column(name = "destinations",columnDefinition = "int[]")
    private List<Integer> destinations;

    @Type(type = "list-array")
    @Column(name = "transporters",columnDefinition = "int[]")
    private List<Integer> transporters;

    @Type(type = "list-array")
    @Column(name = "vehicles",columnDefinition = "int[]")
    private List<Integer> vehicles;

    @Type(type = "list-array")
    @Column(name = "groups",columnDefinition = "int[]")
    private List<Integer> groups;

    @Type(type = "list-array")
    @Column(name = "sources",columnDefinition = "int[]")
    private List<Integer> sources;

   public IssueConstraints(){
        this.consignees = new ArrayList<>();
        this.destinations = new ArrayList<>();
        this.transporters = new ArrayList<>();
        this.vehicles = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.sources = new ArrayList<>();
    }


}
