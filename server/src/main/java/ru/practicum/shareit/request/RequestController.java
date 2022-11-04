package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

/**
 * Класс контроллер для пути "/requests".
 * <p>Взаимодействует с сервисным слоем {@link RequestService}
 *
 * @author Igor Ivanov
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class RequestController {
    private final RequestService requestService;

    // GET запросы

    /**
     * Метод обработки запроса на получение информации о запросе.
     *
     * @param userId    ID пользователя, передается через заголовок запроса "X-Sharer-User-Id".
     * @param requestId ID запроса, передается через переменную пути.
     * @return {@link RequestDto}
     */
    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long requestId) {
        log.info("CONTROLLER: Запрос на получение информации о запросе с ID = {}.", requestId);
        return requestService.getRequestById(userId, requestId);
    }

    /**
     * Метод обработки запроса на получение информации о всех запросах.
     *
     * @param userId ID пользователя, передается через заголовок запроса "X-Sharer-User-Id".
     * @param from   с какой записи вывести информацию, по умолчанию с первой.
     * @param size   количество записей на странице, по умолчанию 10.
     * @return {@link List} {@link RequestDto}
     */
    @GetMapping("/all")
    public List<RequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("CONTROLLER: Запрос на получение информации о всех запросах.");
        return requestService.getAllRequests(userId, pageRequest);
    }

    /**
     * Метод обработки запроса на получение информации о всех запросах пользователя.
     *
     * @param userId ID пользователя, передается через заголовок запроса "X-Sharer-User-Id".
     * @param from   с какой записи вывести информацию, по умолчанию с первой.
     * @param size   количество записей на странице, по умолчанию 10.
     * @return {@link List} {@link RequestDto}
     */
    @GetMapping
    public List<RequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(required = false, defaultValue = "0") Integer from,
                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("CONTROLLER: Запрос на получение информации о всех запросах пользователя с ID = {}.", userId);
        return requestService.getUserRequests(userId, pageRequest);
    }

    //POST запросы

    /**
     * Метод обработки запроса на добавление нового запроса.
     *
     * @param userId     ID пользователя, передается через заголовок запроса "X-Sharer-User-Id".
     * @param requestDto новый запрос, передается через тело запроса.
     * @return {@link RequestDto}
     */
    @PostMapping
    public RequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody RequestDto requestDto) {
        log.info("CONTROLLER: Запрос на добавление нового запроса: {} от пользователя с ID = {}.", requestDto, userId);
        return requestService.addRequest(userId, requestDto);
    }
}