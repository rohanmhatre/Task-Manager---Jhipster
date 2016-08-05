package com.fps.repository;

import com.fps.domain.Owner;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Owner entity.
 */
public interface OwnerRepository extends JpaRepository<Owner,Long> {

}
