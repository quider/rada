package pl.factorymethod.rada.repository;

import org.springframework.data.domain.Page;
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
    @Query("SELECT a FROM Announcement a WHERE a.user.id = :userId ORDER BY a.publishedAt DESC")
    Page<Announcement> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find unread announcements for a specific user
     */
    @Query(
            value = """
                    SELECT a.*
                    FROM announcements a
                    JOIN users u ON u.id = a.user_id
                    JOIN students s ON s.id = u.student_id
                    JOIN classes c ON c.id = s.class_id
                    JOIN schools sc ON sc.id = c.school_id
                    WHERE u.enabled = true
                      AND u.expired = false
                      AND u.deleted = false
                      AND u.public_id = :userId
                      AND a.is_read = false
                    ORDER BY a.published_at DESC
                    """,
            nativeQuery = true)
    Slice<Announcement> findUnreadByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find read announcements for a specific user
     */
    @Query("SELECT a FROM Announcement a WHERE a.user.publicId = :userId AND a.read = true ORDER BY a.publishedAt DESC")
    Page<Announcement> findReadByUserId(@Param("userId") Long userId, Pageable pageable);
}
