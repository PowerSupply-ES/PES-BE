package com.powersupply.PES.notice.entity;

import com.powersupply.PES.member.entity.MemberEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "notice_table")
public class NoticeEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long noticeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "writerId", referencedColumnName = "memberId")
    private MemberEntity memberEntity;

    @Column(nullable = false)
    private String noticeTitle;
    @Lob
    @Column(nullable = false)
    private String noticeContent;
    private int noticeHit;
    private boolean isImportant;
    private boolean isDeleted;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
