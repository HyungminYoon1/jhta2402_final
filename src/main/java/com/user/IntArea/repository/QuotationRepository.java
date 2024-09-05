package com.user.IntArea.repository;

import com.user.IntArea.entity.Portfolio;
import com.user.IntArea.entity.Quotation;
import com.user.IntArea.entity.QuotationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuotationRepository extends JpaRepository<Quotation, UUID> {


    @Query("SELECT DISTINCT q from Quotation q left join QuotationRequest qr " +
            "on q.quotationRequest.id=qr.id where qr.id=:quotationRequestId and q.isAvailable = true")
    Optional<Quotation> getValidQuotationForQuotationRequest(UUID quotationRequestId);

    @Query("SELECT DISTINCT q FROM Quotation q " +
            "JOIN q.quotationRequest qr " +
            "JOIN qr.portfolio p " +
            "JOIN p.company c " +
            "WHERE c.id = :companyId and q.isAvailable=true and qr.isAvailable=true")
    List<Quotation> getAllQuotationOfCompany(UUID companyId);

    @Query("SELECT DISTINCT q FROM Quotation q " +
            "JOIN q.quotationRequest qr " +
            "JOIN qr.portfolio p " +
            "JOIN p.company c " +
            "WHERE c.id = :companyId")
    List<Quotation> getAllQuotationOfCompanyByAdmin(UUID companyId);


}
