package sn.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.model.Notification;
import sn.model.Person;

import java.util.List;

/**
 * @author Andrey.Kazakov
 * @date 21.09.2020
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByToWhomAndIsReadedFalse(Person userReceiver, Pageable pageable);

    List<Notification> findAllByToWhomAndIsReadedFalse(Person userReceiver);
}
