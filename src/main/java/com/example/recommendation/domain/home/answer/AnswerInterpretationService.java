package com.example.recommendation.domain.home.answer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.slot.DecisionSlot;

/**
 * 사용자 발화 의도 분류 서비스 (MVP)
 *
 * [역할]
 * - 사용자 입력 → AnswerInterpretation 변환
 * - 단순 패턴 매칭 (키워드, 정규식)
 *
 * [절대 금지]
 * - 슬롯 결정 ❌
 * - 상태 변경 ❌
 * - AI 호출 ❌ (문장 생성 아님)
 */
@Service
public class AnswerInterpretationService {
    
    // UNKNOWN 패턴
    private static final Pattern UNKNOWN_PATTERN = Pattern.compile(
            "모르|잘 몰|아무|상관없|그냥"
    );
    
    // REFUSAL 패턴
    private static final Pattern REFUSAL_PATTERN = Pattern.compile(
            "넘어갈게|괜찮아요|필요없어요|안 할래"
    );
    
    // CONTEXT_SHIFT 패턴
    private static final Pattern CONTEXT_SHIFT_PATTERN = Pattern.compile(
            "다른 거|처음부터|다시|리셋|초기화"
    );
    
    // NOISE 패턴
    private static final Pattern NOISE_PATTERN = Pattern.compile(
            "^(음|어|그|저|뭐)[\\.\\s]*$"
    );
    
    // 예산 패턴
    private static final Pattern BUDGET_PATTERN = Pattern.compile(
            "(\\d+)만원|예산.*?(\\d+)"
    );
    
    // 제약 키워드
    private static final Pattern CONSTRAINT_PATTERN = Pattern.compile(
            "비싸|부담|저렴|싸게|안[\\s]?되|피하|제외|말고"
    );
    
    // 선호 키워드
    private static final Pattern PREFERENCE_PATTERN = Pattern.compile(
            "좋아|선호|마음에|취향|스타일"
    );
    
    public AnswerInterpretation interpret(String userInput) {
        
        if (userInput == null || userInput.isBlank()) {
            return new AnswerInterpretation(
                    AnswerIntent.NOISE,
                    ""
            );
        }
        
        String normalized = userInput.trim();
        
        // 1. NOISE 체크
        if (NOISE_PATTERN.matcher(normalized).find()) {
            return new AnswerInterpretation(
                    AnswerIntent.NOISE,
                    normalized
            );
        }
        
        // 2. CONTEXT_SHIFT 체크
        if (CONTEXT_SHIFT_PATTERN.matcher(normalized).find()) {
            return new AnswerInterpretation(
                    AnswerIntent.CONTEXT_SHIFT,
                    normalized
            );
        }
        
        // 3. REFUSAL 체크
        if (REFUSAL_PATTERN.matcher(normalized).find()) {
            return new AnswerInterpretation(
                    AnswerIntent.REFUSAL,
                    normalized
            );
        }
        
        // 4. UNKNOWN 체크
        if (UNKNOWN_PATTERN.matcher(normalized).find()) {
            return new AnswerInterpretation(
                    AnswerIntent.UNKNOWN,
                    normalized
            );
        }
        
        // 5. ANSWER + 추가 신호 추출
        List<SecondarySignal> signals = extractSecondarySignals(normalized);
        
        return new AnswerInterpretation(
                AnswerIntent.ANSWER,
                normalized,
                signals
        );
    }
    
    /**
     * 복합 발화에서 추가 정보 추출
     * 예: "친구 결혼인데 비싼 건 부담스러워요"
     * → CONSTRAINT 신호 추출
     */
    private List<SecondarySignal> extractSecondarySignals(String input) {
        
        List<SecondarySignal> signals = new ArrayList<>();
        
        // 예산 추출
        Matcher budgetMatcher = BUDGET_PATTERN.matcher(input);
        if (budgetMatcher.find()) {
            String amount = budgetMatcher.group(1);
            if (amount != null) {
                signals.add(new SecondarySignal(
                        DecisionSlot.BUDGET,
                        amount + "만원"
                ));
            }
        }
        
        // 제약 추출
        if (CONSTRAINT_PATTERN.matcher(input).find()) {
            signals.add(new SecondarySignal(
                    DecisionSlot.CONSTRAINT,
                    input
            ));
        }
        
        // 선호 추출
        if (PREFERENCE_PATTERN.matcher(input).find()) {
            signals.add(new SecondarySignal(
                    DecisionSlot.PREFERENCE,
                    input
            ));
        }
        
        return signals;
    }
}