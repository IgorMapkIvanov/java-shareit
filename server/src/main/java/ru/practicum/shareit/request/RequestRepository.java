package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> getRequestsByRequesterId(Long userId, PageRequest pageRequest);

    List<Request> getRequestsByRequesterIdNot(Long userId, PageRequest pageRequest);

    Optional<Request> getRequestsById(Long requestId);
}