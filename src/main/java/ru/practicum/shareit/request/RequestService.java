package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto getRequestById(Long userId, Long requestId);

    List<RequestDto> getAllRequests(Long userId, PageRequest pageRequest);

    List<RequestDto> getUserRequests(Long userId, PageRequest pageRequest);

    RequestDto addRequest(Long userId, RequestDto requestDto);
}
