package com.user.IntArea.entity;

import com.user.IntArea.entity.enums.Progress;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
public class Quotation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotationRequestId")
    private QuotationRequest quotationRequest;

    @Column
    private Long totalTransactionAmount;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column
    private String clientName; // 견적을 요청한 고객의 이름. 기본값은 quotationRequest에서 작성자의 이름을 따오고 없을 경우 이메일주소를 따옴

    @Column
    private String description; // 견적에 대한 간단한 설명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Progress quotationProgress; // 현재 진행상태

    @Column(nullable = false)
    boolean isDeleted; // 기본값 false. 회사관리자 또는 ADMIN에 의한 삭제를 의미함. isDeleted = true가 될 경우 시스템 관리자만 열람 가능.

    @Column(nullable = false)
    boolean isAvailable; // 기본값 true. 견적서의 효력을 의미함. 고객 또는 회사가 거래 취소시 false로 전환. 거래 체결 전 내용을 수정할 경우 Available = false 가 되고 새로운 quotation이 기존의 quotation을 대체. 회사 관리자 및 고객이 변동사항에 대한 기록으로서 열람 가능

    @Column(nullable = false)
    boolean isCustomerPaid; // 기본값 false. 고객의 비용 지불 여부를 의미함. true가 될 경우 주문서가 됨.

    @Column(nullable = false)
    boolean isContractCancelled; // 기본값 false. 거래가 취소되었는지 여부를 의미함.

    @Column(nullable = false)
    boolean isRefundNecessary; // 기본값 false. 환불의 필요성 여부를 의미함.

    @Column(nullable = false)
    boolean isContractTerminated; // 기본값 false. 계약의 불완전한 종료를 의미함. true가 될 경우 영수증이 되지 못하고 별도의 환불절차 필요

    @Column(nullable = false)
    boolean isContractCompleted; // 기본값 false. 계약 완료 여부를 의미함. true가 될 경우 영수증이 됨.


    @Builder
    public Quotation(QuotationRequest quotationRequest, Long totalTransactionAmount, Progress quotationProgress, boolean isDeleted, boolean isAvailable, boolean isContractCancelled, boolean isRefundNecessary, boolean isCustomerPaid, boolean isContractTerminated, boolean isContractCompleted) {
        this.quotationRequest = quotationRequest;
        this.totalTransactionAmount = totalTransactionAmount;
        this.clientName = quotationRequest.getMember().getUsername() != null ? quotationRequest.getMember().getUsername() : quotationRequest.getMember().getEmail();
        this.quotationProgress = quotationProgress != null ? quotationProgress : Progress.PENDING; // 기본값 설정
        this.isDeleted = isDeleted;
        this.isAvailable = isAvailable;
        this.isCustomerPaid = isCustomerPaid;
        this.isContractCancelled = isContractCancelled;
        this.isRefundNecessary = isRefundNecessary;
        this.isContractTerminated = isContractTerminated;
        this.isContractCompleted = isContractCompleted;
    }
}
