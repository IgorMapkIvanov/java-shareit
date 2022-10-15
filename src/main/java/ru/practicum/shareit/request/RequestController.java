package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public RequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                                     @PathVariable @Positive Long requestId) {
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
    public List<RequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                                        @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
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
    public List<RequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                                           @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
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
    public RequestDto addRequest(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                                 @RequestBody @Validated(value = Create.class) RequestDto requestDto) {
        log.info("CONTROLLER: Запрос на добавление нового запроса: {} от пользователя с ID = {}.", requestDto, userId);
        return requestService.addRequest(userId, requestDto);
    }
}