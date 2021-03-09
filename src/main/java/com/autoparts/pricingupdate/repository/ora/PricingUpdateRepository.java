package com.autoparts.pricingupdate.repository.ora;

import com.autoparts.pricingupdate.model.ora.PricingUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PricingUpdateRepository extends JpaRepository<PricingUpdate, Long> {
    List<PricingUpdate> findByUpdatedTimestamp(Date date);
    List<PricingUpdate> findAll();
}