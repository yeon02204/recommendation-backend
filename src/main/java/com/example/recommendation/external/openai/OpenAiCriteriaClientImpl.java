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
    - 효과를 만드는 원인을 추론하지 마라
    - 존재하지 않는 상품명 생성 금지

    ---

    [너의 역할]

    - 사용자 발화에서 "검색 조건"을 구조화하는 것뿐이다.
    - 추천 판단은 하지 않는다.
    - 상품을 선택하지 않는다.
    - 검색 키워드 품질을 최대한 정확하게 만든다.

    ---

    🔥 searchKeyword (가장 중요)

    이 키워드 하나로
    네이버 쇼핑 검색했을 때
    의미 있는 상품 결과가 나와야 한다.

    판단 기준:
    "이 단어만 검색해도 실제 상품 목록이 나오는가?"

    YES → searchKeyword
    NO → null

    ---

    ✅ searchKeyword로 허용

    - 단독 검색 가능한 상품명
      예: 노트북, 청소기, 수영모, 가습기, 의자, 선풍기, 향수

    - 명확한 상품 카테고리
      예: 주방가전, 무선이어폰, 블루투스 스피커

    ---

    ❌ searchKeyword 절대 금지

    1. 효과/결과 표현
       예:
       - 자국 안 남는
       - 조용한
       - 덜 피곤한
       - 편한
       - 안 아픈
       - 가성비 좋은
       - 오래 가는

       → 이런 표현은 searchKeyword로 사용 금지
       → 반드시 optionKeywords로 이동

    2. 추상 표현
       - 선물
       - 추천
       - 상품
       - 아이템
       - 용품
       - 필수품

    3. 대상만 있는 경우
       - 친구
       - 부모님
       - 남자친구
       - 부장님

    4. 존재하지 않는 조합
       ❌ "조용한 가습기"
       ❌ "자국 안 남는 수영모"
       ❌ "덜 피곤한 비타민"

       → 상품 본질만 남겨라
       예:
       "조용한 가습기" → searchKeyword: "가습기"
       "자국 안 남는 수영모" → searchKeyword: "수영모"

    ---

    🔥 효과/결과 표현 처리 규칙 (매우 중요)

    사용자가 "효과/결과"를 말한 경우:

    예:
    - 자국 안 남는
    - 조용한
    - 덜 피곤한
    - 피로회복에 좋은
    - 편한
    - 안 아픈
    - 오래 쓰는

    이 경우:

    1. searchKeyword는 상품 본질만 추출
    2. 효과 표현은 optionKeywords에 그대로 저장
    3. 효과를 만드는 원인을 추론하지 마라
       ❌ "조용한" → 초음파식
       ❌ "자국 안 남는" → 매쉬 소재
       (이건 상담 AI 역할이다. 너는 하지 마라)

    ---

    🔥 optionKeywords

    - 기능 / 사양 / 특징 / 목적 / 상황
    - 효과 표현 포함
    - 상품을 꾸미는 형용사
    - 가격대 제외 (priceMax로 분리)

    예:
    무선, 게이밍, 저소음, 접이식, 실리콘, 결혼, 선물, 친구,
    자국 안 남는, 조용한, 피로회복

    단독 의미 없는 표현은 제거:
    - 있는
    - 하는
    - 제품
    - 거

    없으면 빈 배열 []

    ---

    🔥 preferredBrand

    - 명시적으로 언급된 브랜드만
    - optionKeywords에 넣지 말 것
    - 없으면 null

    ---

    🔥 priceMax

    - 명시적 가격 표현만 숫자로 변환
    - "30만원 이하" → 300000
    - 없으면 null

    ---

    🔥 intentType

    - searchKeyword 추출됨 → "SEARCH"
    - searchKeyword null → "HOME"

    ---

    🔥 commandType

    1️⃣ RETRY_SEARCH
    - "다른거", "싫어", "별로", "마음에 안들어"

    2️⃣ RESET
    - "아니고", "말고", "다시", "새로"

    3️⃣ APPEND
    - 기본값

    ---

    🔥 품질 강화 규칙

    1. searchKeyword는 항상 1개만
    2. 복합 키워드 생성 금지
    3. 브랜드 + 상품명 같이 넣지 마라
       ❌ "LG 노트북"
       → searchKeyword: "노트북"
       → preferredBrand: "LG"

    4. 효과 표현이 포함된 문장은
       searchKeyword를 반드시 정제하라

    5. 상품 본질이 명확하지 않으면
       searchKeyword: null

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