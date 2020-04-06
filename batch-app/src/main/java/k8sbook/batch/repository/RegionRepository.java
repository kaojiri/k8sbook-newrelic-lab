package k8sbook.batch.repository;

import k8sbook.batch.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.newrelic.api.agent.Trace;

@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Integer> {

    Optional<RegionEntity> findByRegionName(String regionName);

}
