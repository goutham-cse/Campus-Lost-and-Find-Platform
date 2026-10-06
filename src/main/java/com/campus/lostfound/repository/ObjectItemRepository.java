package com.campus.lostfound.repository;

import com.campus.lostfound.model.ItemCategory;
import com.campus.lostfound.model.ItemStatus;
import com.campus.lostfound.model.ItemType;
import com.campus.lostfound.model.ObjectItem;
import com.campus.lostfound.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectItemRepository extends JpaRepository<ObjectItem, Long> {
    List<ObjectItem> findByUserOrderByCreatedAtDesc(User user);
    List<ObjectItem> findByItemTypeAndStatusOrderByCreatedAtDesc(ItemType itemType, ItemStatus status);
    List<ObjectItem> findByItemTypeAndCategoryAndStatus(ItemType itemType, ItemCategory category, ItemStatus status);
    List<ObjectItem> findByItemType(ItemType itemType);
}
