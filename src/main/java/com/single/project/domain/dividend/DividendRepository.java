package com.single.project.domain.dividend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {

    void deleteAllByCompanyId(Long id);

    List<DividendEntity> findByCompanyId(Long id);
}
