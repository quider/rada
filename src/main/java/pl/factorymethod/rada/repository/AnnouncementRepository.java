package pl.factorymethod.rada.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.factorymethod.rada.model.Announcement;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {

    /**
     * Find all announcements for a specific user
     */
    @Query("SELECT a FROM Announcement a WHERE a.user.id = :userId ORDER BY a.publishedAt DESC")
    List<Announcement> findByUserId(@Param("userId") UUID userId);

    /**
     * Find unread announcements for a specific user
     */
    @Query("SELECT a FROM Announcement a WHERE a.user.id = :userId AND a.read = false ORDER BY a.publishedAt DESC")
    List<Announcement> findUnreadByUserId(@Param("userId") UUID userId);

    /**
     * Find read announcements for a specific user
     */
    @Query("SELECT a FROM Announcement a WHERE a.user.id = :userId AND a.read = true ORDER BY a.publishedAt DESC")
    List<Announcement> findReadByUserId(@Param("userId") UUID userId);
}
