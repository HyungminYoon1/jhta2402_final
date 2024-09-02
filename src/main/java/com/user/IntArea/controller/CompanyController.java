package com.user.IntArea.controller;

import com.user.IntArea.dto.company.CompanyPortfolioDetailDto;
import com.user.IntArea.dto.company.CompanyRequestDto;
import com.user.IntArea.dto.company.CompanyResponseDto;
import com.user.IntArea.dto.company.UnAppliedCompanyDto;
import com.user.IntArea.entity.Company;
import com.user.IntArea.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<?> create(CompanyRequestDto companyRequestDto) {

        companyService.create(companyRequestDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public CompanyPortfolioDetailDto getCompanyById() {

        return companyService.getCompanyById();
    }

    @GetMapping("/admin/unapply")
    public ResponseEntity<Page<UnAppliedCompanyDto>> getUnapply(@RequestParam int page, @RequestParam int size) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending());

        Page<UnAppliedCompanyDto> unAppliedCompanyDtoPage = companyService.getUnApply(pageable);

        return ResponseEntity.ok().body(unAppliedCompanyDtoPage);
    }

    @PatchMapping("/admin/apply/{id}")
    public ResponseEntity<?> apply(@PathVariable UUID id) {

        companyService.apply(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/list")
    public ResponseEntity<List<CompanyResponseDto>> getAllCompanies(@RequestBody CompanyRequestDto companyRequestDto) {
        List<CompanyResponseDto> companies = companyService.getAllCompanies(companyRequestDto);

        return ResponseEntity.ok(companies);
    }

    @GetMapping("/admin/list")
    public ResponseEntity<Page<CompanyResponseDto>> getAllCompany(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CompanyResponseDto> companyResponseDtoPage = companyService.getAllCompany(pageable);
        return ResponseEntity.ok().body(companyResponseDtoPage);
    }
}
