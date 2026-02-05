package pl.factorymethod.rada.announcements.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.factorymethod.rada.model.Announcement;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    /**
     * Find all announcements for a specific user
     */
    @Query(
            value = """
                    SELECT a.*
                    FROM announcements a
                    JOIN users u ON u.id = a.user_id
                    WHERE u.enabled = true
                      AND u.expired = false
                      AND u.deleted = false
                      AND u.public_id = :userId
                    ORDER BY a.published_at DESC
                    """,
            nativeQuery = true)
    Slice<Announcement> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Find unread announcements for a specific user
     */
    @Query(
            value = """
                    SELECT a.*
                    FROM announcements a
                    JOIN users u ON u.id = a.user_id
                    WHERE u.enabled = true
                      AND u.expired = false
                      AND u.deleted = false
                      AND u.public_id = :userId
                      AND a.is_read = false
                    ORDER BY a.published_at DESC
                    """,
            nativeQuery = true)
    Slice<Announcement> findUnreadByUserId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Find read announcements for a specific user
     */
    @Query(
            value = """
                    SELECT a.*
                    FROM announcements a
                    JOIN users u ON u.id = a.user_id
                    WHERE u.enabled = true
                      AND u.expired = false
                      AND u.deleted = false
                      AND u.public_id = :userId
                      AND a.is_read = true
                    ORDER BY a.published_at DESC
                    """,
            nativeQuery = true)
    Slice<Announcement> findReadByUserId(@Param("userId") UUID userId, Pageable pageable);

   Optional<Announcement> findByPublicId(UUID publicId);
}
