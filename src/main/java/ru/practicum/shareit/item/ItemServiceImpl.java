package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    /**
     * Метод получения списка всех вещей пользователя.
     *
     * @param userId ID пользователя.
     * @return {@link List} содержащий {@link ItemDtoWithBooking}
     */
    @Override
    public List<ItemDtoWithBooking> getAllItemsForOwnerWithId(Long userId) {
        log.info("SERVICE: Обработка запроса на получение списка с информацией всех вещей пользователя с ID = {}.", userId);

        checkUserIdInDbAndReturnUser(userId);

        List<ItemDtoWithBooking> itemDtoWithBookingList = itemRepository.getItemsByOwnerId(userId).stream()
                .map(ItemMapper::toItemDtoWithBooking)
                .peek(item -> {
                    setLastBooking(userId, item);
                    setNextBooking(userId, item);
                    setComments(item);
                })
                .collect(Collectors.toUnmodifiableList());

        log.info("SERVICE: Отправка списка с информацией всех вещей пользователя с ID = {}.", userId);
        return itemDtoWithBookingList;
    }

    /**
     * Метод получения информации о вещи.
     *
     * @param itemId ID пользователя.
     * @return {@link ItemDtoWithBooking}
     */
    @Override
    public ItemDtoWithBooking getItemById(Long userId, Long itemId) {
        log.info("SERVICE: Обработка запроса запроса на получение информации о вещи с ID = {}.", itemId);
        checkUserIdInDbAndReturnUser(userId);
        ItemDtoWithBooking item = ItemMapper.toItemDtoWithBooking(checkItemInDbAndReturnItem(itemId));
        setLastBooking(userId, item);
        setNextBooking(userId, item);
        setComments(item);

        log.info("SERVICE: Отправка информации о вещи с ID = {}.", itemId);
        return item;
    }

    /**
     * Метод обработки запроса на поиск вещи.
     *
     * @param text ID вещи, передается через параметр запроса.
     * @return {@link List} {@link ItemDto}
     */
    @Override
    public List<ItemDto> searchItemByText(String text) {
        log.info("SERVICE: Обработка запроса на поиск вещи в имени или описании содержащей текст: {}.", text);
        if (text.equals("")) {
            log.info("SERVICE: Отправка пустого списка. Строка поиска пустая.");
            return Collections.emptyList();
        } else {
            log.info("SERVICE: Отправка информации о вещи в имени или описании содержащей текст: {}.", text);
            return itemRepository.searchItemsByNameOrDescriptionContainingTextIgnoreCaseAndAvailable(text, true)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    /**
     * Метод обработки запроса на добавление новой вещи пользователя.
     *
     * @param itemDto объект класса {@link ItemDto}, передается через тело запроса.
     * @param userId  ID владельца вещи, передается через заголовок запроса "X-Sharer-User-Id".
     * @return объект класса {@link ItemDto}.
     */
    @Override
    @Transactional
    public ItemDto addItemForUserWithId(ItemDto itemDto, Long userId) {
        log.info("SERVICE: Обработка запроса на добавление новой вещи: {} для пользователя с ID = {}.", itemDto, userId);
        Item newItem = ItemMapper.fromDto(itemDto);
        newItem.setOwner(checkUserIdInDbAndReturnUser(userId));

        log.info("SERVICE: Новая вещь: {} для пользователя с ID = {} - добавлена.", itemDto, userId);
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    /**
     * Метод обработки запроса на обновление данных вещи.
     *
     * @param userId  ID владельца вещи, передается через заголовок запроса "X-Sharer-User-Id".
     * @param itemDto {@link ItemDto} с новыми значениями.
     * @return {@link ItemDto} обновленная информация о пользователе.
     */
    @Override
    @Transactional
    public ItemDto updateItemForUserWithId(ItemDto itemDto, Long userId) {
        log.info("SERVICE: Обработка запроса на обновление вещи с ID = {} пользователя с ID = {}. {}",
                itemDto.getId(), userId, itemDto);

        checkUserIdInDbAndReturnUser(userId);

        Item itemInDb = checkItemInDbAndReturnItem(itemDto.getId());
        if (itemInDb.getOwner().getId().equals(userId)) {
            copyFields(itemDto, itemInDb);
            itemRepository.save(itemInDb);
            return ItemMapper.toItemDto(itemInDb);
        } else {
            log.error("SERVICE: Пользователю с ID = {} не принадлежит вещь с ID = {}.", userId, itemDto.getId());
            throw new NotFoundException("Пользователю с ID = " + userId +
                    " не принадлежит вещь с ID = " + itemDto.getId() + ".");
        }
    }

    /**
     * Метод обработки запроса на удаление вещи.
     *
     * @param userId ID пользователя, передается через заголовок запроса "X-Sharer-User-Id".
     * @param itemId ID вещи, передается через переменную пути.
     */
    @Override
    @Transactional
    public void deleteItemForUserWithId(Long userId, Long itemId) {
        log.info("SERVICE: Обработка запроса на удаление вещи с ID = {}.", itemId);
        checkUserIdInDbAndReturnUser(userId);
        checkItemInDbAndReturnItem(itemId);
        itemRepository.deleteItemByOwnerIdAndId(userId, itemId);
        log.info("SERVICE: Вещи с ID = {} - удалена.", itemId);
    }

    /**
     * Метод обработки запроса на добавление комментария для вещи.
     *
     * @param commentDto объект класса {@link CommentDto}, передается через тело запроса.
     * @param authorId   ID владельца вещи, передается через заголовок запроса "X-Sharer-User-Id".
     * @param itemId     ID вещи, передается через переменную пути.
     * @return объект класса {@link CommentDto}.
     */
    @Override
    @Transactional
    public CommentDto addComment(Long authorId, CommentDto commentDto, Long itemId) {
        log.info("SERVICE: Обработка запроса на на добавление комментария: {}, пользователем с ID = {}, для вещи с ID = {}.", commentDto.getText(), authorId, itemId);
        User author = checkUserIdInDbAndReturnUser(authorId);
        Item item = checkItemInDbAndReturnItem(itemId);

        List<Booking> authorBookings =
                bookingRepository.findBookingsByBooker_IdAndItemIdAndEndBeforeAndStatus(
                        authorId,
                        itemId,
                        LocalDateTime.now(),
                        BookingStatus.APPROVED);

        if (authorBookings.size() > 0) {
            Comment comment = CommentMapper.fromDto(commentDto);
            comment.setCreated(LocalDateTime.now());
            comment.setAuthorName(author);
            comment.setItem(item);
            commentRepository.save(comment);
            log.info("SERVICE: Комментарий: {}, пользователем с ID = {}, для вещи с ID = {} - добавлен.",
                    commentDto.getText(), authorId, itemId);
            return CommentMapper.toDto(comment);
        } else {
            log.error("SERVICE: Пользователь с ID = {} не бронировал вещь с ID = {}.", authorId, itemId);
            throw new BadRequestException("Пользователь с ID = " + authorId + " не бронировал вещь с ID = " + itemId + ".");
        }
    }

    private void setLastBooking(Long userId, ItemDtoWithBooking item) {
        Pageable pageRequest = PageRequest.of(0, 1, Sort.by(("end")));
        List<Booking> bookings = bookingRepository.getLastBooking(userId, item.getId(), pageRequest);
        if (bookings.size() == 0) {
            item.setLastBooking(null);
        } else {
            item.setLastBooking(new ItemDtoWithBooking
                    .ItemBookingDto(bookings.get(0).getId(), bookings.get(0).getBooker().getId()));
        }
    }

    private void setNextBooking(Long usrId, ItemDtoWithBooking item) {
        Pageable pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("start")));
        List<Booking> bookings = bookingRepository.getNextBooking(usrId, item.getId(), pageRequest);
        if (bookings.size() == 0) {
            item.setNextBooking(null);
        } else {
            item.setNextBooking(new ItemDtoWithBooking
                    .ItemBookingDto(bookings.get(0).getId(), bookings.get(0).getBooker().getId()));
        }
    }

    private void setComments(ItemDtoWithBooking item) {
        List<CommentDto> comments = commentRepository.getComments(item.getId());
        item.setComments(comments);
    }

    private void copyFields(ItemDto itemDto, Item itemInDb) {
        if (itemDto.getName() != null && !itemDto.getName().equals(itemInDb.getName())) {
            log.info("SERVICE: Название вещи с ID = {} - обновлено.", itemDto.getId());
            log.info("Старое значение: {}.", itemInDb.getName());
            log.info("Новое значение: {}.", itemDto.getName());
            itemInDb.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().equals(itemInDb.getDescription())) {
            log.info("SERVICE: Описание вещи с ID = {} - обновлено.", itemDto.getId());
            log.info("Старое значение: {}.", itemInDb.getDescription());
            log.info("Новое значение: {}.", itemDto.getDescription());
            itemInDb.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(itemInDb.getAvailable())) {
            log.info("SERVICE: Доступность вещи с ID = {} - обновлена.", itemDto.getId());
            log.info("Старое значение: {}.", itemInDb.getAvailable());
            log.info("Новое значение: {}.", itemDto.getAvailable());
            itemInDb.setAvailable(itemDto.getAvailable());
        }
    }

    private User checkUserIdInDbAndReturnUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("SERVICE: Пользователь с ID = {} - не найден.", userId);
                    throw new NotFoundException("Пользователь с ID = " + userId + " не найден.");
                });
    }

    private Item checkItemInDbAndReturnItem(Long itemId) {
        return itemRepository.getItemById(itemId)
                .orElseThrow(() -> {
                    log.error("SERVICE: Вещь с ID = {} - не найдена.", itemId);
                    throw new NotFoundException("Вещь с ID = " + itemId + " не найдена.");
                });
    }
}