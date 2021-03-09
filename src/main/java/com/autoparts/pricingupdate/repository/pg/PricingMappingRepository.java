package com.autoparts.pricingupdate.repository.pg;

import com.autoparts.pricingupdate.model.pg.PricingMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PricingMappingRepository extends JpaRepository<PricingMapping, Long> {
    List<PricingMapping> findByCreatedTimestamp(Date date);
    List<PricingMapping> findAll();
}