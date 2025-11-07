package kz.bmstu.kritinina.repository;

import kz.bmstu.kritinina.model.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByUsername(String username);
    Optional<Rental> findByUsernameAndRentalUid(String username, UUID rentalUid);
}
