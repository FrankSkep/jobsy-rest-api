package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.entity.ProviderDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderDocumentRepository extends JpaRepository<ProviderDocument, Integer> {
}
