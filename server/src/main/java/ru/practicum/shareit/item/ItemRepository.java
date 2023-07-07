package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findItemByOwnerId(Long ownerId, Pageable pageable);

    @Query(" select i from Items i " +
            "where upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%'))")
    Page<Item> search(String text, Pageable pageable);
}
