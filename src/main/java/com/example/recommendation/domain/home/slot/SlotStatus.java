package com.example.recommendation.domain.home.slot;

// 각 슬롯이 현재 어떤 상태인지 표현하는 상태 enum

public enum SlotStatus {
	EMPTY,          // 아직 안 물어봄
    ASKED,          // 질문은 했음
    USER_UNKNOWN,   // 사용자가 “모르겠어요”
    ANSWERED,       // 답변 받음 (임시)
    CONFIRMED       // 값 확정 (검색에 써도 됨)
}
