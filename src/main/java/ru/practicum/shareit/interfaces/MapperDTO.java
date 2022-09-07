package ru.practicum.shareit.interfaces;

/**
 * Интерфейс Mapper определяет методы преобразования объекта класса <b>T</b> в DTO объекта класса <b>V</b> и наоборот.
 *
 * @param <T> объект класса
 * @param <V> DTO класса
 * @author Igor Ivanov
 */
public interface MapperDTO<T, V> {
    /**
     * Метод преоброзования объекта класса T в DTO объекта класса V.
     *
     * @param t объект класса
     * @return DTO объекта класса
     */
    V toDto(T t);

    /**
     * Метод преоброзования DTO объекта класса V в объект класса T.
     *
     * @param v DTO объекта класса
     * @return объект класса
     */
    T fromDto(V v);
}