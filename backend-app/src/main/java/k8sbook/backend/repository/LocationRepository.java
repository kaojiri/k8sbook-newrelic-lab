package k8sbook.backend.repository;

import k8sbook.backend.entity.LocationEntity;
import k8sbook.backend.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

    List<LocationEntity> findByRegion(RegionEntity region);

}
