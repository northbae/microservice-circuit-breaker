package kz.bmstu.kritinina.repository;

import kz.bmstu.kritinina.model.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface
CarRepository extends JpaRepository<Car, Long> {
    Page<Car> findAllByAvailabilityTrue(Pageable pageable);

    List<Car> findAllByCarUidIn(List<UUID> carUids);

    Optional<Car> findByCarUid(UUID carUid);
}
