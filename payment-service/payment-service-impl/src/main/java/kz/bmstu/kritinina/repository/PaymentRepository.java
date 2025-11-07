package kz.bmstu.kritinina.repository;

import kz.bmstu.kritinina.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    //@Query("SELECT * FROM payment WHERE payment_uid = :paymentUid")
    Optional<Payment> findByPaymentUid(UUID paymentUid);

    //@Query("")
    List<Payment> findAllByPaymentUidIn(List<UUID> paymentUids);
}
