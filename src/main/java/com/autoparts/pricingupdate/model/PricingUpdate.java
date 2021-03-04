package com.autoparts.pricingupdate.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "pricing_update")
public class PricingUpdate implements Serializable {

    private static final long serialVersionUID = -2343243243242432341L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "ct_id")
    private String ctId;

    @Column(name = "pt_id")
    private String ptId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCtId() {
        return ctId;
    }

    public void setCtId(String ctId) {
        this.ctId = ctId;
    }

    public String getPtId() {
        return ptId;
    }

    public void setPtId(String ptId) {
        this.ptId = ptId;
    }
}
