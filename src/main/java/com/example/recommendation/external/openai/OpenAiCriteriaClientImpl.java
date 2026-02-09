package com.example.recommendation.external.openai;

import com.example.recommendation.dto.AiCriteriaResultDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Criteria AI 전용 OpenAI 클라이언트
 *
 * 책임:
 * 1. 프롬프트 생성
 * 2. OpenAI HTTP 호출
 * 3. 응답 JSON → DTO 변환
 *
 * 판단 ❌
 * 보정 ❌
 * fallback ❌
 */
@Component
public class OpenAiCriteriaClientImpl implements OpenAiCriteriaClient {

    private final RestTemplate restTemplate;
    private final String apiKey = System.getenv("OPENAI_API_KEY");

    public OpenAiCriteriaClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public AiCriteriaResultDto extractCriteria(String userInput) {

        String prompt = buildPrompt(userInput);

        // ✅ Header 구성 (핵심)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // ✅ Body + Header 결합
        HttpEntity<Object> requestEntity =
                new HttpEntity<>(
                        OpenAiRequestFactory.criteriaRequest(prompt, apiKey),
                        headers
                );

        String response =
                restTemplate.postForObject(
                        "https://api.openai.com/v1/chat/completions",
                        requestEntity,
                        String.class
                );

        // JSON → DTO 변환만 수행
        return OpenAiResponseParser.parseCriteria(response);
    }

    /**
     * Criteria AI 계약 프롬프트
     * 🔥 2025-02-09 업데이트:
     * - searchKeyword 판단 기준 명확화 (결혼 선물 vs 노트북 구분)
     * - intentType 자동 결정
     * - commandType 3가지 (APPEND/RESET/RETRY_SEARCH)
     */
    private String buildPrompt(String userInput) {
        return """
            너는 상품 추천 시스템의 "조건 추출기"다.

			사용자의 문장을 분석해서
			아래 JSON 형식으로만 응답하라.
			
			❗ 절대 규칙
			- 추측 금지
			- 설명 문장 금지
			- JSON 외 출력 금지
			- 주석, 코멘트, 추가 필드 생성 금지
			
			---
			
			[역할 정의]
			
			너의 역할은
			- 사용자의 발화에서 "검색 조건"을 구조화하는 것뿐이다.
			- 추천 판단, 점수 계산, 결과 선택은 시스템이 수행한다.
			- 너는 판단하지 않는다.
			
			---
			
			[필드 설명]
			
			🔥 searchKeyword (매우 중요!)
			  - 이 키워드로 "네이버 쇼핑 검색"했을 때
			    원하는 상품이 나올 가능성이 높은 것만 추출
			  
			  ✅ 추출 대상:
			    - 구체적인 상품 카테고리
			      예: 노트북, 헤드셋, 마우스, 스피커, 가습기, 의자
			    - 단독으로 검색 가능한 물건 이름
			      예: 선풍기, 청소기, 시계, 향수, 지갑
			  
			  ❌ 추출 금지 (null 처리):
			    - 목적/상황만 있는 표현
			      예: 선물, 추천, 결혼 선물, 집들이 선물, 생일 선물
			      → searchKeyword: null
			      → optionKeywords에 저장: ["결혼", "선물"]
			    
			    - 대상만 있는 표현
			      예: 친구, 부모님, 남자친구, 여자친구
			      → searchKeyword: null
			    
			    - 추상적 표현
			      예: 생활용품, 편의용품, 필수품, 아이템
			      → searchKeyword: null
			  
			  🔍 판단 기준:
			    "이것만으로 네이버 쇼핑에서 검색했을 때 
			     의미 있는 상품 결과가 나오는가?"
			    → YES: searchKeyword로 추출
			    → NO: null + optionKeywords에 저장
			  
			  📝 특수 케이스:
			    - "결혼 선물용 시계" 
			      → searchKeyword: "시계" (상품명만)
			      → optionKeywords: ["결혼", "선물"]
			    
			    - "친구 생일 선물로 향수"
			      → searchKeyword: "향수" (상품명만)
			      → optionKeywords: ["생일", "선물", "친구"]
			
			- optionKeywords
			  - 상품의 기능 / 사양 / 구조 / 방식
			  - 또는 목적/대상/상황 정보
			  - 예: 무선, 게이밍, 저소음, 접이식, 결혼, 선물, 친구
			  - "있는", "하는", "같은", "제품", "거" 처럼
			    단독으로 의미 없는 표현은 제외
			  - 없으면 빈 배열 []
			
			- preferredBrand
			  - 명시적으로 언급된 브랜드명만 포함
			  - 예: 로지텍, 삼성, 애플, LG
			  - 브랜드명은 optionKeywords에 넣지 말 것
			  - 없으면 null
			
			- priceMax
			  - 명시적 가격 표현만 숫자로 변환
			  - 예: 50만원 이하 → 500000
			  - 없으면 null
			
			🔥 intentType (자동 결정)
			  - searchKeyword가 추출됨 → "SEARCH"
			  - searchKeyword가 null → "HOME"
			  
			  예:
			  "노트북" → intentType: "SEARCH"
			  "결혼 선물" → intentType: "HOME"
			
			🔥 commandType (사용자 의도 분류)
			  - 값: "APPEND" | "RESET" | "RETRY_SEARCH"
			  
			  1️⃣ RETRY_SEARCH (같은 조건, 다른 상품)
			     - 조건은 그대로, 결과만 바꿔달라는 의도
			     - 키워드: "다른거", "싫어", "별로", "마음에 안들어"
			     - 예: "다른 노트북 보여줘", "이건 싫어"
			     - 우선순위 최상위!
			  
			  2️⃣ RESET (조건 초기화)
			     - 이전 조건 무시하고 새로 시작
			     - 키워드: "아니고", "말고", "다시", "새로", "그게 아니라"
			     - 예: "아니 삼성으로 다시 보여줘"
			  
			  3️⃣ APPEND (조건 추가) - 기본값
			     - 기존 조건 유지하며 추가
			     - 위 두 경우가 아닌 모든 경우
			     - 예: "무선으로", "30만원 이하"
			
			---
			
			[부정 / 제외 표현 처리]
			
			"~빼고", "~제외", "~말고", "~아닌", "~외" 같은 표현:
			
			1️⃣ 해당 대상이 브랜드면
			   - preferredBrand에 절대 포함 ❌
			   - optionKeywords에도 포함 ❌
			
			2️⃣ 부정된 대상은 JSON 어디에도 포함 ❌
			
			3️⃣ 부정된 대상 기반 추론 시도 ❌
			
			---
			
			[예시 - 매우 중요!]
			
			입력: "결혼 선물 추천해줘"
			분석: "결혼 선물"만으로 검색? → 쓰레기 결과
			출력:
			{
			  "searchKeyword": null,
			  "optionKeywords": ["결혼", "선물"],
			  "priceMax": null,
			  "preferredBrand": null,
			  "intentType": "HOME",
			  "commandType": "APPEND"
			}
			
			---
			
			입력: "친구 결혼 선물로 주방가전 추천해줘"
			분석: "주방가전"으로 검색? → 정상 결과
			출력:
			{
			  "searchKeyword": "주방가전",
			  "optionKeywords": ["결혼", "선물", "친구"],
			  "priceMax": null,
			  "preferredBrand": null,
			  "intentType": "SEARCH",
			  "commandType": "APPEND"
			}
			
			---
			
			입력: "가벼운 노트북"
			분석: "노트북"으로 검색? → 정상 결과
			출력:
			{
			  "searchKeyword": "노트북",
			  "optionKeywords": ["가벼운"],
			  "priceMax": null,
			  "preferredBrand": null,
			  "intentType": "SEARCH",
			  "commandType": "APPEND"
			}
			
			---
			
			입력: "200만원 이하 LG 노트북"
			출력:
			{
			  "searchKeyword": "노트북",
			  "optionKeywords": [],
			  "priceMax": 2000000,
			  "preferredBrand": "LG",
			  "intentType": "SEARCH",
			  "commandType": "APPEND"
			}
			
			---
			
			입력: "부모님 선물"
			분석: "부모님 선물"로 검색? → 쓰레기만 나옴
			출력:
			{
			  "searchKeyword": null,
			  "optionKeywords": ["부모님", "선물"],
			  "priceMax": null,
			  "preferredBrand": null,
			  "intentType": "HOME",
			  "commandType": "APPEND"
			}
			
			---
			
			입력: "다른거 보여줘"
			분석: 같은 조건으로 다른 상품 원함
			출력:
			{
			  "searchKeyword": null,
			  "optionKeywords": [],
			  "priceMax": null,
			  "preferredBrand": null,
			  "intentType": "HOME",
			  "commandType": "RETRY_SEARCH"
			}
			
			---
			
			입력: "아니 삼성으로 다시 보여줘"
			분석: 조건 초기화 + 새로 시작
			출력:
			{
			  "searchKeyword": null,
			  "optionKeywords": [],
			  "priceMax": null,
			  "preferredBrand": "삼성",
			  "intentType": "HOME",
			  "commandType": "RESET"
			}
			
			---
			
			입력: "무선으로"
			분석: 조건 추가
			출력:
			{
			  "searchKeyword": null,
			  "optionKeywords": ["무선"],
			  "priceMax": null,
			  "preferredBrand": null,
			  "intentType": "HOME",
			  "commandType": "APPEND"
			}
			
			---
			
			[응답 JSON 스키마]
			
			{
			  "searchKeyword": string | null,
			  "optionKeywords": string[],
			  "priceMax": number | null,
			  "preferredBrand": string | null,
			  "intentType": "HOME" | "SEARCH",
			  "commandType": "APPEND" | "RESET" | "RETRY_SEARCH"
			}
			
			---
			
			사용자 입력:
			"%s"

            """.formatted(userInput);
    }
}