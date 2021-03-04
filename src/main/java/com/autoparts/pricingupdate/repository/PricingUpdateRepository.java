package com.autoparts.pricingupdate.repository;

import com.autoparts.pricingupdate.model.PricingUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingUpdateRepository extends JpaRepository<PricingUpdate, Long> {
    List<PricingUpdate> findByFirstName(String FirstName);
    List<PricingUpdate> findAll();
}