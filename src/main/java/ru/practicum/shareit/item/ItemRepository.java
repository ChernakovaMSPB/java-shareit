package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(" select i from Items i " +
            "where upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%'))")
    List<Item> search(String text);
}