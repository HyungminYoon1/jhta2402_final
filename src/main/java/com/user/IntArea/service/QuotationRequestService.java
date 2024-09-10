package com.user.IntArea.service;

import com.user.IntArea.common.utils.SecurityUtil;
import com.user.IntArea.dto.member.MemberDto;
import com.user.IntArea.dto.quotation.QuotationInfoDto;
import com.user.IntArea.dto.quotationRequest.QuotationRequestDto;
import com.user.IntArea.dto.quotationRequest.QuotationRequestInfoDto;
import com.user.IntArea.dto.solution.SolutionDto;
import com.user.IntArea.dto.solution.SolutionForQuotationRequestDto;
import com.user.IntArea.entity.*;
import com.user.IntArea.entity.enums.QuotationProgress;
import com.user.IntArea.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationRequestService {

    private final QuotationRequestRepository quotationRequestRepository;
    private final SolutionRepository solutionRepository;
    private final MemberRepository memberRepository;
    private final PortfolioRepository portfolioRepository;
    private final CompanyRepository companyRepository;
    private final QuotationRepository quotationRepository;
    private final QuotationService quotationService;
    private final RequestSolutionRepository requestSolutionRepository;
    private final RequestSolutionService requestSolutionService;
    private final SolutionService solutionService;

    @Transactional
    public QuotationRequestDto createQuotationRequest(QuotationRequestDto requestDto) {

        // Member 및 Portfolio 조회
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new NoSuchElementException(""));
        Portfolio portfolio = portfolioRepository.findById(requestDto.getPortfolioId())
                .orElseThrow(() -> new NoSuchElementException(""));

        // QuotationRequest 엔티티 생성 및 저장
        QuotationRequest quotationRequest = QuotationRequest.builder()
                .member(member)
                .portfolio(portfolio)
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .build();
        quotationRequestRepository.save(quotationRequest);

        // Solution 엔티티 생성 및 저장
        List<SolutionDto> solutionDtos = requestDto.getSolutions();
        List<Solution> solutions = new ArrayList<>();
        for (SolutionDto solutionDto : solutionDtos) {
            Solution solution = Solution.builder()
                    .title(solutionDto.getTitle())
                    .description(solutionDto.getDescription())
                    .portfolio(portfolio)
                    .price(solutionDto.getPrice())
                    .build();
            solutionRepository.save(solution);

            // RequestSolution 엔티티 생성 및 저장
            RequestSolution requestSolution = RequestSolution.builder()
                    .quotationRequest(quotationRequest)
                    .solution(solution)
                    .build();
            solutions.add(solution);
        }

        // 반환할 DTO 생성
        QuotationRequestDto responseDto = new QuotationRequestDto();
        responseDto.setMemberId(requestDto.getMemberId());
        responseDto.setPortfolioId(requestDto.getPortfolioId());
        responseDto.setTitle(requestDto.getTitle());
        responseDto.setDescription(requestDto.getDescription());
        responseDto.setSolutions(solutionDtos);

        return responseDto;
    }

    @Transactional(readOnly = true)
    public QuotationRequestDto getQuotationRequest(UUID id) {
        QuotationRequest quotationRequest = quotationRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(""));

        // DTO 변환
        return QuotationRequestDto.builder()
                .memberId(quotationRequest.getMember().getId())
                .portfolioId(quotationRequest.getPortfolio().getId())
                .title(quotationRequest.getTitle())
                .description(quotationRequest.getDescription())
                .solutions(quotationRequest.getRequestSolutions().stream()
                        .map(rs -> SolutionDto.builder()
                                .title(rs.getSolution().getTitle())
                                .description(rs.getSolution().getTitle())
                                .price(rs.getSolution().getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public QuotationRequestDto updateQuotationRequest(UUID id, QuotationRequestDto requestDto) {
        QuotationRequest quotationRequest = quotationRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(""));

        // 업데이트
        quotationRequest.setTitle(requestDto.getTitle());
        quotationRequest.setDescription(requestDto.getDescription());

        // 기존 솔루션 삭제 후 새로운 솔루션 추가
        quotationRequest.getRequestSolutions().clear();
        List<SolutionDto> solutionDtos = requestDto.getSolutions();
        for (SolutionDto solutionDto : solutionDtos) {
            Solution solution = Solution.builder()
                    .title(solutionDto.getTitle())
                    .description(solutionDto.getTitle())
                    .portfolio(quotationRequest.getPortfolio())
                    .price(solutionDto.getPrice())
                    .build();
            solutionRepository.save(solution);

            RequestSolution requestSolution = RequestSolution.builder()
                    .quotationRequest(quotationRequest)
                    .solution(solution)
                    .build();
            quotationRequest.getRequestSolutions().add(requestSolution);
        }
        return requestDto;
    }


    @Transactional
    public void deleteQuotationRequest(UUID id) {
        QuotationRequest quotationRequest = quotationRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(""));
        quotationRequestRepository.delete(quotationRequest);
    }

    @Transactional(readOnly = true)
    public List<QuotationRequestDto> getAllQuotationRequests() {
        List<QuotationRequest> quotationRequests = quotationRequestRepository.findAll();
        return quotationRequests.stream()
                .map(quotationRequest -> QuotationRequestDto.builder()
                        .memberId(quotationRequest.getMember().getId())
                        .portfolioId(quotationRequest.getPortfolio().getId())
                        .title(quotationRequest.getTitle())
                        .description(quotationRequest.getDescription())
                        .solutions(quotationRequest.getRequestSolutions().stream()
                                .map(rs -> SolutionDto.builder()
                                        .title(rs.getSolution().getTitle())
                                        .description(rs.getSolution().getDescription())
                                        .price(rs.getSolution().getPrice())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }


    // 현재 로그인한 멤버가 관리하는 회사 확인
    private Company getCompanyOfMember() {
        MemberDto memberDto = SecurityUtil.getCurrentMember().orElseThrow(() -> new NoSuchElementException("로그인을 해주세요."));
        String email = memberDto.getEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("이메일에 매칭되는 사용자 정보가 없습니다."));
        Company company = companyRepository.getCompanyByMember(member).orElseThrow(() -> new NoSuchElementException("관리중인 업체가 없습니다."));
        return company;
    }

    private QuotationRequestInfoDto convertToQuotationRequestTotalInfoDto(QuotationRequest quotationRequest) {
        // 각 QuotationRequest에 대한 Quotation 및 RequestSolution 데이터를 가져와서 DTO로 변환
        List<QuotationInfoDto> quotationInfoDtos = quotationService.getQuotationInfoDtoListFrom(quotationRequest);
        List<SolutionForQuotationRequestDto> solutionDtos = solutionService.getSolutionListFor(quotationRequest);

        return new QuotationRequestInfoDto(quotationRequest, quotationInfoDtos, solutionDtos);
    }

    // (견적요청서를 작성한 사용자 권한) 사용자가 작성한 모든 견적요청서 출력
    public Page<QuotationRequestInfoDto> getAllQuotationRequestOfMember(Pageable pageable) {
        MemberDto memberDto = SecurityUtil.getCurrentMember().orElseThrow(() -> new NoSuchElementException("로그인을 해주세요."));
        String email = memberDto.getEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("이메일에 매칭되는 사용자 정보가 없습니다."));
        Page<QuotationRequest> quotationRequests = quotationRequestRepository.findAllByMember(member, pageable);
        return quotationRequests.map(QuotationRequestInfoDto::new);
    }

    // (견적요청서를 작성한 사용자 권한) [딸려있는 solution, quotation 정보 포함] 사용자가 작성한 모든 견적요청서 출력
    public Page<QuotationRequestInfoDto> getAllQuotationRequestTotalInfoOfMember(Pageable pageable) {
        MemberDto memberDto = SecurityUtil.getCurrentMember().orElseThrow(() -> new NoSuchElementException("로그인을 해주세요."));
        String email = memberDto.getEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("이메일에 매칭되는 사용자 정보가 없습니다."));
        Page<QuotationRequest> quotationRequests = quotationRequestRepository.findAllByMember(member, pageable);
        // QuotationRequestTotalInfoDto로 매핑
        return quotationRequests.map(this::convertToQuotationRequestTotalInfoDto);
    }

    // (견적요청서를 작성한 사용자 권한) 사용자가 작성한 견적요청서 중 특정한 진행상태(progress)만 선택 출력 출력 (progress 소팅)
    public Page<QuotationRequestInfoDto> getAllQuotationRequestOfMember(QuotationProgress progress, Pageable pageable) {
        MemberDto memberDto = SecurityUtil.getCurrentMember().orElseThrow(() -> new NoSuchElementException("로그인을 해주세요."));
        String email = memberDto.getEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("이메일에 매칭되는 사용자 정보가 없습니다."));
        Page<QuotationRequest> quotationRequests = quotationRequestRepository.findAllByMemberAndProgress(member, progress, pageable);
        return quotationRequests.map(QuotationRequestInfoDto::new);
    }

    // (견적요청서를 작성한 사용자 권한) [딸려있는 solution, quotation 정보 포함] 사용자가 작성한 견적요청서 중 특정한 진행상태(progress)만 선택 출력 출력 (progress 소팅)
    public Page<QuotationRequestInfoDto> getAllQuotationRequestTotalInfoDtoOfMember(QuotationProgress progress, Pageable pageable) {
        MemberDto memberDto = SecurityUtil.getCurrentMember().orElseThrow(() -> new NoSuchElementException("로그인을 해주세요."));
        String email = memberDto.getEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("이메일에 매칭되는 사용자 정보가 없습니다."));
        Page<QuotationRequest> quotationRequests = quotationRequestRepository.findAllByMemberAndProgress(member, progress, pageable);
        // QuotationRequestTotalInfoDto로 매핑
        return quotationRequests.map(this::convertToQuotationRequestTotalInfoDto);
    }

    public QuotationRequest findById(UUID id) {
        return quotationRequestRepository.findById(id).orElse(null);
    }

    // seller

    // (seller) 회사 관리자가 받은 모든 견적 요청서 출력
    public Page<QuotationRequestInfoDto> getAllQuotationRequestTowardCompany(Pageable pageable) {
        Company company = getCompanyOfMember();
        Page<QuotationRequest> quotationRequests = quotationRequestRepository.getAllQuotationRequestTowardCompany(company.getId(), pageable);
        return quotationRequests.map(QuotationRequestInfoDto::new);
    }

    // (seller) 회사 관리자가 받은 특정 진행상태(progress)의 견적 요청서 출력
    public Page<QuotationRequestInfoDto> getAllQuotationRequestTowardCompany(QuotationProgress progress, Pageable pageable) {
        Company company = getCompanyOfMember();
        Page<QuotationRequest> quotationRequests = quotationRequestRepository.getAllQuotationRequestTowardCompanySortedByProgress(company.getId(), progress, pageable);
        return quotationRequests.map(QuotationRequestInfoDto::new);
    }

    // admin

    // (admin) 특정 회사가 받은 모든 견적 요청서 출력
    public Page<QuotationRequestInfoDto> getAllQuotationRequestOfCompanyByAdmin(UUID companyId, Pageable pageable) {
        Page<QuotationRequest> quotationRequests = quotationRequestRepository.getAllQuotationRequestTowardCompanyByAdmin(companyId, pageable);
        return quotationRequests.map(QuotationRequestInfoDto::new);
    }

    // (admin) 특정 사용자가 작성한 모든 견적 요청서 출력
    public Page<QuotationRequestInfoDto> getAllQuotationRequestOfMemberByAdmin(Member member, Pageable pageable) {
        Page<QuotationRequest> quotationRequests = quotationRequestRepository.findAllByMember(member, pageable);
        Page<QuotationRequestInfoDto> quotationRequestInfoDtos = quotationRequests.map(QuotationRequestInfoDto::new);
        return quotationRequestInfoDtos;
    }

}
