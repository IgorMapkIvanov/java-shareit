package ru.practicum.shareit.booking;

import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.end < current_timestamp and b.item.id = ?2 and not b.booker.id = ?1")
    List<Booking> getLastBooking(Long userId, Long id, Pageable pageable);

    @Query("select b from Booking b where b.start > current_timestamp and b.item.id = ?2 and not b.booker.id = ?1")
    List<Booking> getNextBooking(Long userId, Long itemId, Pageable pageable);

    Optional<Booking> findBookingById(Long id);

    List<Booking> findBookingsByBooker_IdOrderByStartDesc(Long bookerId, PageRequest pageRequest);

    @Query("select b from Booking b where b.booker.id = ?1 and b.item.id = ?2 and b.end < ?3 and b.status = ?4")
    List<Booking> findBookingsByBooker_IdAndItemIdAndEndBeforeAndStatus(Long bookerId,
                                                                        Long itemId,
                                                                        LocalDateTime end,
                                                                        BookingStatus status);

    List<Booking> findBookingsByBooker_IdAndStatusEqualsOrderByStartDesc(Long userId,
                                                                         BookingStatus bookingStatus,
                                                                         PageRequest pageRequest);

    List<Booking> findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                                   LocalDateTime start,
                                                                                   LocalDateTime end,
                                                                                   PageRequest pageRequest);

    List<Booking> findBookingsByBooker_IdAndStartBeforeAndEndBeforeOrderByStartDesc(Long bookerId,
                                                                                    LocalDateTime start,
                                                                                    LocalDateTime end,
                                                                                    PageRequest pageRequest);

    List<Booking> findBookingsByBooker_IdAndStartAfterAndEndAfterOrderByStartDesc(Long userId,
                                                                                  LocalDateTime start,
                                                                                  LocalDateTime end,
                                                                                  PageRequest pageRequest);

    List<Booking> findBookingsByItemOwnerIdOrderByStartDesc(Long userId, PageRequest pageRequest);

    List<Booking> findBookingsByItemOwnerIdAndStatusEqualsOrderByStartDesc(Long userId,
                                                                           BookingStatus rejected,
                                                                           PageRequest pageRequest);

    List<Booking> findBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                                     LocalDateTime start,
                                                                                     LocalDateTime end,
                                                                                     PageRequest pageRequest);

    List<Booking> findBookingsByItemOwnerIdAndStartBeforeAndEndBeforeOrderByStartDesc(Long userId,
                                                                                      LocalDateTime start,
                                                                                      LocalDateTime end,
                                                                                      PageRequest pageRequest);

    List<Booking> findBookingsByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(Long userId,
                                                                                    LocalDateTime start,
                                                                                    LocalDateTime end,
                                                                                    PageRequest pageRequest);

    void deleteById(@NonNull Long bookingId);
}