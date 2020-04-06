package k8sbook.backend.repository;

import k8sbook.backend.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Integer> {

    Optional<RegionEntity> findByRegionName(String regionName);

}
