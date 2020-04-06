package k8sbook.batch.repository;

import k8sbook.batch.entity.LocationEntity;
import k8sbook.batch.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.newrelic.api.agent.Trace;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    List<LocationEntity> findByRegion(RegionEntity region);

}
