package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
//@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    /**
     * Метод обработки запроса на получение информации о запросе по его ID.
     *
     * @param userId    ID пользователя.
     * @param requestId ID запроса.
     * @return {@link RequestDto}
     */
    @Override
    public RequestDto getRequestById(Long userId, Long requestId) {
        log.info("SERVICE: Обработка запроса на получение информации о запросе с ID = {}.", requestId);
        checkUserIdAndReturn(userId);

        Request request = requestRepository.getRequestsById(requestId).orElseThrow(() -> {
            log.error("SERVICE: Запрос с ID = {} - не найден.", requestId);
            throw new NotFoundException("Запрос с ID = " + requestId + " не найден.");
        });

        RequestDto requestDto = RequestMapper.toDto(request);
        setItems(requestDto);

        return requestDto;
    }

    /**
     * Метод обработки запроса на получение информации о всех запросах.
     *
     * @param userId      ID пользователя.
     * @param pageRequest информация о разбиении на страницы.
     * @return {@link List} {@link RequestDto}
     */
    @Override
    public List<RequestDto> getAllRequests(Long userId, PageRequest pageRequest) {
        log.info("SERVICE: Обработка запроса на получение информации о всех запросах.");
        return requestRepository.getRequestsByRequesterIdNot(userId, pageRequest).stream()
                .map(RequestMapper::toDto)
                .peek(this::setItems)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Метод обработки запроса на получение информации о запросах пользователя по его ID.
     *
     * @param userId      ID пользователя.
     * @param pageRequest информация о разбиении на страницы.
     * @return {@link List} {@link RequestDto}
     */
    @Override
    public List<RequestDto> getUserRequests(Long userId, PageRequest pageRequest) {
        checkUserIdAndReturn(userId);
        log.info("SERVICE: Обработка запроса на получение информации о запросах пользователя с ID = {}.", userId);
        List<Request> requests = requestRepository.getRequestsByRequesterId(userId, pageRequest);
        List<RequestDto> requestDtos = requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        requestDtos.forEach(this::setItems);
        return requestDtos;
    }

    /**
     * Метод обработки запроса на добавление нового запроса.
     *
     * @param userId     ID пользователя.
     * @param requestDto {@link RequestDto}.
     * @return {@link RequestDto}
     */
    @Override
//    @Transactional
    public RequestDto addRequest(Long userId, RequestDto requestDto) {
        User user = checkUserIdAndReturn(userId);

        Request request = RequestMapper.fromDto(requestDto);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        log.info("SERVICE: Обработка запроса на добавление нового запроса: {} от пользователя с ID = {}.", requestDto, userId);
        return RequestMapper.toDto(requestRepository.save(request));
    }

    private User checkUserIdAndReturn(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("SERVICE: Пользователь с ID = {} - не найден.", userId);
                    throw new NotFoundException("Пользователь с ID = " + userId + " не найден.");
                });
    }

    private void setItems(RequestDto requestDto) {
        List<Item> items = itemRepository.getItemsByRequestId(requestDto.getId());
        if (items.isEmpty()) {
            requestDto.setItems(Collections.emptyList());
        } else {
            requestDto.setItems(items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toUnmodifiableList()));
        }
    }
}